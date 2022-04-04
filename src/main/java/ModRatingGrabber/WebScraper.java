package ModRatingGrabber;

import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class WebScraper {
    Document doc;
    Request request;
    Response response;
    ArrayList<String> mods = new ArrayList<>();
    ArrayList<Float> scores = new ArrayList<>();
    ArrayList<Integer> votes = new ArrayList<>();


    int getLastPage() throws IOException {
        doHTML("https://www.minecraftmods.com/");
        String test_subject = UtilityMethods.findValueAt("Last",doc.toString(),3,-19);
        if(Character.isDigit(test_subject.charAt(0))) // Future proofing for when page amount gets to 100's
        return Integer.valueOf(test_subject);
        return Integer.valueOf(test_subject.substring((1)));
    }
    void doHTML(String urlToScrape) throws IOException {
        OkHttpClient client = new OkHttpClient().newBuilder()
                .addInterceptor(chain -> {
                    final Request original = chain.request();
                    final Request authorized = original.newBuilder()
                            .addHeader("Cookie", "cookie-name=cookie-value")
                            .build();
                    return chain.proceed(authorized);
                })
                .build();
        request = new Request.Builder().url(urlToScrape).build();
        response = client.newCall(request).execute();
        doc = Jsoup.connect(urlToScrape).post();
        if(!response.isSuccessful())
        {
            response.close();
        }
    }
    ArrayList<String> grabModsOnCurrentPage(){
        //Target Website has specific heading style for their mods! :D
        Elements elements = grabFromHTML("h2","class");
        for(Element element : elements)
        {
            String tempString = element.toString().substring(32); //Chop off prefix
            //Calculate ending
            int cutOff = tempString.indexOf("\"");
            tempString = tempString.substring(0,cutOff);

            mods.add(tempString);
        }
        return mods;
    }

    public void grabModRating()
    {
        Elements elements = grabFromHTML("script","type");
        for(Element seeker : elements)
        {
            if(seeker.toString().contains("ratingValue"))
            {
                String datum = seeker.data();
                int start = datum.indexOf("ratingValue")+18; //cut off prefix
                datum =datum.substring(start,start+4);
                scores.add(Float.valueOf(datum));

                datum = seeker.data();
                start = datum.indexOf("ratingCount")+18;
                datum = datum.substring(start);
                datum = datum.substring(0,datum.indexOf(","));
                datum = datum.substring(0,datum.indexOf("\""));

                votes.add(Integer.valueOf(datum));
                int index = scores.size()-1;
               printModInfo(index);
            }
        }
    }
    public Elements grabFromHTML(String tag, String spec)
    {
        return doc.select(tag+"["+spec+"]");
    }
    private void printModInfo(int index)
    {
        System.out.println("["+index+"] URL: "+mods.get(index)+" | Votes: "+votes.get(index)+" | Score (out of 5): "+scores.get(index));
    }
}