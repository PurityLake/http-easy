package ie.httpeasy.annotations;

public @interface RequestMethod {
    String method() default "GET";
}