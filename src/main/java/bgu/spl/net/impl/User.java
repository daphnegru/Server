package bgu.spl.net.impl;

import java.awt.print.Book;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class User {
    private int uniqueId;
    private String username;
    private String password;
    private boolean login;
    private Map<String, ArrayList<Book>> genre;

    public User(int uniqueId,String username,String password) {
        genre= new ConcurrentHashMap<>();
        this.uniqueId=uniqueId;
        this.username=username;
        this.password=password;
        login=true;

    }


    public int getUniqueId() {
        return uniqueId;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public boolean isLogin() {
        return login;
    }

    public Map<String, ArrayList<Book>> getGenre() {
        return genre;
    }

    public void setLogin(boolean login) {
        this.login = login;
    }

    public void addGenre(String g){
        genre.put(g,null);
    }

    public void leaveGenre(String g){
        genre.remove(g);
    }

    public void logOut(){
        genre.clear();
        login = false;
        uniqueId=-1;
    }
}