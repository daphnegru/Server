package bgu.spl.net.impl.Frames;

public class unsubscribeFrame {
    String genre;

    public unsubscribeFrame(String genre){
        this.genre=genre;
    }
    public String toString(){
        String s= "RECEIPT"+"\n"+genre+"\u0000";
        return s;
    }

}
