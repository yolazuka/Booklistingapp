package com.example.usuario.booklistingapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Usuario on 30/6/17.
 */
public class BookAdapter extends ArrayAdapter<Book> {

    //BookAdapter is an ArrayAdapter that can set the layout for each list
    //of items based on data source. This data is a list of Book objects

    /**
     * Create a new {@link BookAdapter} object.
     *
     * @param context is the current context (i.e. Activity) that the adapter is being created in.
     * @param books is the list of books to be displayed
     */

    public BookAdapter(Context context, List<Book> books) {

        super(context, 0, books);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // Check if an existing view is being reused, otherwise inflate the view
        View bookListView = convertView;

        if (bookListView == null) {
            bookListView = LayoutInflater.from(getContext()).inflate(R.layout.list_items, parent, false);
        }

        // Get the current object located at this position in the list
        Book currentBook = getItem(position);

        //Get the TextView with the title_view ID and set the title of the current book as text
        TextView titleTextView = (TextView) bookListView.findViewById(R.id.title_text_view);
        titleTextView.setText(currentBook.getTitle());

        //Get the TextView with the author_view ID and set the author of the current book as text
        TextView authorTextView = (TextView) bookListView.findViewById(R.id.author_text_view);
        authorTextView.setText(currentBook.getAuthor());

        return bookListView;

    }

}