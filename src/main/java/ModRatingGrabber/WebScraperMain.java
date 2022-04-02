package ModRatingGrabber;

import java.io.IOException;
import java.util.*;

public class WebScraperMain {
    public static void main(String[] args) throws IOException {
        WebScraper scraper = new WebScraper();
        Hashtable<String, String> links; // Key = URL, Datum = HTMLStatusCode
        String baseURL = "https://www.minecraftmods.com/page/";
        String targetURL = null;
        int i = 0;
        final int LAST_PAGE = 80;
        for (int j = 1; j != LAST_PAGE+1; j++) {
            System.out.println("Page: "+j+" added");
            targetURL = baseURL + j;
            scraper.doHTML(targetURL);
            //Find mod from homepage
            scraper.grabModsOnCurrentPage();

            //Find mod details
            //scraper.grabFromHTML("script","type");
        }

        for(String mod:scraper.mods)
        {
            System.out.println(++i+":"+mod);
        }
    }
}

