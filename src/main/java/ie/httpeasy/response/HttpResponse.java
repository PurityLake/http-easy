package ie.httpeasy.response;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import ie.httpeasy.request.HttpRequest;

public class HttpResponse {
    private String response, message;
    private Map<String, ArrayList<String>> responseDict;
    private String httpVersion, responseCode, responseType;

    public HttpResponse(HttpRequest request) {
        String stored = request.getStored();
        StringBuilder builder = new StringBuilder(1024);
        boolean lastCLRF = false;
        int i;
        char c;
        for (i = 0; i < stored.length(); ++i) {
            c = stored.charAt(i);
            if (c == '\r' && i + 1 < stored.length() && stored.charAt(i+1) == '\n') {
                if (lastCLRF) {
                    i += 2;
                    break;
                }
                builder.append("\r\n");
                ++i;
                lastCLRF = true;
                continue;
            } else {
                lastCLRF = false;
                builder.append(c);
            }
        }
        response = builder.toString();
        message = stored.substring(i);

        responseDict = new HashMap<>();
        String[] splitResponse = response.split("\r\n");
        for (String s : splitResponse) {
            int idx = s.indexOf(":");
            if (idx >= 0) {
                String key = s.substring(0, idx);
                String valueStr = s.substring(idx + 1).trim();
                String[] valueArr = valueStr.split(";");
                ArrayList<String> value = new ArrayList<>();
                for (String val : valueArr) {
                    value.add(val.trim());
                }
                responseDict.put(key, value);
            } else {
                String[] split = s.split(" ");
                if (split.length == 3) {
                    httpVersion = split[0];
                    responseCode = split[1];
                    responseType = split[2];
                }
            }
        }
    }

    public boolean isOK() {
        return responseCode.equals("200");
    }

    public String httpVersion() {
        return httpVersion;
    }

    public String responeCode() {
        return responseCode;
    }

    public String responseType() {
        return responseType;
    }

    public String getResponseString() {
        return response;
    }

    public String getResponseMessage() {
        return message;
    }
}