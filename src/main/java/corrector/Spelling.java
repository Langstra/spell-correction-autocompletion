package corrector;

import org.apache.commons.io.FileUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class Spelling {

    private final HashMap<String, Integer> nWords = new HashMap<String, Integer>();
    private final HashMap<Pair<String, String>, Integer> predecessors = new HashMap<Pair<String, String>, Integer>();

    public Spelling(String file) throws IOException {
        BufferedReader in = new BufferedReader(new FileReader(file));
        Pattern p = Pattern.compile("\\w+");
        for (String temp = ""; temp != null; temp = in.readLine()) {
            Matcher m = p.matcher(temp.toLowerCase());
            while (m.find()) {
                nWords.put((temp = m.group()), nWords.containsKey(temp) ? nWords.get(temp) + 1 : 1);
            }

        }
        String[] words = FileUtils.readFileToString(new File(file)).split("\\s+");
        System.out.println(words.length);
        for (int i = 0; i < words.length; i++) {
            String current = words[i].toLowerCase();
            if (i != 0) {
                String previous = words[i - 1].toLowerCase();
                Pair predecessor = new Pair<String, String>(current, previous);
                predecessors.put(predecessor, predecessors.containsKey(predecessor) ? predecessors.get(predecessor) + 1 : 1);
            }
        }
        in.close();
    }

    /**
     * Get all word with edit distance of 1 from the given word
     * 1. del[x,y], count(xy was typed as x)
     * 2. add[x,y], count(x was typed as xy)
     * 3. sub[x,y], count(y was typed as x)
     * 4. rev[x,y], count(xy was typed as yx)
     *
     * @param word String
     * @return ArrayList<String> list of all words with edit distance 1
     */
    private final ArrayList<String> edits(String word) {
        ArrayList<String> result = new ArrayList<String>();
        for (int i = 0; i < word.length(); ++i) result.add(word.substring(0, i) + word.substring(i + 1));
        for (int i = 0; i < word.length() - 1; ++i)
            result.add(word.substring(0, i) + word.substring(i + 1, i + 2) + word.substring(i, i + 1) + word.substring(i + 2));
        for (int i = 0; i < word.length(); ++i)
            for (char c = 'a'; c <= 'z'; ++c)
                result.add(word.substring(0, i) + String.valueOf(c) + word.substring(i + 1));
        for (int i = 0; i <= word.length(); ++i)
            for (char c = 'a'; c <= 'z'; ++c) result.add(word.substring(0, i) + String.valueOf(c) + word.substring(i));
        return result;
    }

    public final String correct(String sentence) {
        String[] words = sentence.split("\\s+");
        ArrayList<HashMap<Double, String>> candidatesList = new ArrayList<HashMap<Double, String>>();
        for (int i = 0; i < words.length; i++) {
            HashMap<Double, String> candidates = new HashMap<Double, String>();

            if (nWords.containsKey(words[i])) {
                candidates.put(0.95 * nWords.get(words[i]) / nWords.size(), words[i]); //this is p(w|w)*p(w)
            }
            ArrayList<String> list = edits(words[i]);
            for (String s : list) {
                if (nWords.containsKey(s)) {
                    candidates.put((nWords.get(s) + 0.5) / list.size() * 1.0 * nWords.get(s) / nWords.size(), s); //this is p(x|w)*p(w), but not really since we do not use confusion matrices for spelling correction

                }
            }
            for (String s : list) {
                ArrayList<String> list_2 = edits(s);
                for (String w : list_2) {
                    if (nWords.containsKey(w)) {
                        candidates.put((nWords.get(w) + 0.5) / edits(s).size() * list.size() * 1.0 * nWords.get(w) / nWords.size(), w); //this is p(x|w)*p(w), but not really since we do not use confusion matrices for spelling correction
                    }
                }
            }
            if (candidates.size() == 0) {
                candidates.put(0.5 / nWords.size() * 1.0, words[i]);
            }
            candidatesList.add(candidates);
        }
        String end = "";

        HashMap<Double, String> sentence_candidates = new HashMap<Double, String>();
        if (candidatesList.size() > 1) {
            for (int i = 0; i < candidatesList.size(); i++) {
                HashMap<Double, String> word_candidates = new HashMap<Double, String>();
                if (i == 0) {
                    System.out.println();
                    for (Map.Entry<Double, String> entry : candidatesList.get(i).entrySet()) {
//                    System.out.println(entry.getValue() + "----->" + entry.getKey());
                        Pair p = new Pair<String, String>(words[i + 1], entry.getValue());
                        word_candidates.put(entry.getKey() * (predecessors.get(p) == null ? 0.5 : predecessors.get(p)), entry.getValue());
                    }
                } else if (i < candidatesList.size() - 1) {
                    for (Map.Entry<Double, String> entry : candidatesList.get(i).entrySet()) {
//                    System.out.println(entry.getValue() + "----->" + entry.getKey());
                        Pair p1 = new Pair<String, String>(entry.getValue(), words[i - 1]);
                        Pair p2 = new Pair<String, String>(words[i + 1], entry.getValue());
                        word_candidates.put(entry.getKey() * (predecessors.get(p1) == null ? 0.5 : predecessors.get(p1)) * (predecessors.get(p2) == null ? 0.5 : predecessors.get(p2)), entry.getValue());
                    }
                } else {
                    for (Map.Entry<Double, String> entry : candidatesList.get(i).entrySet()) {
                        Pair p = new Pair<String, String>(entry.getValue(), words[i - 1]);
//                    System.out.println(entry.getValue() + "----->" + entry.getKey() + "---------->" + predecessors.get(p));
                        word_candidates.put(entry.getKey() * (predecessors.get(p) == null ? 0.5 : predecessors.get(p)), entry.getValue());
                    }
                }

                String temp_sentence = "";
                for (int j = 0; j < candidatesList.size(); j++) {
                    if (i == j) {
                        temp_sentence += word_candidates.get(Collections.max(word_candidates.keySet()));
                    } else {
                        temp_sentence += words[j];
                    }
                    if (j != candidatesList.size() - 1) {
                        temp_sentence += " ";
                    }
                }

                sentence_candidates.put(Collections.max(word_candidates.keySet()), temp_sentence);
            }
        } else {
            sentence_candidates.put(1.0, candidatesList.get(0).get(Collections.max(candidatesList.get(0).keySet())));
        }

//        for (Map.Entry<Double, String> entry : sentence_candidates.entrySet()) {
//            System.out.println(entry.getValue() + "------------>" + entry.getKey());
//        }

        return sentence_candidates.get(Collections.max(sentence_candidates.keySet()));
    }

    public static void main(String args[]) throws IOException {
        System.out.println("Starting up, please wait...");
        Spelling utwente_spell = new Spelling("utwente.txt");
        Spelling english_spell = new Spelling("big.txt");
        System.out.println("Spell correctors ready");
//        try {
//            BufferedReader br =
//                    new BufferedReader(new InputStreamReader(System.in));
//
//            String input;
//
//            while ((input = br.readLine()) != null) {
//                System.out.println("Utwente: " + utwente_spell.correct(input.toLowerCase()));
//                System.out.println("English: " + english_spell.correct(input.toLowerCase()));
//            }
//
//        } catch (IOException io) {
//            io.printStackTrace();
//        }
        String[] incorrect = {"crossing boarders","agstuderen","alumniburea","application for phs","Breakthrouh with new generation robots","chemival engineering","contact Faculty of science and technolog","Busines Intelligence","fluoresence microscope","green eenrgy initiative","geust network","identifying oppurtunities","ill pooicy","law crimonolgy","djoerd hiemstera","fret bijkerk","Future hiigh tech","health believe model","henk van der klok","international buissines","wireless edoroam","where is trente located","vulnerability asessment coastal","vkgroepen universiteit twente","Victor van der Cheis","quantun electronica","publikaties","positive psycholgy","nirvana meratia","moniek duyvestijn"};
        String[] correct = {"crossing borders","afstuderen","alumnibureau","application for phd","Breakthrough with new generation robots","chemical engineering","contact Faculty of science and technology","Business Intelligence","fluorescence microscope","green energy initiative","guest network","identifying opportunities","ill policy","law criminology","djoerd hiemstra","fred bijkerk","Future High tech","health belief model","henk van der kolk","international business","wireless eduroam","where is twente located","vulnerability assessment coastal","vakgroepen universiteit twente","Victor van der Chijs","quantum electronics","publicaties","positive psychology","nirvana meratnia","monique duyvestijn"};

        int i_utwente_correct = 0;
        int i_english_correct = 0;
        int i_utwente_unchanged = 0;
        int i_english_unchanged = 0;
        for (int i = 0; i < incorrect.length; i++) {
            String utwente = utwente_spell.correct(incorrect[i].toLowerCase());
            if (utwente.equals(correct[i].toLowerCase())) {
                i_utwente_correct++;
                System.out.print("C - ");
            } else {
                if(utwente.equals(incorrect[i].toLowerCase())) {
                    i_utwente_unchanged++;
                }
                System.out.print("I - ");
            }
            System.out.println(utwente + "-------" + correct[i].toLowerCase());
            String english = english_spell.correct(incorrect[i].toLowerCase());
            if (english.equals(correct[i].toLowerCase())) {
                i_english_correct++;
                System.out.print("C - ");
            } else {
                if(english.equals(incorrect[i].toLowerCase())) {
                    i_english_unchanged++;
                }
                System.out.print("I - ");
            }

            System.out.println(english + "-------" + correct[i].toLowerCase());

        }
        System.out.println("Results: ");
        System.out.println("Utwente correct: " + i_utwente_correct);
        System.out.println("English correct: " + i_english_correct);

        System.out.println("Utwente correct: " + i_utwente_unchanged);
        System.out.println("English correct: " + i_english_unchanged);

    }

}