package ie.httpeasy.https.request;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Optional;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

import ie.httpeasy.annotations.*;
import ie.httpeasy.https.exceptions.HttpsConnectionException;
import ie.httpeasy.https.exceptions.HttpsException;
import ie.httpeasy.https.exceptions.HttpsStreamReadException;
import ie.httpeasy.https.exceptions.HttpsStreamWriteException;
import ie.httpeasy.interfaces.Request;
import ie.httpeasy.utils.MutablePair;
import ie.httpeasy.utils.RequestFormatter;

import static ie.httpeasy.utils.RequestItems.*;

@RequestTag
public class HttpsRequest implements Request {
    private SSLSocket client;
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

    public HttpsRequest(String url) throws HttpsException {
        this(url, 443);
    }

    public HttpsRequest(String url, int port) throws HttpsException {
        try {
            client = (SSLSocket)SSLSocketFactory.getDefault().createSocket(url, port);
            client.setEnabledProtocols(client.getSSLParameters().getProtocols());
            client.setEnabledCipherSuites(client.getSSLParameters().getCipherSuites());
            out = new DataOutputStream(client.getOutputStream());
            in = new DataInputStream(client.getInputStream());
            this.port = port;
            path = "/";
            method = "GET";
            version = "";
            storedResult = "";
            headers = new ArrayList<>();
        } catch (IOException e) {
            client = null;
            this.port = -1;
            out = null;
            in = null;
            throw new HttpsConnectionException("Conection failed!", e);
        }
    }

    @Override
    public void close() throws IOException {
        if (client != null) client.close();
        if (out != null) out.close();
        if (in != null) in.close();
        this.port = -1;
        client = null;
        out = null;
        in = null;
    }

    @Override
    public Request addHeader(String key, String value) {
        MutablePair<String, String> temp = new MutablePair<>(key, value);
        if (!headers.contains(temp)) {
            headers.add(temp);
        }
        return this;
    }

    @Override
    public Request removeHeader(String key) {
        for (int i = 0; i < headers.size(); ++i) {
            if (key.equals(headers.get(i).key())) {
                headers.remove(i);
                break;
            }
        }
        return this;
    }

    @Override
    public Request editHeader(String key, String value) {
        for (int i = 0; i < headers.size(); ++i) {
            MutablePair<String, String> temp = headers.get(i);
            if (temp.key().equals(key)) {
                temp.value(value);
                break;
            }
        }
        return this;
    }

    @Override
    public Request editOrAddHeader(String key, String value) {
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

    @Override
    public Request process() throws Exception {
        if (!path.isEmpty() && !method.isEmpty()) {
            try {
                out.write(RequestFormatter.requestToString(this).getBytes());
            } catch (IOException e) {
                throw new HttpsStreamWriteException(
                    "Failed to write to output stream!", e);
            }
            StringBuilder builder = new StringBuilder(1024);
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
                        int len = builder.length() - 1;
                        test = builder.charAt(len - 3) == '\r' && builder.charAt(len - 2) == '\n' && builder.charAt(len - 1) == '\r' && builder.charAt(len - 0) == '\n';
                    }
                } while(c != '\0' && !test);
            } catch (EOFException e) {
                
            } catch (IOException e) {
                throw new HttpsStreamReadException(
                    "Failed to read from input stream!", e);
            }
            storedResult = builder.toString();
        }
        return this;
    }

    @Override
    public String getStored() {
        return storedResult;
    }

    @Override
    public boolean isConnectionSuccessful() {
        return (client != null) && (port > 0);
    }

    @Override
    public boolean isConnectionFailed() {
        return !isConnectionSuccessful();
    }

    @Override
    public Optional<String> get(String val) {
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
                for (MutablePair<String, String> i : headers) {
                    if (i.key().equals(val)) {
                        return Optional.of(i.value());
                    }
                }
        }
        return Optional.empty();
    }

    @Override
    public Request set(String key, String val) {
        switch (key) {
            case VERSION:
                version = val;
                break;
            case PATH:
                path = val;
                break;
            case METHOD:
                method = val;
                break;
            case PORT:
                port = Integer.parseInt(val);
                break;
            default:
                editOrAddHeader(key, val);
                break;
        }
        return this;
    }
}