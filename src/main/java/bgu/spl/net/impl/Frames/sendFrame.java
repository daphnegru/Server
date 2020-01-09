package bgu.spl.net.impl.Frames;

public class sendFrame {
    String msg;
    int msgId;
    String dest;
    int subscription;
    public sendFrame(String dest,String msg,int subscription,int msgId){
        this.dest=dest;
        this.msg=msg;
        this.msgId=msgId;
        this.subscription=subscription;
    }
    public String toString(){
        String s="MESSAGE"+"\n"+subscription+"\n"+msgId+"\n"+dest+"\n"+msg+"\u0000";
        return s;
    }
}