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
        String[] firstrow=message.split("\n",2);
        if(firstrow[0]=="CONNECT"){
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

                case "SEND":
                    String[] sendline = firstrow[1].split("\n", 3);
                    String destSend = sendline[0].substring(sendline[0].indexOf(':') + 1);
                    String msg = sendline[1];
                    int sub=bookclub.subscription(user,destSend);
                    msgid++;
                    if(sub!=-1) {
                        sendFrame b = new sendFrame(destSend, msg, sub, msgid);
                        connections.send(connectionId, b.toString());
                    }

                    else {
                        String s= "the subscription is not found";
                        connections.send(connectionId, new notconnectedFrame("s"));

                    }
                case "UNSUBSCRIBE":
                    String unsubline = firstrow[1];
                    int k = Integer.parseInt(unsubline);
                    String s = bookclub.exitgenre(k,user);
                    if (s != null) {
                        connections.send(connectionId, new unsubscribeFrame(s).toString());
                        msgid++;
                    }


                case "DISCONNECT":
                    String disReceipt = firstrow[1].substring(':' + 1);
                    int rec = Integer.parseInt(disReceipt);
                    connections.send(connectionId, new disconnectFrame(rec).toString());
                    terminate = true;
                    connections.disconnect(connectionId);
                    msgid++;
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