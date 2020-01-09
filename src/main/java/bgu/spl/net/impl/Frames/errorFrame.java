package bgu.spl.net.impl.Frames;

public class errorFrame implements frame {
    String msg;
    String version;

    public errorFrame(String version, String msg){
        this.msg=msg;
        this.version=version;
    }

    public String toString(){
        String s= msg+"\n"+version+"\u0000";
        return s;
    }
}
