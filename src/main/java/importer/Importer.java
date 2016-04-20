package importer;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by langstra on 21-3-16.
 */
public class Importer {

    public static void main(String[] args) {
        MongoClient mongoClient = new MongoClient();
        MongoDatabase db = mongoClient.getDatabase("test");

        Scraper scraper = new Scraper();

        List<String> toParse = new ArrayList<String>();
        toParse.add("h1");
        toParse.add("a");
        for(String s : scraper.scrape(new File("1346.html"), toParse)) {
            System.out.println(s);
        }
    }
}
