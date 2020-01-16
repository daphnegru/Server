package bgu.spl.net.api;

import bgu.spl.net.impl.Frames.*;

import bgu.spl.net.impl.User;
import bgu.spl.net.impl.bookClub;
import bgu.spl.net.srv.Connections;


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
                    bookclub.joingenre(dest, i, user);
                    subscribeFrame n = new subscribeFrame(subReceipt);
                    connections.send(connectionId, n.toString());
                    msgid++;
                    break;

                case "SEND":
                    String[] sendline = firstrow[1].split("\n", 4);
                    String destSend = sendline[0].substring(sendline[0].indexOf(':') + 1);
                    String msg = sendline[2];
                    if (msg.contains("has added")){
                        String[] toAdd = msg.split(" ", 6);
                        String bookName = toAdd[5];
                        msg = user.getUsername() + " has added the book " + bookName;
                        int subid = bookClub.getInstance().getGenreId(destSend,user);
                        user.addBook(destSend,bookName);
                        sendFrame send = new sendFrame(destSend,msg,subid,msgid);
                        connections.send(destSend,send.toString());
                    }
                    if (msg.contains("wish")){
                        String[] toAdd = msg.split(" ", 5);
                        String bookName = toAdd[4];
                        msg = user.getUsername() + " wish to borrow " +bookName;
                        int subid = bookClub.getInstance().getGenreId(destSend,user);
                        sendFrame send = new sendFrame(destSend,msg,subid,msgid);
                        connections.send(destSend,send.toString());
                    }
                    if (msg.contains("has") && !msg.contains("added")){
                        String[] toAdd = msg.split(" ", 3);
                        String bookName = toAdd[2];
                        msg = user.getUsername() + " has " +bookName;
                        int subid = bookClub.getInstance().getGenreId(destSend,user);
                        sendFrame send = new sendFrame(destSend,msg,subid,msgid);
                        connections.send(destSend,send.toString());
                    }
                    if (msg.contains("Taking")){
                        String[] toAdd = msg.split(" ");
                        String bookName="";
                        for (int j = 1;j<toAdd.length-2;j++){
                            bookName=bookName+toAdd[j]+" ";
                        }
                        bookName=bookName.substring(0,bookName.lastIndexOf(' '));
                        int last = msg.lastIndexOf(' ');
                        String userName = msg.substring(last + 1);
                        user.removeBook(destSend,bookName);
                        msg = "Taking " + bookName + " from " + userName;
                        int subid = bookClub.getInstance().getGenreId(destSend,user);
                        sendFrame send = new sendFrame(destSend,msg,subid,msgid);
                        connections.send(destSend,send.toString());
                    }
                    if (msg.contains("Returning")){
                        String[] toAdd = msg.split(" ");
                        String bookName="";
                        for (int j = 1;j<toAdd.length-2;j++){
                            bookName=bookName+toAdd[j]+" ";
                        }
                        bookName=bookName.substring(0,bookName.lastIndexOf(' '));
                        int last = msg.lastIndexOf(' ');
                        String userName = msg.substring(last+1);
                        msg = "Returning " + bookName + " to " + userName;
                        int subid = bookClub.getInstance().getGenreId(destSend,user);
                        user.addBook(destSend,bookName);
                        sendFrame send = new sendFrame(destSend,msg,subid,msgid);
                        connections.send(destSend,send.toString());
                    }
                    if (msg.contains("status")){
                        int subid = bookClub.getInstance().getGenreId(destSend,user);
                        sendFrame send = new sendFrame(destSend,msg,subid,msgid);
                        connections.send(destSend,send.toString());
                    }
                    if (msg.contains(":")) {
                        int subid = bookClub.getInstance().getGenreId(destSend,user);
                        sendFrame send = new sendFrame(destSend,msg,subid,msgid);
                        connections.send(destSend,send.toString());
                    }
                    msgid++;
                    break;

                case "UNSUBSCRIBE":
                    String[] unsubline = firstrow[1].split("\n", 3);
                    String unsub = unsubline[0];
                    String reci=unsubline[1];
                    unsub = unsub.substring(unsub.indexOf(':')+1);
                    reci=reci.substring(reci.indexOf(":")+1);
                    int m= Integer.parseInt(unsub);
                    int k = Integer.parseInt(reci);
                    String s = bookclub.exitgenre(m,user);
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
            errorFrame n= new errorFrame(version,"Wrong password");
            connections.send(connectionId,n.toString());
            terminate=true;
            connections.disconnect(connectionId);
        }
        if (ans == 2){
            errorFrame n= new errorFrame(version,"User already logged in");
            connections.send(connectionId,n.toString());
            terminate=true;
            connections.disconnect(connectionId);
        }
    }
}