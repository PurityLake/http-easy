/*
 * This Java source file was generated by the Gradle 'init' task.
 */
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

    public HttpRequest(String serverName) throws IOException {
        this(serverName, 80);
    }
    public HttpRequest(String serverName, int port) throws IOException {
        System.out.printf("Connecting to '%s' on port %d\n", serverName, port);
        try {
            client = new Socket(serverName, port);
            this.port = port;
            OutputStream tempout = client.getOutputStream();
            InputStream tempin = client.getInputStream();
            toServer = new DataOutputStream(tempout);
            fromServer = new DataInputStream(tempin);
            location = "/";
        } catch (IOException e) {
            client = null;
            this.port = -1;
            toServer = null;
            fromServer = null;
            System.err.println("Connection failed!");
            throw e;
        }
    }

    protected void finalize() throws Throwable {
        port = -1;
        if (fromServer != null) fromServer.close();
        if (toServer != null) toServer.close();
        if (client != null) client.close();
    }

    public HttpRequest setLocation(String location) {
        this.location = location;
        return this;
    }

    public HttpRequest get() {
        method = "GET";
        return this;
    }

    public HttpRequest process() throws IOException {
        if (!location.isEmpty() && !method.isEmpty()) {
            try {
                toServer.writeBytes(method + " " + location + " \r\n\r\n");
            } catch (IOException e) {
                System.err.println("Failed to write to output stream!");
                throw e;
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
                System.err.println("Failed to read from input stream!");
                throw e;
            }
            storedResult = builder.toString();
        }
        return this;
    }

    public String getStored() {
        return storedResult;
    }

    public boolean isConnectionSuccessful() {
        return (client != null) && (port > 0);
    }

    public boolean isConnectionFailed() {
        return !isConnectionSuccessful();
    }
}
