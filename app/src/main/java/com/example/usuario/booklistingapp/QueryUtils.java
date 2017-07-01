package com.example.usuario.booklistingapp;

import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.lang.String;


/**
 * Created by Usuario on 30/6/17.
 */

public class QueryUtils {

    //Tag for the log messages

    public static final String LOG_TAG = QueryUtils.class.getSimpleName();

    public static List<Book> fetchBookData(String query) {

        //Create a variable URL including the user query
        URL url = createUrl(query);

        //Now create a variable for the json response
        String jsonResponse = null;

        try {

            //try to send an http request with the created url
            jsonResponse = makeHttpRequest(url);


        } catch (IOException e) {
            //if there is an exception show it in the log
            Log.e(LOG_TAG, "Error at the http Request", e);
        }

        //Get the requested info from the query and create the suitable list of data
        ArrayList<Book> books = extractFeatureFromJson(jsonResponse);

        return books;
    }

    //Now I create an URL to compose the URL including the custom user search onto the current
    //query
        /* @param query: the searching word set by the user at each searching.
         */

    private static URL createUrl(String query) {

        URL url = null;

        try {
            //here we include the url + the user search term in order to create a NEW complete URL
            url = new URL("https://www.googleapis.com/books/v1/volumes?q=intitle:" + query + "&maxResults=8");

        } catch (MalformedURLException e) {

            // if the composition of the url fails, show it into the logs
            Log.e(LOG_TAG, "Fail at the new URL composition", e);
        }

        return url;

    }

    /*
         * THIS FOLLOWING BLOCK OF CODE IS TO CREATE AN HTTP REQUEST AND GET THE RESPONSE
         * @param url: this is the new "customized" URL created by the createURL method
         * @return the response of the request or null just in the case we don't get any response
         */
    private static String makeHttpRequest(URL url) throws IOException {

        //Create a variable for the response
        String jsonResponse = "";

        //If no valid URL provided, return early
        if (url == null) {
            return jsonResponse;
        }

        //Create a variable for the connection and the InputStream ( the data on bites )
        HttpURLConnection connection = null;
        InputStream stream = null;

        try {

            //Try to GET a connection with the created URL and the settings below and connect
            connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(15000);
            connection.setReadTimeout(10000);
            connection.setRequestMethod("GET");
            connection.connect();

            //(Number of response 200 is a valid response)

            if (connection.getResponseCode() == 200) {

                //If the connection is successful, get the InputStream and the response from the InputStream
                stream = connection.getInputStream();
                jsonResponse = readFromStream(stream);
            } else {

                //If the connection is not valid show it into the logs
                Log.e(LOG_TAG, "Not valid response: " + connection.getResponseCode());
            }
        } catch (IOException e) {

            //If it finds an IOException, show it into the logs
            Log.e(LOG_TAG, "IOException found", e);
        } finally {

            //If after this process the connection is still on and the InputStream is still open, order to disconnect and close the InputStream
            if (connection != null) {
                connection.disconnect();
            }
            if (stream != null) {
                stream.close();
            }
        }

        return jsonResponse;

    }

        /*
         * IN THE FOLLOWING BLOCK OF CODE WE ADD A HELPER METHOD IN ORDER TO READ
         * THE DATA OBTEINED BY THE RESPONSE. THIS DATA ARE BITES (INPUTSTREAM)
         * This inputStream needs to be "translated" into useful info for our app.
         * So we are going to "String" it , through the methods StringBuilder,
         * InputStreamReader and BufferedReader.
         * @param stream: Is the InputStream given back by the server
         * @return the response of the request or null
         */

    private static String readFromStream(InputStream stream) throws IOException {

        //Create a new StringBuilder for the response
        StringBuilder builder = new StringBuilder();

        //If the InputStream is not null (so its valid ), then create an InputStreamReader and
        //a BufferedReader to read the data from the InputStream
        if (stream != null) {

            InputStreamReader streamReader = new InputStreamReader(stream, Charset.forName("UTF-8"));

            //We use a buffered in order to read the info by lines, not by caracters as the InputStreamReader does

            BufferedReader bufferedReader = new BufferedReader(streamReader);

            //Now that we have the data into the streamReader, Read the data line by line and add it to the StringBuilder
            String line = bufferedReader.readLine();
            while (line != null) {
                builder.append(line);
                line = bufferedReader.readLine();
            }
        }

        //Return the response in String
        return builder.toString();

        /*
         * IN THIS FOLLOWING BLOCK OF CODE WE ARE GOING TO EXTRACT THE DATA FROM THE JSON RESPONSE
          * AND CREATE A LIST OF BOOKS FROM IT.
         * @param responseJson: Is the JSON response already read from the InputStream (from the steps above)
         * @return the List of the Book OBJECTS created from the JSON response
         */

        private static List<Book> extractFromJson(String responseJson) {

            //If the response is empty, return early
            if (TextUtils.isEmpty(responseJson)) {
                return null;
            }

            //Create a List for the Book Objects
            List<Book> books = new ArrayList<Book>();

            try {

                //Try to get the main JSONObject (the whole compilation of the queried books) from the response and get the items (the books one by one)
                JSONObject compilations = new JSONObject(responseJson);
                JSONArray items = compilations.getJSONArray("items");

                //Loop through all the books, get the title, and author of all the books in the JSON response
                for (int i = 0; i < items.length(); i++) {
                    JSONObject item = items.getJSONObject(i);
                    JSONObject compilation = compilations.getJSONObject("compilationInfo");
                    String title = compilation.getString("title");
                    String author;
                    if (compilation.has("authors")) {
                        author = compilation.getJSONArray("authors").get(0).toString();
                    }
                    else {
                        author = "Unknown author";
                    }

                    //Create a new Book Object with all the  data parameters
                    Book book = new Book(author, title);

                    //Add the book to the List of Book Objects
                    books.add(book);
                }


            } catch (JSONException e) {

                //If it finds a JSONException, show it into the logs
                Log.e(LOG_TAG, "JSON response exception", e);
            }

            return books;

        }
    }


}
