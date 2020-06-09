package ie.httpeasy.util;

import java.util.logging.Formatter;
import java.util.logging.LogRecord;

public class BasicFormatter extends Formatter {
    @Override
    public String format(LogRecord record) {
        return record.getMessage();
    }
    
}