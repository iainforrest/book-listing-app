package com.redefineeverything.booklistingapp;

import java.util.ArrayList;

/**
 * Created by Iain Forrest on 26/07/2016.
 */
public class Book {
    private String mBookTitle;
    private ArrayList<String> mAuthors;
    private String mInfoUrl;
    private String mDescription;

    public Book(String mBookTitle, ArrayList<String> mAuthors, String mInfoUrl, String mDescription) {
        this.mBookTitle = mBookTitle;
        this.mAuthors = mAuthors;
        this.mInfoUrl = mInfoUrl;
        this.mDescription = mDescription;
    }

    public String getBookTitle() {
        return mBookTitle;
    }

    public ArrayList<String> getAuthors() {
        return mAuthors;
    }

    public String getAuthorList () {
        StringBuilder authors = new StringBuilder();
        authors.append("By ");
        for (int i = 0; i < mAuthors.size(); i++) {
            authors.append(mAuthors.get(i));
            if (i < mAuthors.size() -1){
                authors.append(", ");
            }
        }
        return authors.toString();
    }

    public String getmInfoUrl() {
        return mInfoUrl;
    }

    public String getDescription() {
        return mDescription;
    }


}
