package edu.vrg18.habr_bot;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;


class JsonReader {

    static JSONArray readJsonArrayFromUrl(String ... args) throws IOException {

        return new JSONArray(readJsonTextFromUrl(args));
    }

    static JSONObject readJsonObjectFromUrl(String ... args) throws IOException {

        return new JSONObject(readJsonTextFromUrl(args));
    }

    private static String readJsonTextFromUrl(String[] args) throws IOException {

        HttpURLConnection conn = (HttpURLConnection) new URL(args[0]).openConnection();

        if (args.length > 1) {
            Base64.Encoder enc = Base64.getEncoder();
            byte[] encbytes = enc.encode((args[1] + ":" + args[2]).getBytes());
            conn.setRequestProperty("Authorization", "Basic " + new String(encbytes));
        }

        conn.setDoOutput(true);

        try (InputStream is = conn.getResponseCode() == 200 ? conn.getInputStream() : conn.getErrorStream()) {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
            return readAll(rd);
        } finally {
            conn.disconnect();
        }
    }

    private static String readAll(Reader rd) throws IOException {

        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        return sb.toString();
    }
}
