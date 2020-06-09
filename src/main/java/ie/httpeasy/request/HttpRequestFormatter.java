package ie.httpeasy.request;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.Objects;

import ie.httpeasy.annotations.Request;
import ie.httpeasy.annotations.RequestHeaders;
import ie.httpeasy.annotations.RequestMethod;
import ie.httpeasy.annotations.RequestPath;
import ie.httpeasy.annotations.RequestPort;
import ie.httpeasy.annotations.RequestVersion;
import ie.httpeasy.utils.MutablePair;

public final class HttpRequestFormatter {
    private static boolean isRequest(Object object) {
        if (Objects.isNull(object)) {
            return false;
        }
        return object.getClass().isAnnotationPresent(Request.class);
    }

    public static String requestToRequestString(Object object) {
        if (isRequest(object)) {
            StringBuilder builder = new StringBuilder(1024);
            Formatter formatter = new Formatter(builder);
            String method = null;
            String location = null;
            String version = null;
            ArrayList<MutablePair<String, String>> headers = null;
            Class<?> clazz = object.getClass();
            for (Field field : clazz.getDeclaredFields()) {
                field.setAccessible(true);
                try {
                    if (field.isAnnotationPresent(RequestPath.class)) {
                        location = (String)field.get(object);
                    } else if (field.isAnnotationPresent(RequestMethod.class)) {
                        method = (String)field.get(object);
                    } else if (field.isAnnotationPresent(RequestVersion.class)) {
                        version = (String)field.get(object);
                    } else if (field.isAnnotationPresent(RequestHeaders.class)) {
                        headers = (ArrayList<MutablePair<String, String>>)field.get(object);
                    }
                } catch (IllegalAccessException e) {

                }
            }
            if (method != null && location != null && version != null) {
                formatter.format("%s %s %s\n", method, location, version);
                for (MutablePair<String,String> header : headers) {
                    formatter.format("%s: %s\n", header.key(), header.value());
                }
                builder.append("\r\n\r\n");
                formatter.close();
                return builder.toString();
            }
            formatter.close();
        }
        return null;
    }
}