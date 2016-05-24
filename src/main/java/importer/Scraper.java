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

    /**
     * Returns text of all meta tags in the head with the given name attributes
     * @param file
     * @param nameAttributes
     * @return
     */
    public List<String> scrape(File file, List<String> nameAttributes) {

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
        for(String name : nameAttributes) {
            scrapedList.add(html.head().getElementsByAttributeValue("name", name).attr("content"));
        }

        return scrapedList;

    }

}
