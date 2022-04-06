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

    String findValueAt(String target,int n,int offset) //n is desired amount of characters in returned string
    {                                                                      //offset to find value near target word
        int start = doc.toString().indexOf(target)+offset;
        int end = start+n;
        return  doc.toString().substring(start,end);
    }

    int getLastPage(int website) throws IOException {
        if(website==1) { //minecraftmods
            doHTML("https://www.minecraftmods.com/");
            String test_subject = findValueAt("Last", 3, -19);
            if (Character.isDigit(test_subject.charAt(0))) // Future proofing for when page amount gets to 100's
                return Integer.parseInt(test_subject);
            return Integer.parseInt(test_subject.substring((1)));
        }
        else if(website==2) //planetminecraft
        {
            doHTML("https://www.planetminecraft.com/mods/tag/mod/?p=");
            String test_subject = findValueAt("</span> of",5,11);
            while(test_subject.indexOf(',')!=-1) //remove commas from string to prep for integer conversion
            {
                int comma_place = test_subject.indexOf(',');
                test_subject = test_subject.substring(0,comma_place)+test_subject.substring(comma_place+1);
            }
            return Integer.parseInt(test_subject)/25; //Each page paginated by 25


        }
        return -1;
    }
    void doHTML(String urlToScrape) throws IOException {
        OkHttpClient client = new OkHttpClient().newBuilder()
                .addInterceptor(chain -> {
                    final Request original = chain.request();
                    final Request authorized = original.newBuilder()
                            .build();
                    return chain.proceed(authorized);
                })
                .build();
        if(response!=null)
        response.close(); // prevent some warnings from being thrown
        request = new Request.Builder().url(urlToScrape).build();
        response = client.newCall(request).execute();
        doc = Jsoup.connect(urlToScrape).post();
        if(!response.isSuccessful())
        {
            response.close();
        }
    }
    ArrayList<String> grabModsOnCurrentPage(int website) throws IOException {
        //Target Website has specific heading style for their mods! :D
        if(website==1)
        {
           Elements elements = grabFromHTML("h2","class");
            for(Element element : elements)
            {
                String temp_string = element.toString().substring(32); //Chop off prefix
                //Calculate ending
                int cutOff = temp_string.indexOf("\"");
                temp_string = temp_string.substring(0,cutOff);

                mods.add(temp_string);

                doHTML(temp_string);
                grabModRating();
            }
        }
        else if(website==2)
        {
            Elements elements = grabFromHTML("a","href");
            for(Element element : elements)
            {
                if(element.toString().contains("/mod/"))
                {
                    String temp_string = element.toString();
                    int start = temp_string.indexOf("/mod/")+5;
                    temp_string = temp_string.substring(start);
                    int end = temp_string.indexOf("/");
                    temp_string = temp_string.substring(0,end);
                    if('?'!=temp_string.charAt(0)&&'"'!=temp_string.charAt(0))
                    {
                        if(mods.isEmpty()||!temp_string.equals(mods.get(mods.size()-1)))
                        {
                            //ToDo: Fix Page27+ bug
                            mods.add(temp_string);
                            System.out.println("["+mods.size()+"] ModUrl: https://www.planetminecraft.com/mod/"+temp_string);
                        }
                    }
                }
            }
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
        System.out.println("["+(index+1)+"] URL: "+mods.get(index)+" | Votes: "+votes.get(index)+" | Score (out of 5): "+scores.get(index));
    }
}