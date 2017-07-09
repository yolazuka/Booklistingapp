package com.example.usuario.booklistingapp;

import android.net.Uri;
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
        List<Book> books = extractBookInfo(jsonResponse);

        return books;
    }

    //Now I create an URL to compose the URL including the custom user search onto the current
    //query
        /* @param query: the searching word set by the user at each searching.
         */

    private static URL createUrl(String query) {

        URL url = null;

        try {

            url = new URL(query);

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
    }
        /*
         * IN THIS FOLLOWING BLOCK OF CODE WE ARE GOING TO EXTRACT THE DATA FROM THE JSON RESPONSE
          * AND CREATE A LIST OF BOOKS FROM IT.
         * @param responseJson: Is the JSON response already read from the InputStream (from the steps above)
         * @return the List of the Book OBJECTS created from the JSON response
         */

    private static List<Book> extractBookInfo(String objectBookJSON) {

        String responseJson;

        //If the response is empty, return early
        if (TextUtils.isEmpty(objectBookJSON)) {
            return null;
        } else {

            //Create a List for the Book Objects
            List<Book> books = new ArrayList<Book>();

            try {

                //We create the object of the book and the Array that contain these objects
                JSONObject objectBook = new JSONObject(objectBookJSON);
                JSONArray itemsArray = objectBook.getJSONArray("items");

                //Loop through all the books, get the title, author and info link of all the books in the JSON response
                for (int i = 0; i < itemsArray.length(); i++) {

                    JSONObject item = itemsArray.getJSONObject(i);
                    JSONObject bookAttributes = item.getJSONObject("volumeInfo");
                    JSONObject imageForCover = bookAttributes.getJSONObject("imageLinks");
                    String urilinkBook = "";
                    Uri imgUri = Uri.parse(urilinkBook);
                    String title = bookAttributes.getString("title");

                    String authors = "";

                    //If there is author info make this:

                    if (bookAttributes.has("authors")) {

                        JSONArray author = bookAttributes.getJSONArray("authors");
                        Log.v(LOG_TAG, "The key authors got author info");

                        for (int x = 0; x < author.length(); x++) {
                            authors = authors.concat(author.getString(x) + "\n");
                        }
                        //if there is not, show this message
                    } else {
                        authors = "Unknown author";

                    }

                    //if there is title info, get the string from title

                    if (bookAttributes.has("title")) {
                        title = bookAttributes.getString("title");
                        Log.v(LOG_TAG, "The key title got title info");

                        //if there is not, show this message

                    } else {
                        title = "Unknown title";
                    }

                    //if there is image, parse the image thumbnail

                    if (bookAttributes.has("imageLinks")) {
                        imageForCover = bookAttributes.getJSONObject("imageLinks");
                        urilinkBook = imageForCover.getString("thumbnail");
                        imgUri = Uri.parse(urilinkBook);

                        Log.v(LOG_TAG, "The imagelinks key got info");

                        //if there is not image,show this message

                    } else {
                        urilinkBook = "Image N/A";
                    }

                    //Create a new Book Object with the data of a given book
                    Book currentbook = new Book(title, authors, imgUri);

                    //Add the book to the List of Book Objects
                    books.add(currentbook);
                }

            } catch (JSONException e) {

                //If it fails, log it
                Log.e(LOG_TAG, "Error extracting the data from the JSON response", e);
            }

            return books;

        }

    }
}

