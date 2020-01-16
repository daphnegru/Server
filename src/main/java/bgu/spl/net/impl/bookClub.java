package bgu.spl.net.impl;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import javafx.util.Pair;

public class bookClub {
    private Map<String, User> users;
    private Map<String, CopyOnWriteArrayList<Pair<User, Integer>>> genres;
    private static bookClub instance = new bookClub();

    public bookClub() {
        users = new ConcurrentHashMap<>();
        genres = new ConcurrentHashMap<>();
    }
    public static bookClub getInstance() {
        return instance;
    }

    public Map<String, User> getUsers() {
        return users;
    }

    public int login(int cId,  String username,String password) {
        if (users.containsKey(username)) {
            User u = users.get(username);
            if(!u.isLogin()) {
                if (u.getPassword().equals(password)) {
                    u.setLogin(true);
                    u.setUniqueId(cId);
                    return 0;

                }
                else return 1;
            }
            return 2;
        }
        else{
            User newuser= new User(cId,username,password);
            //Integer i = (Integer)cId;
            // String id = i.toString();
            users.put(username,newuser);
            return 0;
        }
    }


    public String joingenre(String genre, int id, User u){
        Pair<User, Integer> p=new Pair<>(u,id);
        if (genres.containsKey(genre)) {
            if (!u.getIds().containsKey(genre)) {
                genres.get(genre).addIfAbsent(p);
                u.addGenre(genre, id);
                return "Joined club " + genre;
            }
        }
        else if (!genres.containsKey(genre)) {
            CopyOnWriteArrayList<Pair<User, Integer>> list=new CopyOnWriteArrayList<>();
            list.add(p);
            genres.putIfAbsent(genre,list);
            u.addGenre(genre,id);
            return "Joined club " + genre;
        }
        return "Already in genre: " + genre;
    }

    public String exitgenre(int genre, User u){
        String ans = "";
        String g = "";
        boolean found = false;
        for (Map.Entry<String, CopyOnWriteArrayList<Pair<User, Integer>>> entry: genres.entrySet()){
            for (Pair<User, Integer> p: entry.getValue()){
                if (p.getValue()==genre && p.getKey()==u){
                    genres.remove(p);
                    found = true;
                    g = entry.getKey();
                }
            }
        }
        if (found){
            ans = g;
        }
        else {
            ans = null;
        }
        return ans;
    }

    public void logout(User u){
        for (CopyOnWriteArrayList<Pair<User,Integer>> list: genres.values()){
            for (Pair <User,Integer> p: list){
                if (p.getKey()==u){
                    list.remove(p);
                }
            }
        }
        u.logOut();
    }


    public User getUser(int id){
        User u = null;
        for (Map.Entry<String,User> user: users.entrySet()){
            if (user.getValue().getUniqueId()==id){
                u=user.getValue();
            }
        }
        return u;
    }

    public int subscription(User u,String genre){
        List<Pair<User, Integer>> l= genres.get(genre);
        for(int i=0;i<l.size();i++){
            if(l.get(i).getKey()==u)
                return l.get(i).getValue();
        }
        return -1;
    }

    public CopyOnWriteArrayList<Pair<User, Integer>> getGenreUsers(String genre){
        for (Map.Entry<String, CopyOnWriteArrayList<Pair<User, Integer>>> entry: genres.entrySet()){
            if (entry.getKey().equals(genre)){
                return entry.getValue();
            }
        }
        return null;
    }

    public int getGenreId(String genre, User u){
        for (Map.Entry<String,Integer> entry: u.getIds().entrySet()){
            if (entry.getKey().equals(genre)){
                return entry.getValue().intValue();
            }
        }
        return -1;
    }
}