package com.sz1358.transpixel;

public class User {
    private int id, lang;
    private String username, email;

    public User(int id, String username, String email, int lang) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.lang = lang;
    }

    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public int getLang() {
        return lang;
    }
}
