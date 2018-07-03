package com.gcappslab.bookipedia.Library.Objects;

/**
 * Book object
 */

public class Book {

    private int BookId;
    private String Name;
    private int AuthorId;
    private String AuthorName;
    private int Year;
    private String Description;
    private String Genre;
    private String Language;
    private float Rating;
    private int Reviews;
    private String File;
    private int Progress;
    private String Notes;

    public Book(int bookId, String name, int authorId, String authorName, int year, String description,
                String genre, String language, float rating, int reviews) {
        BookId = bookId;
        Name = name;
        AuthorId = authorId;
        AuthorName = authorName;
        Year = year;
        Description = description;
        Genre = genre;
        Language = language;
        Rating = rating;
        Reviews = reviews;
        Progress = 0;
        Notes = "";
    }

    public Book(int bookId, String name, int authorId, String authorName, int year, String description, String file,
                String genre, String language, float rating, int reviews, int progress, String notes) {
        BookId = bookId;
        Name = name;
        AuthorId = authorId;
        AuthorName = authorName;
        Year = year;
        Description = description;
        File = file;
        Genre = genre;
        Language = language;
        Rating = rating;
        Reviews = reviews;
        Progress = progress;
        Notes = notes;
    }

    public Book(int bookId, String name, String authorName) {
        BookId = bookId;
        Name = name;
        AuthorName = authorName;
    }

    public Book(int bookId, String name, int authorId, String authorName, int year, String file, int progress, String notes) {
        BookId = bookId;
        Name = name;
        AuthorId = authorId;
        AuthorName = authorName;
        Year = year;
        File = file;
        Progress = progress;
        Notes = notes;
    }

    public Book(int bookId, String name, int authorId, String authorName, int year, String description, String genre, String language, float rating, int reviews, String file) {
        BookId = bookId;
        Name = name;
        AuthorId = authorId;
        AuthorName = authorName;
        Year = year;
        Description = description;
        Genre = genre;
        Language = language;
        Rating = rating;
        Reviews = reviews;
        File = file;
    }

    public Book() {
    }


    public int getBookId() {
        return BookId;
    }

    public void setBookId(int bookId) {
        BookId = bookId;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
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

    public int getYear() {
        return Year;
    }

    public void setYear(int year) {
        Year = year;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getGenre() {
        return Genre;
    }

    public void setGenre(String genre) {
        Genre = genre;
    }

    public String getLanguage() {
        return Language;
    }

    public void setLanguage(String language) {
        Language = language;
    }

    public float getRating() {
        return Rating;
    }

    public void setRating(float rating) {
        Rating = rating;
    }

    public int getReviews() {
        return Reviews;
    }

    public void setReviews(int reviews) {
        Reviews = reviews;
    }

    public String getFile() {
        return File;
    }

    public void setFile(String file) {
        File = file;
    }

    public int getProgress() {
        return Progress;
    }

    public void setProgress(int progress) {
        Progress = progress;
    }

    public String getNotes() {
        return Notes;
    }

    public void setNotes(String notes) {
        Notes = notes;
    }
}
