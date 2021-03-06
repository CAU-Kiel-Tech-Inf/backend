package sc.server.network;

import org.jetbrains.annotations.NotNull;
import sc.networking.TcpNetwork;
import sc.networking.clients.XStreamClient;
import sc.protocol.ProtocolPacket;

import java.io.IOException;
import java.net.Socket;

public class TestTcpClient extends XStreamClient {
  public TestTcpClient(Socket socket) throws IOException {
    super(new TcpNetwork(socket));
  }

  @Override
  protected void onObject(@NotNull ProtocolPacket message) {
    // ignore it
    // LoggerFactory.getLogger(this.getClass()).debug("Received: {}", o);
  }

}
