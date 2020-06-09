package ie.httpeasy.http.request;

import java.io.Closeable;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.*;
import java.util.ArrayList;
import java.util.Optional;

import ie.httpeasy.http.annotations.*;
import ie.httpeasy.http.exceptions.HttpConnectionException;
import ie.httpeasy.http.exceptions.HttpException;
import ie.httpeasy.http.exceptions.HttpStreamReadException;
import ie.httpeasy.http.exceptions.HttpStreamWriteException;
import ie.httpeasy.utils.MutablePair;

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
@Request
public class HttpRequest implements Closeable {
    private Socket client;
    private DataOutputStream toServer;
    private DataInputStream fromServer;

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
    public HttpRequest(String url) throws HttpException {
        this(url, 80);
    }

    /**
     * Takes a url e.g. {@code www.google.com} and port to connect to.
     * @param url The url to be reached by the request
     * @param port The port to make the request on
     * @throws HttpException Throws in there was an error creating the socket.
     */
    public HttpRequest(String url, int port) throws HttpException {
        try {
            client = new Socket(url, port);
            version = "HTTP/1.1";
            this.port = port;
            OutputStream tempout = client.getOutputStream();
            InputStream tempin = client.getInputStream();
            toServer = new DataOutputStream(tempout);
            fromServer = new DataInputStream(tempin);
            path = "/";
            method = "GET";
            storedResult = "";
            headers = new ArrayList<>();
        } catch (IOException e) {
            client = null;
            this.port = -1;
            toServer = null;
            fromServer = null;
            throw new HttpConnectionException("Conection failed!", e);
        }
    }

    public HttpRequest HTTP1_1() {
        version = "HTTP/1.1";
        return this;
    }

    public HttpRequest HTTP1() {
        version = "HTTP/1";
        return this;
    }

    public HttpRequest addHeader(String key, String value) {
        MutablePair<String, String> temp = new MutablePair<>(key, value);
        if (!headers.contains(temp)) {
            headers.add(temp);
        }
        return this;
    }

    public HttpRequest removeHeader(String key) {
        for (int i = 0; i < headers.size(); ++i) {
            if (key.equals(headers.get(i).key())) {
                headers.remove(i);
                break;
            }
        }
        return this;
    }

    public HttpRequest editHeader(String key, String value) {
        for (int i = 0; i < headers.size(); ++i) {
            MutablePair<String, String> temp = headers.get(i);
            if (temp.key().equals(key)) {
                temp.value(value);
                break;
            }
        }
        return this;
    }

    public HttpRequest editOrAddHeader(String key, String value) {
        boolean added = false;
        for (int i = 0; i < headers.size(); ++i) {
            MutablePair<String, String> temp = headers.get(i);
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
     * Sets the path part of the request i.e. the part after the domain name.
     * @param path The path to be requested
     * @return The current object after path is set
     */
    public HttpRequest setPath(String path) {
        this.path = path;
        return this;
    }

    /**
     * Makes the current HTTP method GET.
     * @return The current object after method is set
     */
    public HttpRequest methodGET() {
        method = "GET";
        return this;
    }

    /**
     * Processes the request object and stores the message in the class before
     * it is passed onto a @{code HttpResponse} object.
     * @throws HttpException Throws if there was an error reading from or writing to the server
     * @return The current object after thr process is finished
     */
    public HttpRequest process() throws HttpException {
        if (!path.isEmpty() && !method.isEmpty()) {
            try {
                toServer.writeBytes(HttpRequestFormatter.requestToString(this));
            } catch (IOException e) {
                throw new HttpStreamWriteException(
                    "Failed to write to output stream!", e);
            }
            StringBuilder builder = new StringBuilder(1024);
            boolean test = false;
            int i = 0;
            try {
                test = false;
                char c;
                do {
                    c = (char)fromServer.readByte();
                    builder.append(c);
                    ++i;
                    if (i > 4) {
                        int len = builder.length() - 1;
                        test = builder.charAt(len - 3) == '\r' && builder.charAt(len - 2) == '\n' && builder.charAt(len - 1) == '\r' && builder.charAt(len - 0) == '\n';
                    }
                } while(c != '\0' && !test);
            } catch (EOFException e) {
                
            } catch (IOException e) {
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
    public String getStored() {
        return storedResult;
    }

    /**
     * Whether the connection was successful
     * @return If connection was successful
     */
    public boolean isConnectionSuccessful() {
        return (client != null) && (port > 0);
    }

    /**
     * Whether the connection was unsuccessful
     * @return If connection was unsucessful
     */
    public boolean isConnectionFailed() {
        return !isConnectionSuccessful();
    }

    public Optional<String> get(String val) {
        switch (val) {
            case "#version":
                return Optional.of(version);
            case "#path":
                return Optional.of(path);
            case "#method":
                return Optional.of(method);
            case "#port":
                return Optional.of(Integer.toString(port));
            default:
                for (MutablePair<String, String> i : headers) {
                    if (i.key().equals(val)) {
                        return Optional.of(i.value());
                    }
                }
        }
        return Optional.empty();
    }

    @Override
    public void close() throws IOException {
        port = -1;
        if (fromServer != null) fromServer.close();
        if (toServer != null) toServer.close();
        if (client != null) client.close();
        fromServer = null;
        toServer = null;
        client = null;
    }
}
