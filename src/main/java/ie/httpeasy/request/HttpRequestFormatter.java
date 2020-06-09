package ie.httpeasy.request;

import java.lang.reflect.Field;
import java.util.Formatter;
import java.util.Objects;

import ie.httpeasy.annotations.Request;
import ie.httpeasy.annotations.RequestMessage;
import ie.httpeasy.annotations.RequestMethod;
import ie.httpeasy.annotations.RequestPath;
import ie.httpeasy.annotations.RequestPort;
import ie.httpeasy.utils.HtmlFormatter;

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
            String storedResult = null;
            Class<?> clazz = object.getClass();
            for (Field field : clazz.getDeclaredFields()) {
                field.setAccessible(true);
                try {
                    if (field.isAnnotationPresent(RequestPath.class)) {
                        location = (String)field.get(object);
                    } else if (field.isAnnotationPresent(RequestMessage.class)) {
                        storedResult = (String)field.get(object);
                    } else if (field.isAnnotationPresent(RequestMethod.class)) {
                        method = (String)field.get(object);
                    }
                } catch (IllegalAccessException e) {
                    
                }
            }
            if (method != null && location != null) {
                formatter.format("%s %s HTTP1.1\n", method, location);
                if (storedResult != null) {
                    builder.append(HtmlFormatter.formatText(storedResult));
                }
                return builder.toString();
            }
        }
        return null;
    }

    public static String requestToString(Object object) {
        if (isRequest(object)) {
            StringBuilder builder = new StringBuilder(1024);
            Formatter formatter = new Formatter(builder);
            String method = null;
            int port = -1;
            String location = null;
            String storedResult = null;
            Class<?> clazz = object.getClass();
            for (Field field : clazz.getDeclaredFields()) {
                field.setAccessible(true);
                try {
                    if (field.isAnnotationPresent(RequestPath.class)) {
                        location = (String)field.get(object);
                    } else if (field.isAnnotationPresent(RequestMessage.class)) {
                        storedResult = (String)field.get(object);
                    } else if (field.isAnnotationPresent(RequestMethod.class)) {
                        method = (String)field.get(object);
                    } else if (field.isAnnotationPresent(RequestPort.class)) {
                        port = field.getInt(object);
                    }
                } catch (IllegalAccessException e) {
                }
            }
            if (method != null && port != -1 && location != null) {
                formatter.format("%s %s (port %d)\n", method, location, port);
                if (storedResult != null) {
                    builder.append(HtmlFormatter.formatText(storedResult));
                }
                return builder.toString();
            }
        }
        return null;
    }
}