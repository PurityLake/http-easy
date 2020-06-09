package ie.httpeasy.http.request;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.*;
import java.util.ArrayList;
import java.util.Optional;

import ie.httpeasy.interfaces.Request;
import ie.httpeasy.annotations.*;
import ie.httpeasy.http.exceptions.HttpConnectionException;
import ie.httpeasy.http.exceptions.HttpException;
import ie.httpeasy.http.exceptions.HttpStreamReadException;
import ie.httpeasy.http.exceptions.HttpStreamWriteException;
import ie.httpeasy.utils.MutablePair;
import ie.httpeasy.utils.RequestFormatter;

/**
 * HttpRequest stores and executes a GET, POST, UPDATE, DELETE requests.
 * Most methods returns the object after an operation to allow for chaining
 * of methods.
 * Example Usage
 * <pre>{@code
 * HttpRequest example = new HttpRequest("www.google.com")
 *      .setpath("/")
 *      .methodGET()
 *      .process();
 * System.out.println(example.getStored());
 * }</pre>
 */
@RequestTag
public class HttpRequest implements Request {
    public static final String HTTP_VERSION_1 = "HTTP/1";
    public static final String HTTP_VERSION_1_1 = "HTTP/1.1";
    public static final String METHOD = "#method";
    public static final String VERSION = "#version";
    public static final String PATH = "#path";
    public static final String PORT = "#port";
    public static final String GET = "GET";
    public static final String POST = "POST";

    private Socket client;
    private OutputStream out;
    private InputStream in;

    @RequestVersion
    private String version;

    @RequestMethod
    private String method;

    @RequestPort
    private int port;

    @RequestPath
    private String path;
    
    @RequestMessage
    private String storedResult;

    @RequestHeaders
    private ArrayList<MutablePair<String, String>> headers;

    /**
     * Takes a url e.g. {@code www.google.com} and defaults to port 80.
     * @param url The url to be reached by the request. 
     * @throws HttpException Throws in there was an error creating the socket.
     */
    public HttpRequest(final String url) throws HttpException {
        this(url, 80);
    }

    /**
     * Takes a url e.g. {@code www.google.com} and port to connect to.
     * @param url The url to be reached by the request
     * @param port The port to make the request on
     * @throws HttpException Throws in there was an error creating the socket.
     */
    public HttpRequest(final String url, final int port) throws HttpException {
        try {
            client = new Socket(url, port);
            version = "HTTP/1.1";
            this.port = port;
            out = new DataOutputStream(client.getOutputStream());
            in = new DataInputStream(client.getInputStream());
            path = "/";
            method = "GET";
            storedResult = "";
            headers = new ArrayList<>();
        } catch (final IOException e) {
            client = null;
            this.port = -1;
            out = null;
            in = null;
            throw new HttpConnectionException("Conection failed!", e);
        }
    }

    @Override
    public HttpRequest addHeader(final String key, final String value) {
        final MutablePair<String, String> temp = new MutablePair<>(key, value);
        if (!headers.contains(temp)) {
            headers.add(temp);
        }
        return this;
    }

    @Override
    public HttpRequest removeHeader(final String key) {
        for (int i = 0; i < headers.size(); ++i) {
            if (key.equals(headers.get(i).key())) {
                headers.remove(i);
                break;
            }
        }
        return this;
    }

    @Override
    public HttpRequest editHeader(final String key, final String value) {
        for (int i = 0; i < headers.size(); ++i) {
            final MutablePair<String, String> temp = headers.get(i);
            if (temp.key().equals(key)) {
                temp.value(value);
                break;
            }
        }
        return this;
    }

    @Override
    public HttpRequest editOrAddHeader(final String key, final String value) {
        boolean added = false;
        for (int i = 0; i < headers.size(); ++i) {
            final MutablePair<String, String> temp = headers.get(i);
            if (temp.key().equals(key)) {
                temp.value(value);
                added = true;
                break;
            }
        }
        if (!added) {
            headers.add(new MutablePair<String, String>(key, value));
        }
        return this;
    }

    /**
     * Processes the request object and stores the message in the class before
     * it is passed onto a @{code HttpResponse} object.
     * @throws HttpException Throws if there was an error reading from or writing to the server
     * @return The current object after thr process is finished
     */
    @Override
    public HttpRequest process() throws HttpException {
        if (!path.isEmpty() && !method.isEmpty()) {
            try {
                out.write(RequestFormatter.requestToString(this).getBytes());
            } catch (final IOException e) {
                throw new HttpStreamWriteException(
                    "Failed to write to output stream!", e);
            }
            final StringBuilder builder = new StringBuilder(1024);
            boolean test = false;
            int i = 0;
            try {
                test = false;
                char c;
                do {
                    c = (char)in.read();
                    builder.append(c);
                    ++i;
                    if (i > 4) {
                        final int len = builder.length() - 1;
                        test = builder.charAt(len - 3) == '\r' && builder.charAt(len - 2) == '\n' && builder.charAt(len - 1) == '\r' && builder.charAt(len - 0) == '\n';
                    }
                } while(c != '\0' && !test);
            } catch (final EOFException e) {
                
            } catch (final IOException e) {
                throw new HttpStreamReadException(
                    "Failed to read from input stream!", e);
            }
            storedResult = builder.toString();
        }
        return this;
    }

    /**
     * Returns the message that the request object recieved after {@code process()}
     * @return The message received after <pre>process</pre>
     */
    @Override
    public String getStored() {
        return storedResult;
    }

    /**
     * Whether the connection was successful
     * @return If connection was successful
     */
    @Override
    public boolean isConnectionSuccessful() {
        return (client != null) && (port > 0);
    }

    /**
     * Whether the connection was unsuccessful
     * @return If connection was unsucessful
     */
    @Override
    public boolean isConnectionFailed() {
        return !isConnectionSuccessful();
    }

    @Override
    public Optional<String> get(final String val) {
        switch (val) {
            case VERSION:
                return Optional.of(version);
            case PATH:
                return Optional.of(path);
            case METHOD:
                return Optional.of(method);
            case PORT:
                return Optional.of(Integer.toString(port));
            default:
                for (final MutablePair<String, String> i : headers) {
                    if (i.key().equals(val)) {
                        return Optional.of(i.value());
                    }
                }
        }
        return Optional.empty();
    }

    @Override
    public Request set(final String key, final String value) {
        switch (key) {
            case VERSION:
                version = value;
                break;
            case PATH:
                path = value;
                break;
            case METHOD:
                method = value;
                break;
            case PORT:
                port = Integer.parseInt(value);
                break;
            default:
                editOrAddHeader(key, value);
                break;
        }
        return this;
    }

    @Override
    public void close() throws IOException {
        port = -1;
        if (in != null) in.close();
        if (out != null) out.close();
        if (client != null) client.close();
        in = null;
        out = null;
        client = null;
    }
}
