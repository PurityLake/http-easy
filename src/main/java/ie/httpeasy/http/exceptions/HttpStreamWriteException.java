package ie.httpeasy.http.exceptions;

public class HttpStreamWriteException extends HttpException {
    private static final long serialVersionUID = -9122067624072211280L;

    public HttpStreamWriteException(String message, Throwable err) {
        super(message, err);
    }
}