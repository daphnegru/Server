package bgu.spl.net.srv;

import bgu.spl.net.impl.User;
import bgu.spl.net.impl.bookClub;

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
        for(int i=0;i<bookclub.getUsers().size();i++){
            if (bookclub.getUsers().get(i).getGenre().containsKey(channel)){
                connections.get(bookclub.getUsers().get(i).getUniqueId()).send(msg);
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