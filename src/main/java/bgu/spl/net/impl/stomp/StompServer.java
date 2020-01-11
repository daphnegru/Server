package bgu.spl.net.impl.stomp;

import bgu.spl.net.api.MessageEncoderDecoder;
import bgu.spl.net.api.MessageEncoderDecoderImp;
import bgu.spl.net.api.StompMessagingProtocolImp;
import bgu.spl.net.srv.Server;

public class StompServer {

    public static void main(String[] args) {
        Server.threadPerClient(
                7777,
                ()-> new StompMessagingProtocolImp(),
                ()-> new MessageEncoderDecoderImp<String>() {
                }
        ).serve();

//        Server.reactor(
//                Runtime.getRuntime().availableProcessors(),
//                7777,
//                ()->new StompMessagingProtocolImp(),
//                ()-> new MessageEncoderDecoderImp<>()
//        ).serve();

    }


}
