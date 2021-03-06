package net.bandoviet.tool;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;

import net.bandoviet.place.Place;
import net.bandoviet.place.PlaceType;

public class TestYELP {
  private static final String URL = "http://www.yelp.com/search?find_desc=vietnamese&find_loc=tokyo&ns=1";

  @Test
  public void URLConnectionReader() throws IOException {
    Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("216.190.97.3", 3128));
    URL oracle = new URL(URL);
    URLConnection yc = oracle.openConnection(proxy);
    BufferedReader in = new BufferedReader(new InputStreamReader(yc.getInputStream()));
    String inputLine;
    while ((inputLine = in.readLine()) != null)
      System.out.println(inputLine);
    in.close();

  }

  public void testCVS() {
    String csvFile = "/Users/quocanh/worldcities.csv";
    BufferedReader br = null;
    String line = "";
    String cvsSplitBy = ",";

    try {

      br = new BufferedReader(new FileReader(csvFile));
      while ((line = br.readLine()) != null) {

        // use comma as separator
        String[] country = line.split(cvsSplitBy);

        System.out.println(country[0].replaceAll("\"", "") + " : "
            + country[6].replaceAll("\"", "").replaceAll(" ", "+"));

      }

    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      if (br != null) {
        try {
          br.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }

    System.out.println("Done");
  }

  public void test() {
    try {
      Document doc = Jsoup.connect(URL).timeout(10000)
          .userAgent(
              "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_10_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/46.0.2490.71 Safari/537.36")
          .get();
      // Document doc = Jsoup.connect(url).get();
      Elements items = doc.select(".regular-search-result");
      System.out.println(items.size());

      for (int i = 0; i < items.size(); i++) {
        Place place = getPlace(items.get(i));
      }

      // lay duong linkk trang ke tiep
      Elements links = doc.select(".search-pagination").select("li a");
      for (Element link : links) {
        System.out.println("http://www.yelp.com" + link.attr("href"));
      }

      // Lay toa do (lat, lng) cac POIs
      String key1 = "yelp.www.init.search.Controller(";
      String key2 = "#find_desc";
      String str = doc.html();

      int start = str.indexOf(key1) + key1.length();
      int end = str.indexOf(key2, start);

      String json = str.substring(start, end);

      System.out.println(json);

      int idx = 0;
      while (json.indexOf("key", idx + 3) > 0) {
        int position = json.indexOf("key", idx + 3);
        String numStr = json.substring(position + 5, json.indexOf(",", position + 5)).trim();
        if (!StringTools.isNumeric(numStr)) {
          return;
        }
        System.out.println(String.valueOf(numStr));

        String substr = json.substring(idx, position);
        System.out.println(substr);
        start = substr.lastIndexOf("latitude") + 11;
        // System.out.println(start);
        end = substr.indexOf(",", start);

        System.out.println(substr.substring(start, end));

        start = substr.lastIndexOf("longitude") + 12;
        end = substr.indexOf("}", start);
        System.out.println(substr.substring(start, end));

        idx = json.indexOf("key", idx + 3);
      }

    } catch (Exception e) {
      System.out.println(e.getMessage());
    }
  }

  private Place getPlace(Element item) {
    Place place = new Place();
    place.setCommunityCode("VN");
    place.setPlaceType(PlaceType.RESTAURANT.toString());

    String key = item.select(".natural-search-result").attr("data-key");
    System.out.println(key);

    String photo = "http:" + item.select(".photo-box-img").attr("src");
    System.out.println(photo);

    String title = item.select(".photo-box-img").attr("alt");
    System.out.println(title);

    String telephone = item.select(".biz-phone").get(0).text();
    System.out.println(telephone);

    String address = item.select("address").text();
    System.out.println(address);

    return place;
  }
}
