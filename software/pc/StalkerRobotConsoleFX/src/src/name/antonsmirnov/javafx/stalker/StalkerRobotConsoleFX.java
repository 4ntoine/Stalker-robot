/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
package name.antonsmirnov.javafx.stalker;

import java.text.MessageFormat;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import name.antonsmirnov.firmata.serial.ISerial;
import name.antonsmirnov.firmata.serial.IndepProcessingSeriaAdapter;
import name.antonsmirnov.javafx.dialog.Dialog;
import name.antonsmirnov.javafx.stalker.log.LogRecord;
import name.antonsmirnov.javafx.stalker.log.LogRecordType;
import name.antonsmirnov.javafx.stalker.log.factory.ColorTextCellFactory;
import name.antonsmirnov.javafx.stalker.log.factory.LogDateTimeCellValueFactory;
import name.antonsmirnov.javafx.stalker.log.factory.LogRecordCellValueFactory;
import name.antonsmirnov.javafx.stalker.log.factory.LogRecordTypeCellValueFactory;
import processing.serial.IndepProcessingSerial;

/**
 *
 * @author sas
 */
public class StalkerRobotConsoleFX
    extends Application
    implements IndepProcessingSerial.Listener {
    
    public static final int NEW_LINE = 10;

    private IndepProcessingSerial indepSerial;
    private ISerial serial;
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Application.launch(args);
    }

    private ObservableList<LogRecord> log = FXCollections.observableArrayList();
    
    private void addLog(LogRecord entry) {
        log.add(entry);
        if (autoScrollCheckBox.isSelected() && log.size() > 1)
            tableView.scrollTo(log.size() - 1); // scroll tableview to last log record
    }
    
    protected void onDataReceived(String message) {
        addLog(new LogRecord(message, LogRecordType.FROM_DEVICE));
    }
            
    public void onDataReceived(IndepProcessingSerial is) {
        String message = is.readString();
        onDataReceived(message);
    }
    
    private void connect() {
        final String port = (String) portCombo.getSelectionModel().getSelectedItem();
        final String baudRate = (String) baudRateCombo.getSelectionModel().getSelectedItem();
        try {
            indepSerial = new IndepProcessingSerial(port, Integer.parseInt(baudRate));
            indepSerial.setListener(this);
            serial = new IndepProcessingSeriaAdapter(indepSerial);
            serial.start();
                    
            String logMessage = MessageFormat.format("Connected to {0} at {1}", port, baudRate);
            addLog(new LogRecord(logMessage, LogRecordType.INFO));
        } catch (Throwable t) {
            String logMessage = MessageFormat.format("Failed to connect to {0} at {1}", port, baudRate);
            addLog(new LogRecord(logMessage, LogRecordType.INFO));
            Dialog.showThrowable("Connection error", logMessage, t);
            return;
        }
        
        onConnected(true);
    }
    
    private void disconnect() {
        serial.stop();
        addLog(new LogRecord("Disconnected", LogRecordType.INFO));
        
        onConnected(false);
        connectButton.requestFocus();
    }
    
    private void sendMessage() {
        String message = messageTextEdit.getText();
        try {
            serial.write(message);
            addLog(new LogRecord(message, LogRecordType.TO_DEVICE));
        } catch (Throwable t) {
            String logMessage = "Failed to write message to the port";
            addLog(new LogRecord(logMessage, LogRecordType.INFO));
            Dialog.showThrowable("Port write error", logMessage, t);
        }
    }
    
    protected void alignToolbarWidth() {
        vbox.setPrefWidth(scene.getWidth());
        borderPane.setPrefWidth(scene.getWidth());
        toolbar.setPrefWidth(scene.getWidth());        
        messagePanel.setPrefWidth(scene.getWidth());
        tableView.setPrefWidth(scene.getWidth());
        statusPanel.setPrefWidth(scene.getWidth());
    }
    
    protected void alignMessageColumnWidth() {
        messageColumn.setPrefWidth(
                tableView.getWidth()
                - dateColumn.getWidth()
                - typeColumn.getWidth()
                - 20);  // scrollbar width
    }
    
    protected void alignTableHeight() {
        borderPane.setPrefHeight(scene.getHeight());
        tableView.setPrefHeight(
            scene.getHeight()
            - toolbar.getHeight()
            - statusPanel.getHeight()     
            - (messagePanelVisible ? messagePanel.getHeight() : 0));
    }
    
    private void alignSendPanel() {
        messageLabel.autosize();
        sendButton.autosize();
        messageTextEdit.setPrefWidth(messagePanel.getWidth() - messageLabel.getWidth() - sendButton.getWidth() - 4 * MARGIN);
    }
    
    private static final double MARGIN = 6;

    private BorderPane borderPane;
    
    private VBox vbox;
    private ToolBar toolbar;
    private Scene scene;
    private ChoiceBox portCombo;
    private ChoiceBox baudRateCombo;
    private Button connectButton;    
    private Button disconnectButton;
    
    private TableColumn dateColumn;
    private TableColumn typeColumn;
    private TableColumn messageColumn;
    private TableView tableView;
    
    private HBox messagePanel;
    private Label messageLabel;
    private TextField messageTextEdit;
    private Button sendButton;
    
    private HBox statusPanel;
    private CheckBox autoScrollCheckBox;
    private CheckBox cbWaitForCRLF;

    private boolean validateSettings() {
        if (portCombo.getSelectionModel().getSelectedItem() == null) {
            portCombo.requestFocus();
            portCombo.show();
            return false;
        }

        if (baudRateCombo.getSelectionModel().getSelectedItem() == null) {
            baudRateCombo.requestFocus();
            baudRateCombo.show();
            return false;
        }

        return true;
    }
    
    protected boolean messagePanelVisible = false;
    
    protected void onConnected(boolean isConnected) {
        portCombo.setDisable(isConnected);
        baudRateCombo.setDisable(isConnected);
        connectButton.setDisable(isConnected);
        disconnectButton.setDisable(!isConnected);
        
        if (!isConnected) {
            messagePanelVisible = false;
            vbox.getChildren().remove(messagePanel);
        } else {
            messagePanelVisible = true;
            vbox.getChildren().add(0, messagePanel);
            alignSendPanel();
        }
        
        alignTableHeight();
        messageTextEdit.requestFocus();
    }

    private void trySendMessage() {
        if (!validMessage()) {
            return;
        }

        sendMessage();
        messageTextEdit.clear();
    }

    private boolean validMessage() {
        if (messageTextEdit.getText().isEmpty()) {
            Dialog.showWarning("Message", "Type message first");
            messageTextEdit.requestFocus();
            return false;
        }

        return true;
    }
    
    private ObservableList<String> ports = FXCollections.observableArrayList();
    
    private void setWaitForCRLF() {
        // do nto chanhe the order
        indepSerial.bufferUntil(NEW_LINE);
        indepSerial.setBufferUntil(cbWaitForCRLF.isSelected());
    }
            
    @Override
    public void start(final Stage primaryStage) {
        primaryStage.setTitle("StalkerRobotConsoleFX");
        Group root = new Group();
        scene = new Scene(root, 800, 600);
        vbox = new VBox();
        vbox.setFillWidth(true);
        
        borderPane = new BorderPane();
        
        toolbar = new ToolBar();        
        
        scene.widthProperty().addListener(new ChangeListener<Number>() {

            public void changed(ObservableValue<? extends Number> ov, Number oldValue, Number newValue) {
                alignToolbarWidth();
            }                        
            
        });
        
        // port
        portCombo = new ChoiceBox(ports);
        portCombo.showingProperty().addListener(new ChangeListener<Boolean>() {

            //show ports on drop-down event
            public void changed(ObservableValue<? extends Boolean> ov, Boolean t, Boolean t1) {
                if (portCombo.isShowing()) {
                    ports.clear();
                    ports.addAll(IndepProcessingSerial.list());
                }
            }
        });
        
        toolbar.getItems().add(portCombo);
        
        // baud rate
        baudRateCombo = new ChoiceBox(
            FXCollections.observableArrayList(
                "300",
                "1200",
                "2400",
                "4800",
                "9600",
                "14400",
                "19200",
                "28800",
                "38400",
                "57600",
                "115200"
                ));
        baudRateCombo.getSelectionModel().select(4); // 9600 as default
        toolbar.getItems().add(baudRateCombo);        
        
        // "connect" button
        connectButton = new Button("Connect");
        connectButton.setGraphic(
            new ImageView(
                new Image(getClass().getResourceAsStream("/connect.png"))));
        toolbar.getItems().add(connectButton);
        
        connectButton.setOnAction(new EventHandler<ActionEvent>() {

            public void handle(ActionEvent t) {
                if (!validateSettings()) {
                    Dialog.showWarning("Connection settings", "Choose port and baud rate first");
                    return;
                }
                
                connect();
                setWaitForCRLF();
            }           
        });
        
        // "disconnect button"
        disconnectButton = new Button("Disconnect");
        disconnectButton.setGraphic(
            new ImageView(
                new Image(getClass().getResourceAsStream("/disconnect.png"))));
        
        disconnectButton.setOnAction(new EventHandler<ActionEvent>() {

            public void handle(ActionEvent t) {
                disconnect();
            }
        });
        toolbar.getItems().add(disconnectButton);
        
        toolbar.heightProperty().addListener(new ChangeListener<Number>() {

            public void changed(ObservableValue<? extends Number> ov, Number oldvalue, Number newValue) {
                // to set toolbar choiceboxes height = toolbar height
                double newCorrectedValue = newValue.doubleValue() - 10;
                portCombo.setPrefHeight(newCorrectedValue);
                baudRateCombo.setPrefHeight(newCorrectedValue);
            }
            
        });
        borderPane.setTop(toolbar);
        
        messagePanel = new HBox();
        
        // message label
        messageLabel = new Label("Text: ");
        messageLabel.setAlignment(Pos.CENTER);
        HBox.setMargin(messageLabel, new Insets(MARGIN));
        messagePanel.getChildren().add(messageLabel);        
        
        // message text field
        messageTextEdit = new TextField();
        messageTextEdit.autosize();
        HBox.setMargin(messageTextEdit, new Insets(MARGIN, 0, MARGIN, 0));
        messageTextEdit.setOnKeyReleased(new EventHandler<KeyEvent>() {

            public void handle(KeyEvent t) {
                if (t.getCode().equals(KeyCode.ENTER))
                    trySendMessage();
            }
        
        });
        messagePanel.getChildren().add(messageTextEdit);
        
        // send button
        sendButton = new Button("Send");
        sendButton.autosize();
        sendButton.setAlignment(Pos.CENTER_RIGHT);
        HBox.setMargin(sendButton, new Insets(MARGIN));
        messagePanel.getChildren().add(sendButton);        
        messagePanel.widthProperty().addListener(new ChangeListener<Number>() {

            public void changed(ObservableValue<? extends Number> ov, Number t, Number t1) {
                alignSendPanel();
            }
            
        });        
        
        sendButton.setOnAction(new EventHandler<ActionEvent>() {

            public void handle(ActionEvent t) {
                trySendMessage();
            }
        });

        // log list (table)
        dateColumn = new TableColumn("DateTime");
        dateColumn.setCellValueFactory(new LogDateTimeCellValueFactory("HH:mm:ss"));
        dateColumn.setSortable(false);
        dateColumn.setPrefWidth(80);
        
        typeColumn = new TableColumn("Type");
        typeColumn.setCellValueFactory(new LogRecordTypeCellValueFactory());
        typeColumn.setSortable(false);
        typeColumn.setPrefWidth(50);
        
        messageColumn = new TableColumn("Data");
        messageColumn.setPrefWidth(Integer.MAX_VALUE); // asmuch as possible        
        messageColumn.setCellValueFactory(new LogRecordCellValueFactory());
        messageColumn.setSortable(false);
        messageColumn.setCellFactory(new ColorTextCellFactory());

        tableView = new TableView();
        
        LogTableContextMenuFactory menuFactory = new LogTableContextMenuFactory(tableView);
        ContextMenu menu = menuFactory.build();
        tableView.setContextMenu(menu);
        
        tableView.setPlaceholder(new Label());
        tableView.getColumns().addAll(dateColumn, typeColumn, messageColumn);
        tableView.widthProperty().addListener(new ChangeListener<Number>() {

            public void changed(ObservableValue<? extends Number> ov, Number t, Number t1) {
                alignMessageColumnWidth();
            }
        });
        
        scene.heightProperty().addListener(new ChangeListener<Number>() {

            public void changed(ObservableValue<? extends Number> ov, Number t, Number t1) {
                alignTableHeight();
            }
        });
        
        tableView.setItems(log);
        vbox.getChildren().add(tableView);
        
        // status panel
        statusPanel = new HBox();
        
        // autoscroll
        autoScrollCheckBox = new CheckBox("autoscroll");
        autoScrollCheckBox.setSelected(true);
        autoScrollCheckBox.setTooltip(new Tooltip("Autoscroll log table to the last record"));
        HBox.setMargin(autoScrollCheckBox, new Insets(MARGIN));
        statusPanel.getChildren().add(autoScrollCheckBox);
        
        // wait for CRLF
        cbWaitForCRLF = new CheckBox("wait for CRLF");
        cbWaitForCRLF.setTooltip(new Tooltip("Buffer data from device until CRLF is received"));
        cbWaitForCRLF.setSelected(true);
        HBox.setMargin(cbWaitForCRLF, new Insets(MARGIN));
        cbWaitForCRLF.selectedProperty().addListener(new ChangeListener<Boolean>() {

            public void changed(ObservableValue ov, Boolean oldValue, Boolean newValue) {
                if (!newValue && serial.available() > 0) {
                    String message = serial.readString();
                    if (message.length() > 1  || message.charAt(0) == NEW_LINE) {
                        onDataReceived(message);
                    }
                }
                setWaitForCRLF();
            }            
        });
        
        statusPanel.getChildren().add(cbWaitForCRLF);
        
        borderPane.setCenter(vbox);
        borderPane.setBottom(statusPanel);
        
        root.getChildren().add(borderPane);        
        primaryStage.setScene(scene);
        primaryStage.centerOnScreen();
        
        onConnected(false);
        alignToolbarWidth();
        alignMessageColumnWidth();
        alignTableHeight();
        
        primaryStage.show();        
    }
}
