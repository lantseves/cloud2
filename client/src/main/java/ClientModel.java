import main.java.message.AuthorizationMessage;
import netty.NettyClient;

public class ClientModel {
    private NettyClient nettyClient ;

    public ClientModel() {
        this.nettyClient = new NettyClient();
    }
}
