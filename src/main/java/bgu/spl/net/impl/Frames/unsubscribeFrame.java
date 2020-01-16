package bgu.spl.net.impl.Frames;

public class unsubscribeFrame {
    int genre;

    public unsubscribeFrame(int genre){
        this.genre=genre;
    }
    public String toString(){
        String s= "RECEIPT"+'\n'+ "receipt-id:" +genre;
        return s;
    }

}
