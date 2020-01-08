package bgu.spl.net.api;

import bgu.spl.net.impl.User;
import bgu.spl.net.impl.bookClub;
import bgu.spl.net.srv.Connections;

public class StompMessagingProtocolImp implements StompMessagingProtocol{
    private int connectionId;
    private Connections<String> connections;
    private boolean terminate;
    private bookClub bookclub;
    private User user;
    @Override
    public void start(int connectionId, Connections<String> connections) {
        this.connectionId=connectionId;
        this.connections=connections;
        this.terminate=false;
        bookclub=bookClub.getInstance();
        user=null;
    }

    @Override
    public void process(String message) {
        String[] firstrow=message.split("\n",2);
        switch (firstrow[0]){
            case"CONNECT":
                String [] splitline=firstrow[1].split("\n", 4);

                String version= splitline[0].substring(splitline[0].indexOf(':')+1);
                String host = splitline[1].substring(splitline[1].indexOf(':')+1);
                String user=splitline[2].substring(splitline[2].indexOf(':')+1);
                String password=splitline[3].substring(splitline[3].indexOf(':')+1);
                int answer= bookclub.login(connectionId,user,password);
                sendConnect(answer);
                // frame
            case"SUBSCRIBE":
                String [] subline= firstrow[1].split("\n", 3);
                String dest = subline[0].substring(subline[0].indexOf(':')+1);
                String id = subline[1].substring(subline[1].indexOf(':')+1);
                int i = Integer.parseInt(id);
                String subReceipt = subline[2].substring(subline[2].indexOf(':')+1);
//                bookclub.joingenre(dest,);
            case"SEND":
                String[] sendline = firstrow[1].split("\n", 2);
                String destSend = sendline[0].substring(sendline[0].indexOf(':')+1);
                String msg = sendline[1];

            case"UNSUBSCRIBE":
                String unsubline = firstrow[1];

            case"DISCONNECT":
                String disReceipt = firstrow[1].substring(':'+1);

            case"RECEIPT":
                String receiptId = firstrow[1].substring(firstrow[1].indexOf(':')+1);
        }

    }

    @Override
    public boolean shouldTerminate() {
        return false;
    }

    public void sendConnect(int ans){
        if (ans ==0){
            //succeded
            user = bookclub.getUser(connectionId);
//            connections.send(connectionId, new);
        }
        if (ans == 1){
            //wrong password

        }
        if (ans == 2){
            //already logged in

        }
    }
}