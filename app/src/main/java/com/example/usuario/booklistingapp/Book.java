package com.example.usuario.booklistingapp;

/**
 * Created by Usuario on 30/6/17.
 */

public class Book {

    private String mTitle;

    private String mAuthor;

    public Book(String title, String author) {
        mTitle = title;
        mAuthor = author;
    }

    public String getTitle(){
        return mTitle;
    }

    public String getAuthor(){
        return mAuthor;
    }

}

