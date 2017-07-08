package com.example.usuario.booklistingapp;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import static com.example.usuario.booklistingapp.QueryUtils.LOG_TAG;

public class MainActivity extends AppCompatActivity {

    public EditText searchField;

    //variable for the search of the EditText
    private String mQuery;

    //URL for the book data from the googleAPI//
    private int MAIN_REQUEST_URL = R.string.google_book_api;

    //TextView that is displayed when the list of books is empty//

    private TextView emptyStateTextView;

    //Adapter for the list of books//

    private BookAdapter adapter;

    //declare the variable for the progress bar //

    private ProgressBar progressBar;

    //declare the final string for the userSearch

    private String finalUserRequest;

    private Button search;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Find the ListView id in the listview xml
        final ListView bookList = (ListView) findViewById(R.id.list);

        //creates a new adapter from the previously created BookAdapter
        adapter = new BookAdapter(this, new ArrayList<Book>());

        //Set the created ListView with the SetAdapter
        bookList.setAdapter(adapter);

        //Find the view for the empty View in xml
        emptyStateTextView = (TextView) findViewById(R.id.empty_view);

        //set and link the ListView (bookList ) with the TextView( emptyStateTextView )
        bookList.setEmptyView(emptyStateTextView);

        // Find the View that shows the EditText
        searchField = (EditText) findViewById(R.id.field_for_the_search);

        //Find the View for the progress bar xml
        progressBar = (ProgressBar) findViewById(R.id.progress_Bar);

        // Find the View that shows the searching button
        search = (Button) findViewById(R.id.search_button);

        // Set a click listener on that View
        search.setOnClickListener(new View.OnClickListener() {

            // when the user cliks the button send the searchword into a variable to include it into the request URL and
            //through the Builder we compose the whole url request ( api http + the user query = finalUserRequest )

            @Override
            public void onClick(View view) {
                StringBuilder builder = new StringBuilder();
                String userSearch = searchField.getText().toString();
                String bookApi = getString(MAIN_REQUEST_URL);
                builder.append(bookApi).append(userSearch);
                finalUserRequest = builder.toString();
                Log.e(userSearch, "this is the user search word");
                Log.e(finalUserRequest, " this is the final url");
                progressBar.setVisibility(View.VISIBLE);

                if (progressBar.getVisibility() == View.VISIBLE) {
                    emptyStateTextView.setVisibility(View.GONE);
                }

                //Set the connectivity manager, which checks the state of network connectivity
                ConnectivityManager connMgr = (ConnectivityManager)
                        getSystemService(Context.CONNECTIVITY_SERVICE);

                //To get the details on the current active default data network
                NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

                //If there is a network connection, fetch data
                if (networkInfo != null && networkInfo.isConnected()) {
                    BookAsyncTask asyncTask = new BookAsyncTask();
                    Log.v(LOG_TAG, "FinalUserRequest being fetched is: " + finalUserRequest);
                    asyncTask.execute(finalUserRequest);

                } else {

                    //if there is not network hide the progress bar and..
                    adapter.clear();
                    progressBar.setVisibility(View.GONE);

                    //Update empty state with no connection error message
                    emptyStateTextView.setText(R.string.no_internet_connection);
                }

            }
        });

    }

    //THE FOLLOWING BLOCK OF CODE IS TO CREATE AN INNER CLASS FOR THE ASYNCTASK

    private class BookAsyncTask extends AsyncTask<String, Void, List<Book>> {

        @Override
        protected List<Book> doInBackground(String... params) {
            //IF the url is not valid, dont execute any task
            if (params.length < 1 || params[0] == null) {
                return null;
            }
            List<Book> books = QueryUtils.fetchBookData(params[0]);

            Log.v(LOG_TAG, "url being fetched is: " + params[0]);

            return books;
        }

        protected void onPostExecute(List<Book> data) {
            progressBar.setVisibility(View.GONE);
            emptyStateTextView.setText(R.string.no_books_found);
            // Deletes the adapter from previous book items
            adapter.clear();
            if (data != null && !data.isEmpty()) {
                adapter.addAll(data);
            }
        }
    }
}