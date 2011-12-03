/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
package name.antonsmirnov.javafx.stalker.log.factory;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;
import name.antonsmirnov.javafx.stalker.log.LogRecord;

/**
 *
 * @author adept
 */
public class LogRecordTypeCellValueFactory
        implements Callback<TableColumn.CellDataFeatures<LogRecord, String>, ObservableValue<String>> {

    public ObservableValue<String> call(TableColumn.CellDataFeatures<LogRecord, String> p) {
        StringProperty stringProperty = new SimpleStringProperty();
        stringProperty.set(p.getValue().getType().getTitle());
        return stringProperty;
    }
}
