package bgu.spl.net.srv;

import bgu.spl.net.impl.User;
import bgu.spl.net.impl.bookClub;
import javafx.util.Pair;

import java.sql.Connection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class ConnectionsImpl<T> implements Connections<T> {

    private Map<Integer,ConnectionHandler> connections;
    private bookClub bookclub;

    public ConnectionsImpl(){
        connections=new ConcurrentHashMap<>();
        bookclub=bookClub.getInstance();

    }
    @Override
    public boolean send(int connectionId, T msg) {
        if(connections.containsKey(connectionId)){
            connections.get(connectionId).send(msg);
            return true;
        }
        else
            return false;
    }


    @Override
    public void send(String channel, T msg) {
        if (bookclub.getGenreUsers(channel)!=null){
            for (Pair<User,Integer> p: bookclub.getGenreUsers(channel)){
                connections.get(p.getKey().getUniqueId()).send(msg);
            }
        }
    }

    @Override
    public void disconnect(int connectionId) {
        connections.remove(connectionId);
    }

    public void connect(int id,ConnectionHandler connectionHandler){
        connections.put(id,connectionHandler);
    }

}