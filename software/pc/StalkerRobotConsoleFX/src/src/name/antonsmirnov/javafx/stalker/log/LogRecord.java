/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
package name.antonsmirnov.javafx.stalker.log;

import java.util.Date;
import java.util.GregorianCalendar;

/**
 *
 * @author sas
 */
public class LogRecord {
    
    private Date time;
    private LogRecordType type;
    private String message;

    public Date getTime() {
        return time;
    }

    public LogRecordType getType() {
        return type;
    }

    public String getMessage() {
        return message;
    }

    public LogRecord(String message, LogRecordType type) {
        this.time = GregorianCalendar.getInstance().getTime();
        this.type = type;
        this.message = message;
    }
}