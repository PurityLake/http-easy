package ie.httpeasy.exceptions;

public class HttpConnectionException extends HttpException {
    private static final long serialVersionUID = -3354309268655553912L;

    public HttpConnectionException(String message, Throwable err) {
        super(message, err);
    }
}