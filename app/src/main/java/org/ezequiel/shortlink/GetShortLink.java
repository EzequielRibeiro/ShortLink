package org.ezequiel.shortlink;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class GetShortLink {
   //https://shrtco.de/docs/
    //https://api.shrtco.de/v2/shorten?url=www.ig.com.br
    //https://api.shrtco.de/v2/info?code=ZjPQyL
    public GetShortLink(String longLinkURL,String api_key) {

        try {

           // URL url = new URL("https://cutt.ly/api/api.php?key="+api_key+"&short="+longLinkURL+"&name=GLink");
            URL url = new URL("https://api.shrtco.de/v2/shorten?url=www.ig.com.br");
            url = new URL("https://api.shrtco.de/v2/info?code=ZdlWIz");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");

          //  System.out.println(url.toString());

            if (conn.getResponseCode() != 200 && conn.getResponseCode() != 201) {
                throw new RuntimeException("Failed : HTTP error code : "
                        + conn.getResponseCode());
            }

            BufferedReader br = new BufferedReader(new InputStreamReader(
                    (conn.getInputStream())));

            String output;
            System.out.println(" Code response:  "+conn.getResponseCode());
            System.out.println("Output from Server .... \n");
            while ((output = br.readLine()) != null) {
                System.out.println(output);
            }

            conn.disconnect();

        } catch (MalformedURLException e) {

            e.printStackTrace();

        } catch (IOException | RuntimeException e) {

            e.printStackTrace();

        }

    }

}
