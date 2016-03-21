package importer;

import org.apache.commons.io.IOUtils;

import java.io.*;

/**
 * Created by langstra on 21-3-16.
 */
public class Scraper {

    public Scraper() {
    }

    public String scrape(File file) {

        String everything = "";
        try {
            FileInputStream inputStream = new FileInputStream(file);
            try {
                everything = IOUtils.toString(inputStream);
            } finally {
                inputStream.close();
            }
        } catch (Exception e) {}
        return everything;
    }

}
