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
    private Map<String, ArrayList<book>> genre;
    private Map<String,Integer> ids;

    public User(int uniqueId,String username,String password) {
        genre= new ConcurrentHashMap<>();
        this.uniqueId=uniqueId;
        this.username=username;
        this.password=password;
        login=true;
        ids = new ConcurrentHashMap<>();

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

    public Map<String, ArrayList<book>> getGenre() {
        return genre;
    }


    public void setLogin(boolean login) {
        this.login = login;
    }

    public void addGenre(String g, int id) {
        if (!genre.containsKey(g)) {
            ArrayList<book> list = new ArrayList<>();
            genre.put(g, list);
            ids.putIfAbsent(g, (Integer) id);
        }
        else {
            ArrayList<book> list = genre.get(g);
            genre.remove(g);
            genre.put(g,list);
            ids.putIfAbsent(g, (Integer) id);
        }
    }

    public Map<String,Integer> getIds(){
        return ids;
    }


    public void logOut(){
        genre.clear();
        login = false;
        uniqueId=-1;
    }

    public void addBook(String g, String book) {
        if (genre.get(g) != null) {
            book toAdd = new book(book, g);
            if (!genre.get(g).contains(toAdd)) {
                genre.get(g).add(toAdd);
            }
        }
        else {
            ArrayList<book> genreToAdd = new ArrayList<>();
            book toAdd = new book(book, g);
            genreToAdd.add(toAdd);
            genre.putIfAbsent(g, genreToAdd);
        }
    }

    public void removeBook(String g, String book){
        if (genre.get(g)!=null){
            for (book b: genre.get(g)){
                if (b.getName()==book){
                    genre.get(g).remove(b);
                }
            }
        }
    }

    public boolean hasBook(String g, String book){
        if (genre.get(g)!=null){
            for (book b: genre.get(g)){
                if (b.getName().equals(book)){
                    return true;
                }
            }
        }
        return false;
    }

    public void setUniqueId(int id){
        this.uniqueId=id;
    }
}