package ie.httpeasy.http.exceptions;

public class HttpException extends Exception {
    private static final long serialVersionUID = 2505496466976515795L;
    
    public HttpException(String message, Throwable err) {
        super(message, err);
    }
}