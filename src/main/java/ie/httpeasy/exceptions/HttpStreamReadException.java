package ie.httpeasy.exceptions;

public class HttpStreamReadException extends HttpException {
    private static final long serialVersionUID = 3977993434528506157L;

    public HttpStreamReadException(String message, Throwable err) {
        super(message, err);
    }
}