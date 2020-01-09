package bgu.spl.net.impl.Frames;

public class notconnectedFrame {
    String message;

    public notconnectedFrame(String m){
        message=m;
    }
    public String toString(){
        return message;
    }
}