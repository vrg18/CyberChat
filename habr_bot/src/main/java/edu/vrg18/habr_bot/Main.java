package edu.vrg18.habr_bot;

import org.json.JSONObject;

import java.io.IOException;

public class Main {

//    private static final String API_URL = "http://159.69.208.196:8080/rest/";  // Адрес API
    private static final String API_URL = "http://localhost:8080/rest/";  // Адрес API
    private static final String API_USERNAME = "habrabot";
    private static final String API_PASSWORD = "123";
    private static final String API_FIRSTNAME = "HabrBot";

    public static void main(String[] args) throws IOException {

        JSONObject isThereBot = JsonReader.readJsonObjectFromUrl(API_URL.concat("name/").concat(API_USERNAME));

        System.out.println(isThereBot);
    }
}
