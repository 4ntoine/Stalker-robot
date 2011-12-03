/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
package name.antonsmirnov.javafx.stalker;

import java.util.HashMap;
import java.util.Map;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.DataFormat;
import name.antonsmirnov.javafx.stalker.log.LogRecord;

/**
 *
 * @author adept
 */
public class LogTableContextMenuFactory {

    private TableView tableView;
    
    public LogTableContextMenuFactory(TableView tableView) {
        this.tableView = tableView;
    }
    
    public ContextMenu build() {
        ContextMenu menu = new ContextMenu();
        
        // copy message
        MenuItem copyItem = new MenuItem("Copy message");
        copyItem.setOnAction(new EventHandler<ActionEvent>() {

            public void handle(ActionEvent t) {
                LogRecord logRecord = (LogRecord)tableView.getSelectionModel().getSelectedItem();
                Map<DataFormat, Object> map = new HashMap<DataFormat, Object>();
                map.put(DataFormat.PLAIN_TEXT, logRecord.getMessage());
                Clipboard.getSystemClipboard().setContent(map);
            }
        });
        menu.getItems().add(copyItem);
        
        // clear table
        MenuItem clearItem = new MenuItem("Clear");
        clearItem.setOnAction(new EventHandler<ActionEvent>() {

            public void handle(ActionEvent t) {
                tableView.getItems().clear();
            }
        });
        menu.getItems().add(clearItem);
        
        return menu;
    }
    
}
