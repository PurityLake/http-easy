package ie.httpeasy.https.exceptions;

public class HttpsStreamWriteException extends HttpsException {
    private static final long serialVersionUID = 1489538264240388998L;

    public HttpsStreamWriteException(String message, Throwable err) {
        super(message, err);
    }
}