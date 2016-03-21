package importer;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;

import java.io.File;

/**
 * Created by langstra on 21-3-16.
 */
public class Importer {

    public static void main(String[] args) {
        MongoClient mongoClient = new MongoClient();
        MongoDatabase db = mongoClient.getDatabase("test");

        Scraper s = new Scraper();
        System.out.println(s.scrape(new File("1346.html")));
    }
}
