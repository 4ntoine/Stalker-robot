/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
package name.antonsmirnov.javafx.stalker.log.factory;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.util.Callback;
import name.antonsmirnov.javafx.stalker.log.LogRecord;

/**
 *
 * @author adept
 */
public class LogRecordCellValueFactory
    implements Callback<CellDataFeatures<LogRecord, LogRecord>, ObservableValue<LogRecord>> {

    public ObservableValue<LogRecord> call(CellDataFeatures<LogRecord, LogRecord> features) {
        SimpleObjectProperty<LogRecord> property = new SimpleObjectProperty<LogRecord>(features.getValue());
        return property;
    }
}
