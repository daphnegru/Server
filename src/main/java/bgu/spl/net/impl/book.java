package bgu.spl.net.impl;

public class book {
    private String name;
    private String genre;


    public book(String name,String genre){
        this.name=name;
        this.genre=genre;
    }

    public String getName() {
        return name;
    }

    public String getGenre() {
        return genre;
    }
}