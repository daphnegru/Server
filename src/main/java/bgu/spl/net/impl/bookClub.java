package bgu.spl.net.impl;

import jdk.internal.net.http.common.Pair;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

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

    public int login(int cId, String password, String username) {
        if (users.containsKey(username)) {
            User u = users.get(users.containsKey(username));
            if(!u.isLogin()) {
                if (u.getPassword() == password) {
                    if (u.getUniqueId() == cId) {
                        u.setLogin(true);
                        return 0;
                    }
                }
                else return 1;
            }
            return 2;
        }
        else{
            User newuser= new User(cId,username,password);
            //check
            String id = ((Integer)cId).toString();
            users.put(id,newuser);
            return 0;
        }
    }
    public String joingenre(String genre, int id, User u){
        Pair<User, Integer> p=new Pair<>(u,id);
        if (genres.containsKey(genre) && !u.getGenre().containsKey(genre)){
            genres.get(genre).add(p);
            u.addGenre(genre);
            return "Joined club " + genre;
        }
        else if (!genres.containsKey(genre)) {
            CopyOnWriteArrayList<Pair<User, Integer>> list=new CopyOnWriteArrayList<>();
            list.add(p);
            genres.put(genre,list);
            u.addGenre(genre);
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
                if (p.second==genre && p.first==u){
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
                if (p.first==u){
                    list.remove(p);
                }
            }
        }
        u.logOut();
    }


    public User getUser(int id){
        User u = null;
        for(int i=0;i<users.size();i++){
            if(users.get(i).getUniqueId()==id)
                u= users.get(i);
        }
        return u;
    }

    public int subscription(User u,String genre){
        List<Pair<User, Integer>> l= genres.get(genre);
        for(int i=0;i<l.size();i++){
            if(l.get(i).first==u)
                return l.get(i).second;
        }
        return -1;
    }


}