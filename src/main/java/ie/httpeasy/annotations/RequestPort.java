package ie.httpeasy.annotations;

public @interface RequestPort {
    int port() default 80;
}