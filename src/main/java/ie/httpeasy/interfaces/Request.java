package ie.httpeasy.interfaces;

import java.io.Closeable;
import java.util.Optional;

public interface Request extends Closeable {
    public Request addHeader(String key, String value);
    public Request removeHeader(String key);
    public Request editHeader(String key, String value);
    public Request editOrAddHeader(String key, String value);
    public Request process() throws Exception;
    public String getStored();
    public boolean isConnectionSuccessful();
    public boolean isConnectionFailed();
    public Optional<String> get(String val);
    public Request set(String key, String val);
}