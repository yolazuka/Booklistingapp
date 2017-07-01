package com.example.usuario.booklistingapp;

import java.util.ArrayList;

/**
 * Created by Usuario on 30/6/17.
 */

public class Book {

    private String mTitle;

    private ArrayList<String> mAuthor;

    public Book(String title, ArrayList<String> author){
        mTitle = title;
        mAuthor = author;
    }

    public String getTitle(){
        return mTitle;
    }

    public String getAuthor(){
        String authors = checkAuthors();
        return authors;
    }

    public String checkAuthors() {
        String authors = mAuthor.get(0);
        if (mAuthor.size()>1) {
            for (int i=1; i<mAuthor.size(); i++) {
                authors += "\n" + mAuthor.get(i);
            }
        }
        return authors;
    }
}

