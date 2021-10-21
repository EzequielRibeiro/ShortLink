package org.ezequiel.shortlink;

import android.util.JsonReader;
import android.util.Log;

import androidx.annotation.NonNull;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.UnknownHostException;

public class GetShortLink {
    //https://shrtco.de/docs/
    //https://api.shrtco.de/v2/shorten?url=www.ig.com.br
    //https://api.shrtco.de/v2/info?code=ZjPQyL


    private ShortLink shortlink;

    public GetShortLink() throws IOException {

        shortlink = new ShortLink();
       
    }

    private ShortLink readShortlink1(JsonReader reader) throws IOException {

        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            if (name.equals("ok")) {
                shortlink.setIsOkUr1(reader.nextBoolean());
            } else if (name.equals("error_code")) {
                shortlink.setError_code1(reader.nextString());
            } else if (name.equals("result")) {
                    shortLink(reader, shortlink);
            } else {
                reader.skipValue();
            }
        }
        reader.endObject();
        return shortlink;
    }

    private ShortLink readShortlink2(JsonReader reader) throws IOException {

        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            if (name.equals("shorturl")) {
                shortlink.setIsOkUr2(true);
                shortlink.setCode2(reader.nextString());
            } else if (name.equals("url")) {
                shortlink.setUrl(reader.nextString());
            } else if (name.equals("errorcode")) {
                shortlink.setError_code2(reader.nextString());
            } else if (name.equals("errormessage")) {
                shortlink.setErrorMensagem2(reader.nextString());
            } else {
                reader.skipValue();
            }
        }

        reader.endObject();
        return shortlink;
    }

    private void shortLink(JsonReader reader, ShortLink shortlink) throws IOException {

        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            if (name.equals("code")) {
                shortlink.setCode1(reader.nextString());
            } else if (name.equals("original_link")) {
                shortlink.setOriginal_link(reader.nextString());
            } else {
                reader.skipValue();
            }
        }
        reader.endObject();
    }

    public void requestShortlink(@NonNull String urlLongLink) throws IOException {
        JsonReader reader = null;
        HttpURLConnection conn = null;
        shortlink.setUrlApi(urlLongLink);
        Log.e("request",shortlink.getUrlApi());

        try {

            //URL url = new URL("https://api.shrtco.de/v2/shorten?url=" + longLinkURL);
            // url = new URL("https://api.shrtco.de/v2/info?code=ZdlWIz");
            URL url = new URL(urlLongLink);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");
            conn.setConnectTimeout(5000);

            if (conn.getResponseCode() != 200 && conn.getResponseCode() != 201) {

                reader = new JsonReader(new InputStreamReader(conn.getErrorStream(), "UTF-8"));

            } else {

                reader = new JsonReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));

            }


            if (urlLongLink.contains("https://api.shrtco.de"))
                readShortlink1(reader);
            else
                readShortlink2(reader);

        } catch (SocketTimeoutException e) {
            showError(urlLongLink,"Error: Socket Timeout");
            e.printStackTrace();

        } catch (FileNotFoundException e) {
            showError(urlLongLink,"Error: File NotFound");
            e.printStackTrace();

        } catch (MalformedURLException e) {
            showError(urlLongLink,"Error: MalformedURL");
            e.printStackTrace();

        } catch (UnknownHostException e) {

            showError(urlLongLink,"Error: Unknown Host");
           e.printStackTrace();

        } catch (IOException | RuntimeException e) {
            showError(urlLongLink,"Error: IOException | RuntimeException");
            e.printStackTrace();

        } finally {
            if (reader != null)
                reader.close();
            if (conn != null)
                conn.disconnect();
        }

    }

    public ShortLink getShortlink() {

        return shortlink;
    }

    private void showError(String urlLongLink, String error){

           shortlink.setErrorMensagem1(error);
           shortlink.setErrorMensagem2(error);

    }

    /*shrtco.de/
    9qr.de/
    shiny.link/*/


}
