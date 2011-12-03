/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
package name.antonsmirnov.javafx.dialogs;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageBuilder;

/**
 *
 * @author 4ntoine
 */
public class Dialog extends Stage {

    protected Scene scene;
    protected BorderPane borderPanel;
    protected Label messageLabel;
    protected ImageView icon;
    protected HBox buttonsPanel;
    
    /**
     * Builder
     */
    public static class Builder {

        protected Dialog stage;
        
        private static final double MARGIN = 10;
        
        public Builder create() {
            stage = new Dialog();
            stage.setResizable(false);
            stage.initModality(Modality.APPLICATION_MODAL);                        
            stage.setIconified(false);
            stage.borderPanel = new BorderPane();            

            // icon
            stage.icon = new ImageView();
            stage.borderPanel.setLeft(stage.icon);
            BorderPane.setMargin(stage.icon, new Insets(MARGIN));
            
            // message
            stage.messageLabel = new Label();
            stage.borderPanel.setCenter(stage.messageLabel);
            BorderPane.setMargin(stage.messageLabel, new Insets(MARGIN, MARGIN, MARGIN, 0));
            
            // buttons
            stage.buttonsPanel = new HBox();
            stage.buttonsPanel.setSpacing(MARGIN);
            stage.buttonsPanel.setAlignment(Pos.BOTTOM_CENTER);
            BorderPane.setMargin(stage.buttonsPanel, new Insets(0, MARGIN, MARGIN, 0));
            stage.borderPanel.setBottom(stage.buttonsPanel);
            stage.borderPanel.widthProperty().addListener(new ChangeListener<Number> () {

                public void changed(ObservableValue<? extends Number> ov, Number t, Number t1) {
                    stage.buttonsPanel.layout();
                }
                
            });
            
            stage.scene = new Scene(stage.borderPanel);
            stage.setScene(stage.scene);
            return this;
        }
        
        public Builder setTitle(String title) {
            stage.setTitle(title);
            return this;
        }
        
        public Builder setMessage(String message) {
            stage.messageLabel.setText(message);
            return this;
        }
        
        protected void setIconFromResource(String resourceName) {
            final Image image = new Image(getClass().getResourceAsStream(resourceName));
            stage.icon.setImage(image);
        }
        
        public Builder setWarningIcon() {
            setIconFromResource("/warningIcon.png");
            return this;
        }
        
        public Builder setErrorIcon() {
            setIconFromResource("/errorIcon.png");
            return this;
        }
        
        public Builder setInfoIcon() {
            setIconFromResource("/infoIcon.png");
            return this;
        }
        
        public Builder addOkButton() {
            Button okButton = new Button("OK");
            okButton.setUserData(stage);
            okButton.setOnAction(new EventHandler<ActionEvent> () {

                public void handle(ActionEvent t) {
                    Button button = (Button) t.getSource();
                    Dialog dialog = (Dialog) (button.getUserData());
                    dialog.close();
                }
                
            });
            stage.buttonsPanel.getChildren().add(okButton);
            return this;
        }
        
        public Dialog build() {
            return stage;
        }
    }
    
    /**
     * Show information dialog box
     * @param title dialog title
     * @param message dialog message
     */
    public static void info(String title, String message) {
        new Builder()
            .create()
            .setTitle(title)
            .setMessage(message)
            .setInfoIcon()
            .addOkButton()
                .build()
                    .show();
    }

    /**
     * Show warning dialog box
     * @param title dialog title
     * @param message dialog message
     */
    public static void warning(String title, String message) {
        new Builder()
            .create()
            .setTitle(title)
            .setMessage(message)
            .setWarningIcon()
            .addOkButton()
                .build()
                    .show();
    }

    /**
     * Show error dialog box
     * @param title dialog title
     * @param message dialog message 
     */
    public static void error(String title, String message) {
        error(title, message, null);
    }
    
    /**
     * Show error dialog box
     * @param title dialog title
     * @param message dialog message 
     * @param t throwable
     */
    public static void error(String title, String message, Throwable t) {
        new Builder()
            .create()
            .setTitle(title)
            .setMessage(message)
            .setErrorIcon()
            .addOkButton()
                .build()
                    .show();
    }
}
