package importer;

import org.apache.commons.io.IOUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by langstra on 21-3-16.
 */
public class Scraper {

    public Scraper() {
    }

    public List<String> scrape(File file, List<String> parseables) {

        String everything = "";
        List<String> scrapedList = new ArrayList<String>();
        try {
            FileInputStream inputStream = new FileInputStream(file);
            try {
                everything = IOUtils.toString(inputStream);
            } finally {
                inputStream.close();
            }
        } catch (Exception e) {}

        Document html = Jsoup.parse(everything);
        scrapedList.add(html.title());
        for(String parseable : parseables) {
            scrapedList.add(html.body().getElementsByTag(parseable).text());
        }

        return scrapedList;

    }

}
