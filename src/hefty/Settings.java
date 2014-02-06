/*
 * Software made by SHOT(by)GUN <https://twitter.com/SHOTbyGUN>
 */

package hefty;

import java.awt.Desktop;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.Properties;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import static lib.FileOperations.getFile;
import static lib.FileOperations.getOrCreateFile;
import static lib.FileOperations.getWorkingDirectory;
import lib.Logger;
import lib.Statics;

/**
 *
 * @author SHOT(by)GUN
 */
public class Settings {
    
    // All available property items
    public static final String ffprobePath = "ffprobePath";
    public static final String lowGraphicsMode = "lowGraphicsMode";
    
    
    private final Properties defaultSettings = new Properties();
    private final Properties currentSettings;
    
    // GUI components
    TextField ffprobePathTextField;
    CheckBox lowGraphicsModeCheckBox;
    FileChooser fileChooser;
    
    public Settings() {
        
        // Set default values
        defaultSettings.setProperty(ffprobePath, "");
        defaultSettings.setProperty(lowGraphicsMode, "false");
        
        // Create current settings
        currentSettings = new Properties(defaultSettings);
        
        // load settings from file
        load();
        
        // Generate GUI components
        GridPane grid = Statics.mainGuiController.getSettingsGridPane();
        grid.alignmentProperty().setValue(Pos.TOP_LEFT);
        grid.getChildren().clear();
        int gridID = 0;
        
        // ffprobePath
        Label ffprobeLabel = new Label("ffprobe executable path");
        ffprobePathTextField = new TextField(currentSettings.getProperty(ffprobePath));
        fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("ffprobe executable", "ffprobe.bin", "ffprobe.exe", "ffprobe"));
        Button browseButton = new Button("Browse");
        browseButton.setOnMouseClicked(browseButtonEvent);
        Hyperlink ffprobeDownloadLink = new Hyperlink("Download ffprobe here");
        ffprobeDownloadLink.setOnMouseClicked(ffProbeHyperLinkClickedEvent);
        grid.addRow(gridID++, ffprobeLabel, ffprobePathTextField, browseButton, ffprobeDownloadLink);
        
        // Bar graphics
        Label lowGraphicsModeLabel = new Label("Quality of bars");
        lowGraphicsModeCheckBox = new CheckBox("Low graphics mode");
        lowGraphicsModeCheckBox.setSelected(Boolean.parseBoolean(currentSettings.getProperty(lowGraphicsMode)));
        grid.addRow(gridID++, lowGraphicsModeLabel, lowGraphicsModeCheckBox);
        
        // Save button
        //Label saveLabel = new Label("Settings are not applied until you click Apply and Save");
        Button saveButton = new Button("Apply and Save");
        saveButton.setOnMouseClicked(saveMouseEvent);
        grid.addRow(gridID++, saveButton);
        
        // If we dont have ffprobe path, show it on welcome screen
        if(ffprobePathTextField.getText().length() < 1 ) {
            Statics.mainGuiController.getDragAndDropText().setText("Please set ffprobe executable path on settings tab");
        }
    }
    
    public Properties getSettings() {
        return currentSettings;
    }
    
    public void save() {
        
        // load values from GUI and put em into currentProperties
        currentSettings.setProperty(ffprobePath, ffprobePathTextField.getText());
        currentSettings.setProperty(lowGraphicsMode, Boolean.toString(lowGraphicsModeCheckBox.isSelected()));
        
        // if ffprobe path is atleast SOMETHING change welcome message
        if(ffprobePathTextField.getText().length() > 1 &&  Statics.mainGuiController.getDragAndDropText().getText() != Statics.defaultDragAndDropText) {
            Statics.mainGuiController.getDragAndDropText().setText(Statics.defaultDragAndDropText);
        }
        
        try {
            File outFile = getOrCreateFile(getWorkingDirectory() + File.separator + "settings.properties");
            OutputStream out = new FileOutputStream(outFile);
            currentSettings.store(out, "saving properties");
            Logger.log(Settings.class.getSimpleName(), "saved");
        } catch (Exception ex) {
            Logger.log(Settings.class.getSimpleName(), "error saving properties", ex);
        }
    }
    
    private void load() {
        try {
            File inFile = getFile(getWorkingDirectory() + File.separator + "settings.properties");
            InputStream in = new FileInputStream(inFile);
            currentSettings.load(in);
            Logger.log(Settings.class.getSimpleName(), "loaded");
        } catch (FileNotFoundException ex) {
            Logger.log(Settings.class.getSimpleName(), "file not found, maybe first time running", ex);
        } catch (Exception ex) {
            Logger.log(Settings.class.getSimpleName(), "error loading properties", ex);
        }
    }
    
    private EventHandler<MouseEvent> ffProbeHyperLinkClickedEvent = new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent t) {
            try {
                final String url = "http://www.ffmpeg.org/download.html";
                Desktop.getDesktop().browse(new URI(url));

            } catch (Exception ex) {
                Logger.log(MainGUIController.class.getSimpleName(), "ffProbeHyperLinkClickedEvent error", ex);
            }
        }
    };
    
    private EventHandler<MouseEvent> saveMouseEvent = new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent t) {
            save();
        }
    };
    
    private EventHandler<MouseEvent> browseButtonEvent = new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent t) {
            File file = fileChooser.showOpenDialog(Statics.stage);
            if(file != null)
                ffprobePathTextField.setText(file.getAbsolutePath());
        }
    };
    
}
