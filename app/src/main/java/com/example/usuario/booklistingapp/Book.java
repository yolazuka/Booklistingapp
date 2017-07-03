package com.example.usuario.booklistingapp;

import android.net.Uri;

/**
 * Created by Usuario on 30/6/17.
 */

public class Book {

    private String mTitle;

    private String mAuthor;

    private Uri mCover;

    public Book(String title, String author, Uri cover) {
        mTitle = title;
        mAuthor = author;
        mCover = cover;
    }

    public String getTitle(){
        return mTitle;
    }

    public String getAuthor(){
        return mAuthor;
    }

    public Uri getCover() {
        return mCover;
    }

}

