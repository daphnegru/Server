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
        String s="MESSAGE"+"\n"+"subscription:"+subscription+"\n"+"Message-id:"+msgId+"\n"+"destination:"+dest+ "\n" + '\n' +msg;
        return s;
    }
}