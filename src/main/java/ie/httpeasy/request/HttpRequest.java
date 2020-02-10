package ie.httpeasy.request;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.*;

import ie.httpeasy.annotations.Request;
import ie.httpeasy.annotations.RequestLocation;
import ie.httpeasy.annotations.RequestMessage;
import ie.httpeasy.annotations.RequestMethod;
import ie.httpeasy.annotations.RequestPort;
import ie.httpeasy.exceptions.HttpConnectionException;
import ie.httpeasy.exceptions.HttpException;
import ie.httpeasy.exceptions.HttpStreamReadException;
import ie.httpeasy.exceptions.HttpStreamWriteException;

/**
 * HttpRequest stores and executes a GET, POST, UPDATE, DELETE requests.
 * Most methods returns the object after an operation to allow for chaining
 * of methods.
 * Example Usage
 * <pre>{@code
 * HttpRequest example = new HttpRequest("www.google.com")
 *      .setLocation("/")
 *      .GET()
 *      .process();
 * System.out.println(example.getStored());
 * }</pre>
 */
@Request
public class HttpRequest {
    private Socket client;
    private DataOutputStream toServer;
    private DataInputStream fromServer;

    @RequestMethod
    private String method;

    @RequestPort
    private int port;

    @RequestLocation
    private String location;
    
    @RequestMessage
    private String storedResult;

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
        System.out.printf("Connecting to '%s' on port %d\n", url, port);
        try {
            client = new Socket(url, port);
            this.port = port;
            OutputStream tempout = client.getOutputStream();
            InputStream tempin = client.getInputStream();
            toServer = new DataOutputStream(tempout);
            fromServer = new DataInputStream(tempin);
            location = "/";
            method = "GET";
            storedResult = "";
        } catch (IOException e) {
            client = null;
            this.port = -1;
            toServer = null;
            fromServer = null;
            throw new HttpConnectionException("Conection failed!", e);
        }
    }
    /**
     * Cleans up the streams and socket when garbage collected.
     */
    protected void finalize() throws Throwable {
        port = -1;
        if (fromServer != null) fromServer.close();
        if (toServer != null) toServer.close();
        if (client != null) client.close();
    }

    /**
     * Sets the location part of the request i.e. the part after the domain name.
     * @param location The location to be requested
     * @return The current object after location is set
     */
    public HttpRequest setLocation(String location) {
        this.location = location;
        return this;
    }

    /**
     * Makes the current HTTP method GET.
     * @return The current object after method is set
     */
    public HttpRequest GET() {
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
        if (!location.isEmpty() && !method.isEmpty()) {
            try {
                toServer.writeBytes(method + " " + location + " \r\n\r\n");
            } catch (IOException e) {
                throw new HttpStreamWriteException(
                    "Failed to write to output stream!", e);
            }
            StringBuilder builder = new StringBuilder(1024);
            try {
                char c;
                do {
                    c = (char)fromServer.readByte();
                    builder.append(c);
                } while(c != '\0');
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
}
