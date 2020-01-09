package bgu.spl.net.impl.Frames;

public class disconnectFrame {
    int receipt;


    public disconnectFrame(int receipt){
        this.receipt=receipt;
    }
    public String toString(){
        String s= "RECEIPT"+"\n"+receipt+"\u0000";
        return s;
    }
}