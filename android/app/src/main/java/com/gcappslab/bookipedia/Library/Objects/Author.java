package com.gcappslab.bookipedia.Library.Objects;

/**
 * Author object
 */

public class Author {

    private int AuthorId;
    private String AuthorName;
    private String Biography;

    public Author(int authorId, String authorName, String biography) {
        AuthorId = authorId;
        AuthorName = authorName;
        Biography = biography;
    }

    public Author(int authorId, String authorName) {
        AuthorId = authorId;
        AuthorName = authorName;
    }

    public int getAuthorId() {
        return AuthorId;
    }

    public void setAuthorId(int authorId) {
        AuthorId = authorId;
    }

    public String getAuthorName() {
        return AuthorName;
    }

    public void setAuthorName(String authorName) {
        AuthorName = authorName;
    }

    public String getBiography() {
        return Biography;
    }

    public void setBiography(String biography) {
        Biography = biography;
    }
}
