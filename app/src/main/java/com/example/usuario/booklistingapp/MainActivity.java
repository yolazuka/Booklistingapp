package com.example.usuario.booklistingapp;

import android.app.LoaderManager;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Context;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LoaderCallbacks<List<Book>> {

    //We specify an ID for our loader //

    public static final int BOOK_LOADER_ID = 1;

    //Private Variableto keep the user query when rotate the device

    private String mQuery;

    //URL for the book data from the googleAPI//

    private static final String MAIN_REQUEST_URL = "https://www.googleapis.com/books/v1/volumes?q=intitle";

    //TextView that is displayed when the list of books is empty//

    private TextView mEmptyStateTextView;

    //Adapter for the list of books//

    private BookAdapter mAdapter;

    //declare the variable for the progress bar //

    private ProgressBar mProgressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Set the connectivity manager, which checks the state of network connectivity
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);

        //To get the details on the current active default data network
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        //If there is a network connection, fetch data
        if (networkInfo != null && networkInfo.isConnected()) {
            //Get a link to the LoaderManager, in order to interact with loaders
            LoaderManager loaderManager = getLoaderManager();

            //in this moment the loader is initializing. it links to the int ID
            loaderManager.initLoader(BOOK_LOADER_ID, null, this);

        } else {

            //otherwise display an error. First hide loading indicator so error message will be visible
            View loadingIndicator = findViewById(R.id.progressBar);
            loadingIndicator.setVisibility(View.GONE);

            //Update empty state with no connection error message
            mEmptyStateTextView.setText(R.string.no_internet_connection);
        }

        // Find a reference to the {@link ListView} in the layout
        ListView bookListView = (ListView) findViewById(R.id.list);

        //set an empty view if there is not data from the url on the app

        mEmptyStateTextView = (TextView) findViewById(R.id.empty_view);
        bookListView.setEmptyView(mEmptyStateTextView);

        //Create a new adapter that takes a list of books as an input//
        mAdapter = new BookAdapter(this, new ArrayList<Book>());

        //link the adapter to the ListView, so in this way the list can be populate in the
        //user interface
        bookListView.setAdapter(mAdapter);

        //Declare a LoaderManager, in order to interact with loaders

        LoaderManager loaderManager = getLoaderManager();

        //Initialize the loader.

        loaderManager.initLoader(BOOK_LOADER_ID, null, this);

    }

    @Override

    public Loader<List<Book>> onCreateLoader(int i, Bundle bundle) {

        // Create a new loader for the given URL

        return new BookLoader(this, MAIN_REQUEST_URL);

    }

    // After the background task is done, bring it back to the main thread

    @Override
    public void onLoadFinished(Loader<List<Book>> loader, List<Book> books) {

        //We set invisible the progress bar

        mProgressBar.setVisibility(View.GONE);

        /// Then We Set empty state text to display "No books found."
        mEmptyStateTextView.setText(R.string.no_books_found);

        //And Clear the adapter of previous book search data
        mAdapter.clear();

        // If there is a valid list of books, then add them to the adapter's
        // data set. This will trigger the ListView to update.
        if (books != null && !books.isEmpty()) {
            mAdapter.addAll(books);
        }

    }

    @Override

    public void onLoaderReset(Loader<List<Book>> loader) {
        mAdapter.clear();

    }

    //THE FOLLOWING BLOCK OF CODE IS TO KEEP THE DATA WHEN ROTATE THE DEVICE

    @Override
    protected void onSaveInstanceState(Bundle outState) {

        //Save the query given by the user
        mQuery = search.getQuery().toString();
        outState.putString("query", mQuery);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {

        //Restore the saved data, to initialize it later through the loader
        mQuery = savedInstanceState.getString("query");
        //Initialize the Loader (execute the search)
        super.onRestoreInstanceState(savedInstanceState);
    }
}
