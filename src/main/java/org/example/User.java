package org.example;
public class User {
    public int id;
    public String name;
    public String email;

    public User() {} // Needed for Gson

    public User(int id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }
}
