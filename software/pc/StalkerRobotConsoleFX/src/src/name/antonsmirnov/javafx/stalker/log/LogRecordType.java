/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
package name.antonsmirnov.javafx.stalker.log;

/**
 *
 * @author sas
 */
public enum LogRecordType {    
    INFO("info", "#000000"),
    FROM_DEVICE("<<", "#77C6FF"),
    TO_DEVICE(">>", "#00A321");
    
    private String title;
    private String textColor;

    public String getTitle() {
        return title;
    }

    public String getTextColor() {
        return textColor;
    }

    private LogRecordType(String title, String textColor) {
        this.title = title;
        this.textColor = textColor;
    }
}
