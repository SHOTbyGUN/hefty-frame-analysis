/*
 * Software made by SHOT(by)GUN <https://twitter.com/SHOTbyGUN>
 */

package hefty;

import java.awt.Desktop;
import java.net.URI;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import lib.Logger;
import lib.Statics;

/**
 * FXML Controller class
 *
 * @author SHOT(by)GUN
 */
public class MainGUIController implements Initializable {
    @FXML
    private BorderPane rootBorderPane;
    @FXML
    private TabPane rootTabPane;
    @FXML
    private Text dragDropText;
    @FXML
    private Label versionLabel;
    @FXML
    private TextArea logTextArea;
    @FXML
    private VBox progressVBox;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        versionLabel.setText(Statics.version);
    }    

    @FXML
    private void clearLogAction(ActionEvent event) {
        logTextArea.setText("");
    }
    
    @FXML
    private void twitterLinkClicked(MouseEvent event) {
        try {
            
            final String url = "https://twitter.com/SHOTbyGUN";
            Desktop.getDesktop().browse(new URI(url));
            
        } catch (Exception ex) {
            Logger.log(MainGUIController.class.getSimpleName(), "twitterLinkClicked error", ex);
        }
    }
    
    public void setLogText(final String text) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                logTextArea.appendText(text);
            }
        });
    }
    
    public TabPane getRootTabPane() {
        return rootTabPane;
    }
    
    public VBox getprogressVBox() {
        return progressVBox;
    }
    
    public TextArea getLogTextArea() {
        return logTextArea;
    }
    
}
