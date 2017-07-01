package com.example.usuario.booklistingapp;

import android.content.AsyncTaskLoader;
import android.content.Context;

import java.util.List;

/**
 * Created by Usuario on 30/6/17.
 */

//Load a list of books by using an AsyncTask to perform
//the network request to the given URL.

public class BookLoader extends AsyncTaskLoader<List<Book>> {

    //Tag for log messages //
    private static final String LOG_TAG = BookLoader.class.getName();

    //Query URL //
    private String mUrl;

    /** In this way constructs a new bookLoader
     *
     * @param context of the activity
     * @param url to load data from
     */

    public BookLoader(Context context, String url) {

        super(context);
        mUrl = url;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    /**
     * This is on a background thread.
     */

    @Override
    public List<Book> loadInBackground() {
        if (mUrl == null) {
            return null;
        }

        // Perform the network request, parse the response, and extract a list of books
        List<Book> books = QueryUtils.fetchBookData(mUrl);
        return books;
    }
}
