/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
package name.antonsmirnov.javafx.stalker.log.factory;

import java.text.SimpleDateFormat;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.util.Callback;
import name.antonsmirnov.javafx.stalker.log.LogRecord;

/**
 * Formats date-time for usage in table cell 
 * 
 * @author adept
 */
public class LogDateTimeCellValueFactory
    implements Callback<CellDataFeatures<LogRecord, String>, ObservableValue<String>> {

    private SimpleDateFormat format;
    
    public LogDateTimeCellValueFactory(String format) {
        this.format = new SimpleDateFormat(format);
    }   

    public ObservableValue<String> call(CellDataFeatures<LogRecord, String> p) {
        StringProperty stringProperty = new SimpleStringProperty();
        stringProperty.set(format.format(p.getValue().getTime()));
        return stringProperty;
    }    
}
