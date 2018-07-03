package com.gcappslab.bookipedia.Library.Objects;

/**
 * User object
 */

public class User {

    private int userId;
    private String email;
    private String language;
    private String favGenres;
    private int lastDevice;

    public User() {
    }

    public User(String email, String language, String favGenres) {
        this.email = email;
        this.language = language;
        this.favGenres = favGenres;
    }

    public User(int userId, String email, String language, String favGenres) {
        this.userId = userId;
        this.email = email;
        this.language = language;
        this.favGenres = favGenres;
    }

    public User(int userId, String email, String language, String favGenres, int lastDevice) {
        this.userId = userId;
        this.email = email;
        this.language = language;
        this.favGenres = favGenres;
        this.lastDevice = lastDevice;
    }


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getFavGenres() {
        return favGenres;
    }

    public void setFavGenres(String favGenres) {
        this.favGenres = favGenres;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getLastDevice() {
        return lastDevice;
    }

    public void setLastDevice(int lastDevice) {
        this.lastDevice = lastDevice;
    }
}
