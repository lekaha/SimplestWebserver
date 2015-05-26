import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

public class Server {
    private int mPort = 8080;
    private final ServerLooper mServerLooper;

    private ServerHandler mServerHandler;

    public Server() {
        mServerLooper = new ServerLooper();
    }

    public Server(int port) {
        this();
        mPort = port;
    }

    public void start(final ServerHandler handler) {
        mServerHandler = handler;
        new Thread(mServerLooper).start();
    }

    public interface ServerHandler<T> {
        public T handleReceived(ByteBuffer buffer);
        public ByteBuffer handleSent(T t);
    }

    private class ServerLooper implements Runnable {

        @Override
        public void run() {
            try {
                ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
                ServerSocket serverSocket = serverSocketChannel.socket();
                Selector selector = Selector.open();
                InetSocketAddress localPort = new InetSocketAddress(mPort);
                serverSocket.bind(localPort);
                serverSocketChannel.configureBlocking(false);
                serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

                System.out.println("Server is bound? " + serverSocket.isBound());
                System.out.println("Server port? " + serverSocket.getLocalPort());

                while (true) {
                    selector.select();
                    Iterator keys = selector.selectedKeys().iterator();
                    while(keys.hasNext()) {
                        SelectionKey key = (SelectionKey) keys.next();
                        keys.remove();
                        try {
                            if (key.isAcceptable()) {
                                ServerSocketChannel server = (ServerSocketChannel) key.channel();
                                SocketChannel channel = server.accept();
                                channel.configureBlocking(false);
                                channel.register(selector, SelectionKey.OP_READ);
                            }
                            else if (key.isReadable()) {
                                //parse header
                                SocketChannel channel = (SocketChannel) key.channel();
                                ByteBuffer buffer = ByteBuffer.allocate(4096);
                                channel.read(buffer);
                                Object obj = null;
                                if (null != mServerHandler) {
                                    obj = mServerHandler.handleReceived(buffer);
                                }
                                key.attach(obj); //attach object
                                key.interestOps(SelectionKey.OP_WRITE);
                            }
                            else if (key.isWritable()) {
                                SocketChannel channel = (SocketChannel) key.channel();
                                Object obj = key.attachment();
                                if (null != mServerHandler) {
                                    ByteBuffer buffer = mServerHandler.handleSent(obj);
                                    buffer.flip();
                                    channel.write(buffer);
                                    System.out.println(new String(buffer.array()));
                                    channel.close();
                                }

                            }
                        }
                        catch (IOException e) {
                            key.cancel();
                            key.channel().close();
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
