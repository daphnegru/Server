package bgu.spl.net.impl.Frames;

public class subscribeFrame implements frame {
    String receipt;


    public subscribeFrame(String receipt){
        this.receipt=receipt;
    }
    public String toString(){
        int i = Integer.parseInt(receipt);
        String s= "RECEIPT"+'\n'+ "receipt-id:" + i + '\n';
        return s;
    }

}