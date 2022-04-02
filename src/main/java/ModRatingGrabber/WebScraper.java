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
    private Document doc;
    Request request;
    Response response;
    ArrayList<String> mods = new ArrayList<>();


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
    }

    String extractTitle(String content) {
        final Pattern titleRegExp = Pattern.compile("<title>(.*?)</title>", Pattern.DOTALL);
        final Matcher matcher = titleRegExp.matcher(content);
        matcher.find();
        return matcher.group(1);
    }
    ArrayList<String> grabModsOnCurrentPage() throws IOException {

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
    public Elements grabFromHTML(String tag, String spec)
    {
        Elements elements = doc.select(tag+"["+spec+"]");

        return elements;
    }
}