package ModRatingGrabber;

import java.io.IOException;

public class WebScraperMain {
    public static void main(String[] args) throws IOException {
        WebScraper scraper = new WebScraper();
        String base_url = "https://www.minecraftmods.com/page/";
        String target_url = null;
        int i = 0;
        int last_page = scraper.getLastPage(); // Gets last page index from Last button in HTML
        for (int j = 1; j != last_page+1; j++) {
            System.out.println("Page: "+j+" / "+last_page+" added. "+(int)(((float)j/(float)last_page)*100)+"%");
            target_url = base_url + j;
            scraper.doHTML(target_url);
            scraper.grabModsOnCurrentPage();
        }
        for(String mod:scraper.mods)
        {
            scraper.doHTML(mod);
            scraper.grabModRating();
            ++i;
        }
    }
}

