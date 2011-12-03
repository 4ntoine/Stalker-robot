/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
package name.antonsmirnov.javafx.stalker.log.factory;

import java.text.MessageFormat;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Callback;
import name.antonsmirnov.javafx.stalker.log.LogRecord;

/**
 *
 * @author sas
 */
public class ColorTextCellFactory implements Callback<TableColumn<LogRecord, LogRecord>, TableCell<LogRecord, LogRecord>> {

    /**
     * TextCell
     */
    protected class LabelCell extends TableCell<LogRecord, LogRecord> {

        private TableColumn<LogRecord, LogRecord> column;
        
        public LabelCell(TableColumn<LogRecord, LogRecord> column) {
            this.column = column;
        }
        
        public void updateItem(LogRecord value, boolean empty) {
            if (empty) {
                setGraphic(new Label(null));
                return;
            }
            
            Label label = new Label(empty ? null : value.getMessage());
            label.setStyle(MessageFormat.format("-fx-text-fill: {0};", value.getType().getTextColor()));
            setGraphic(label);
        }
    }
    
    public TableCell<LogRecord, LogRecord> call(TableColumn<LogRecord, LogRecord> column) {
        return new LabelCell(column);
    }
    
}
