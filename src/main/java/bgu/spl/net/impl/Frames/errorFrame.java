package bgu.spl.net.impl.Frames;

public class errorFrame implements frame {
    String msg;
    String version;

    public errorFrame(String version, String msg){
        this.msg=msg;
        this.version=version;
    }

    public String toString(){
        String s="ERROR"+'\n'+ msg +"\n"+version+'\n';
        return s;
    }
}
