package importer;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by langstra on 21-3-16.
 */
public class Importer {

    public static void main(String[] args) throws IOException {
        Scraper scraper = new Scraper();
        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter("utwente.txt", true));


        List<String> toParse = new ArrayList<String>();
        toParse.add("eprints.creators_name");
        toParse.add("eprints.publisher");
        toParse.add("eprints.research_group_name");
        toParse.add("eprints.supervisors_name");
        toParse.add("eprints.citation");
        toParse.add("eprints.title");

        final File folder = new File("/home/langstra/Documents/Computer Science/Theme Course/purl.utwente.nl");
//
        List<File> files = listFilesForFolder(folder);
        String string = "";
        double num_files = files.size();
        double cur_file = 0.0;
        double percentage = -1.0;
        DateTime start = new DateTime();
        System.out.println(String.format("Total files to process: %f", num_files));
        int written_words = 0;
        PeriodFormatter formatter = new PeriodFormatterBuilder()
                .appendDays()
                .appendSuffix("d")
                .appendHours()
                .appendSuffix("h")
                .appendMinutes()
                .appendSuffix("m")
                .appendSeconds()
                .appendSuffix("s")
                .toFormatter();

        for(File f : files) {
            for (String s : scraper.scrape(f, toParse)) {
                bufferedWriter.write(s + " ");
                written_words++;
            }
            cur_file++;
            if(cur_file%1000 == 0) {
                bufferedWriter.flush();
                percentage = Math.floor(cur_file / num_files * 100.0);
                DateTime current_time = new DateTime();
                Duration duration = new Duration(start, current_time);

                System.out.println(String.format("%f %% of files processed", percentage));
                System.out.println(String.format("Current file: %f", cur_file));
                System.out.println(String.format("Time passed: %s", formatter.print(duration.toPeriod())));
//                System.out.println(String.format("Time remaining: %s \n\n", formatter.print(duration.toPeriod())));
            }
        }
        System.out.println("Written words: " + written_words);
        bufferedWriter.flush();
        bufferedWriter.close();
        System.out.println(string);
    }

    public static List<File> listFilesForFolder(final File folder) {
        List<File> files = new ArrayList<File>();
        for (final File fileEntry : folder.listFiles()) {
            if (fileEntry.isDirectory()) {
                files.addAll(listFilesForFolder(fileEntry));
            } else {
                files.add(fileEntry);
            }
        }
        return files;
    }
}
