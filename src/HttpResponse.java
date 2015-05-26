

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

public class HttpResponse extends HttpPacket {
    private final ResponseCode mResponseCode;
    private HttpHeader mHttpHeader;

    public HttpResponse(ResponseCode code) {
        mResponseCode = code;
        mHttpHeader = HttpHeader.generate(this);
    }

    public ByteBuffer getOutputByteBuffer() {
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        ByteBuffer buffer = ByteBuffer.allocate(4096);
        try {
            String content = "\r\n<html><body><h1>Hello simplest web server</h1></body></html>\r\n";
            b.write("HTTP/1.1 200 OK\r\n".getBytes());
            b.write("Content-Type: text/html; charset=utf-8\r\n".getBytes());
            b.write(content.getBytes());

            buffer.put(b.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return buffer;
    }

    public ResponseCode getResponseCode() {
        return mResponseCode;
    }

    public static enum ResponseCode {
        OK_200(200),
        ;

        public int getCode() {
            return code;
        }

        private int code;
        private ResponseCode(int code) {
            this.code = code;
        }
    }
}
