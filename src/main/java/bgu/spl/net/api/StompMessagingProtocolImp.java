package bgu.spl.net.api;

import bgu.spl.net.impl.Frames.*;

import bgu.spl.net.impl.User;
import bgu.spl.net.impl.bookClub;
import bgu.spl.net.srv.Connections;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class StompMessagingProtocolImp implements StompMessagingProtocol {
    private int connectionId;
    private Connections connections;
    private boolean terminate;
    private bookClub bookclub;
    private User user;
    private int msgid;

    @Override
    public void start(int connectionId, Connections connections) {
        this.connectionId=connectionId;
        this.connections=connections;
        this.terminate=false;
        bookclub=bookClub.getInstance();
        this.user=null;
        msgid=0;
    }

    @Override
    public void process(String message) {
        int back = message.indexOf('\n');
        if (back==0){
            message=message.substring(1);
        }
        String[] firstrow=message.split("\n",2);
        if(firstrow[0].equals("CONNECT")){
            String [] splitline=firstrow[1].split("\n", 5);
            String version= splitline[0].substring(splitline[0].indexOf(':')+1);
            String host = splitline[1].substring(splitline[1].indexOf(':')+1);
            String user=splitline[2].substring(splitline[2].indexOf(':')+1);
            String password=splitline[3].substring(splitline[3].indexOf(':')+1);
            int answer= bookclub.login(connectionId,user,password);
            sendConnect(answer,version);
            msgid++;

        }
        if(user!=null) {
            switch (firstrow[0]) {
                case "SUBSCRIBE":
                    String[] subline = firstrow[1].split("\n", 4);
                    String dest = subline[0].substring(subline[0].indexOf(':') + 1);
                    String id = subline[1].substring(subline[1].indexOf(':') + 1);
                    int i = Integer.parseInt(id);
                    String subReceipt = subline[2].substring(subline[2].indexOf(':') + 1);
                    bookclub.joingenre(dest, connectionId, user);
                    subscribeFrame n = new subscribeFrame(subReceipt);
                    connections.send(connectionId, n.toString());

                    msgid++;
                    break;

                case "SEND":
                    String[] sendline = firstrow[1].split("\n", 3);
                    String destSend = sendline[0].substring(sendline[0].indexOf(':') + 1);
                    String msg = sendline[1];
                    boolean has = false;
                    User toBorrow;
                    if (msg.contains("has added")){
                        int last = msg.lastIndexOf(' ') + 1;
                        String bookName = msg.substring(last);
                        user.addBook(destSend,bookName);
                    }
                    if (msg.contains("wish")){
                        int last = msg.lastIndexOf(' ') + 1;
                        String bookName = msg.substring(last);
                        has = user.hasBook(destSend,bookName);
                    }
                    if (has){
                        toBorrow = user;
                    }
                    if (msg.contains("taking")){
                        int first = msg.indexOf(' ') +1;
                        String bookName = msg.substring(first);
                        first = bookName.indexOf(' ');
                        bookName = bookName.substring(0,first);
                        user.removeBook(destSend,bookName);
                    }
                    if (msg.contains("Returning")){
                        int first = msg.indexOf(' ') +1;
                        String bookName = msg.substring(first);
                        first = bookName.indexOf(' ');
                        bookName = bookName.substring(0,first);
                        user.addBook(destSend,bookName);
                    }
                    if (msg.contains("status")){

                    }
                    int subid = bookClub.getInstance().getGenreId(destSend,user);
                    sendFrame send = new sendFrame(destSend,msg,subid,msgid);
                    connections.send(destSend,send.toString());
                    msgid++;
                    break;

                case "UNSUBSCRIBE":
                    String[] unsubline = firstrow[1].split("\n", 3);
                    String unsub = unsubline[1];
                    unsub = unsub.substring(unsub.indexOf(':')+1);
//                    String unsub = unsubline[1].substring(unsubline[0].indexOf(':'+1));
                    int k = Integer.parseInt(unsub);
                    String s = bookclub.exitgenre(k,user);
                    if (s != null) {
                        connections.send(connectionId, new unsubscribeFrame(k).toString());
                        msgid++;
                    }
                    break;

                case "DISCONNECT":
                    String[] disReceipt = firstrow[1].split("\n", 2);
                    String disRec = disReceipt[0].substring(disReceipt[0].indexOf(':')+1);
                    int rec = Integer.parseInt(disRec);
                    bookclub.logout(user);
                    disconnectFrame disconnectFrame = new disconnectFrame(rec);
                    connections.send(connectionId, disconnectFrame.toString());
//                    terminate = true;
                    connections.disconnect(connectionId);
                    terminate=true;
                    msgid++;
                    break;
            }
        }
        else {
            connections.send(connectionId, new notconnectedFrame("User not connected"));
            terminate=true;
            connections.disconnect(connectionId);
        }
    }

    @Override
    public boolean shouldTerminate() {
        return terminate;
    }

    public void sendConnect(int ans, String version){
        if (ans ==0){
            //succeded
            user = bookclub.getUser(connectionId);
            connectframe n= new connectframe(version);
            connections.send(connectionId,n.toString());
            user=bookclub.getUser(connectionId);
        }
        if (ans == 1){
            errorFrame n= new errorFrame("Wrong password",version);
            connections.send(connectionId,n.toString());
            terminate=true;
            connections.disconnect(connectionId);
        }
        if (ans == 2){
            errorFrame n= new errorFrame("User already logged in",version);
            connections.send(connectionId,n.toString());
            terminate=true;
            connections.disconnect(connectionId);
        }
    }

}