package ie.httpeasy.https.request;

import java.io.IOException;
import java.util.Optional;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

import ie.httpeasy.https.exceptions.HttpsException;
import ie.httpeasy.interfaces.Request;

public class HttpsRequest implements Request {
    private static final String[] protocols = new String[] { "TLSv1.3" };
    private static final String[] ciphers = new String[] { "TLS_AES_128_GCM_SHA256" };

    private SSLSocket client;

    public HttpsRequest(String url) throws HttpsException {
        this(url, 443);
    }

    public HttpsRequest(String url, int port) throws HttpsException {
        try {
            client = (SSLSocket) SSLSocketFactory.getDefault().createSocket(url, port);
            client.setEnabledProtocols(protocols);
            client.setEnabledCipherSuites(ciphers);
        } catch (IOException e) {

        }
    }

    @Override
    public void close() throws IOException {
        // TODO Auto-generated method stub

    }

    @Override
    public Request addHeader(String key, String value) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Request removeHeader(String key) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Request editHeader(String key, String value) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Request editOrAddHeader(String key, String value) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Request process() throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getStored() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean isConnectionSuccessful() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean isConnectionFailed() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public Optional<String> get(String val) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Request set(String key, String val) {
        // TODO Auto-generated method stub
        return null;
    }
}