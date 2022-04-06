package ModRatingGrabber;

import java.io.IOException;

public class WebScraperMain {
    public static void main(String[] args) throws IOException {

        //MinecraftMods.com report
        String base_url = "https://www.minecraftmods.com/page/";
        WebScraper scraper = new WebScraper();
        int last_page = scraper.getLastPage(1); // Gets last page index from Last button in HTML
        printResults(scraper,base_url,last_page);


        //planetminecraft.com report
        WebScraper scraper2 = new WebScraper();
        base_url = "https://www.planetminecraft.com/mods/tag/mod/?p=";
        last_page = scraper2.getLastPage(2);
        printResults(scraper2,base_url,last_page);



    }
    static void printResults(WebScraper scraper,String base_url,int last_page) throws IOException {
        String target_url;

        if(base_url.equals("https://www.minecraftmods.com/page/"))
        {
            for (int j = 1; j != last_page+1; j++) {
                System.out.println("Page: "+j+" / "+last_page+" added. "+(int)(((float)j/(float)last_page)*100)+"%");
                target_url = base_url + j;
                scraper.doHTML(target_url);
                scraper.grabModsOnCurrentPage(1);
            }
        }
        else if(base_url.equals("https://www.planetminecraft.com/mods/tag/mod/?p="))
        {
            for(int j = 1; j!=last_page; j++) //Bug: For some reason after i gets to 27, it just repeats page 2's mods.
            {
                System.out.println("Page: "+j+" / "+last_page+" added. "+(int)(((float)j/(float)last_page)*100)+"%");
                target_url = base_url+j;
                System.out.println("TargetUrl: "+target_url);
                scraper.doHTML(target_url);
                scraper.grabModsOnCurrentPage(2);
            }
        }

    }
}

