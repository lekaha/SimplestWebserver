import java.io.InputStream;

public class HttpRequest extends HttpPacket {
    private final HttpHeader mHeader;
    private InputStream mInputStream;

    protected HttpRequest() {
        mHeader = HttpHeader.parse(this);
    }

    public HttpRequest(InputStream inputStream) {
        this();
        mInputStream = inputStream;
    }

    public InputStream getInputStream() {
        return mInputStream;
    }

}
