package ie.httpeasy.https.exceptions;

public class HttpsException extends Exception {
    private static final long serialVersionUID = 1686003075935888641L;

    public HttpsException(String message, Throwable err) {
        super(message, err);
    }
}