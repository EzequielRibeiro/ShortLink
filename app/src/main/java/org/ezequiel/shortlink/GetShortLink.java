package org.ezequiel.shortlink;

import android.util.JsonReader;
import android.util.Log;

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


    private ShortLink shortlink = new ShortLink();

    public GetShortLink(String longLinkURL) throws IOException {

        JsonReader reader = null;
        HttpURLConnection conn = null;

        try {

            URL url = new URL("https://api.shrtco.de/v2/shorten?url=" + longLinkURL);
            // url = new URL("https://api.shrtco.de/v2/info?code=ZdlWIz");
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");
            conn.setConnectTimeout(5000);

            if (conn.getResponseCode() != 200 && conn.getResponseCode() != 201) {

                reader = new JsonReader(new InputStreamReader(conn.getErrorStream(), "UTF-8"));

            } else {

                reader = new JsonReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));

            }

            readshortlink(reader);

        } catch (SocketTimeoutException e) {
            shortlink.setErrorMensagem("Error: SocketTimeoutException");
            e.printStackTrace();

        } catch (FileNotFoundException e) {
            shortlink.setErrorMensagem("Error: FileNotFoundException");
            e.printStackTrace();

        } catch (MalformedURLException e) {
            shortlink.setErrorMensagem("Error: MalformedURLException");
            e.printStackTrace();

        } catch (UnknownHostException e) {
            shortlink.setErrorMensagem("Error: UnknownHostException");
            e.printStackTrace();

        } catch (IOException | RuntimeException e) {
            shortlink.setErrorMensagem("Error: IOException | RuntimeException");
            e.printStackTrace();

        } finally {
            if (reader != null)
                reader.close();
            if (conn != null)
                conn.disconnect();
        }

    }

    public ShortLink readshortlink(JsonReader reader) throws IOException {

        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            if (name.equals("ok")) {
                shortlink.setOk(reader.nextBoolean());
            } else if (name.equals("error_code")) {
                shortlink.setError_code(reader.nextString());
            } else if (name.equals("result")) {
                shortLink(reader, shortlink);
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
                shortlink.setCode(reader.nextString());
            } else if (name.equals("original_link")) {
                shortlink.setOriginal_link(reader.nextString());
            } else {
                reader.skipValue();
            }
        }
        reader.endObject();
    }

    public ShortLink getShortlink() {
        return shortlink;
    }

    /*shrtco.de/
    9qr.de/
    shiny.link/*/


}
