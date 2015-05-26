import java.io.ByteArrayInputStream;
import java.nio.ByteBuffer;

public class HttpServer extends Server {
    private HttpServerHandler mHttpServerHandler;

    public HttpServer() {
        mHttpServerHandler = new HttpServerHandler();
    }

    protected void start() {
        super.start(mHttpServerHandler);

    }

    @Override
    public void start(final ServerHandler handler) {
        super.start(mHttpServerHandler);
    }

    private class HttpServerHandler implements ServerHandler<HttpPacket> {

        @Override
        public HttpRequest handleReceived(ByteBuffer buffer) {
            System.out.println(new String(buffer.array()));
//            HttpRequest req = new HttpRequest(new ByteArrayInputStream(buffer.array()));
//            return req;
            return null;
        }

        @Override
        public ByteBuffer handleSent(HttpPacket response) {
            HttpResponse rep = new HttpResponse(HttpResponse.ResponseCode.OK_200);
            return rep.getOutputByteBuffer();
        }
    }

}
