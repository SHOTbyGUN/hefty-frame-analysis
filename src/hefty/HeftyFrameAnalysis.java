package hefty;

import java.io.File;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import lib.Logger;
import lib.Statics;


/*

Needed data
- ffprobe executable location


*/

/**
 *
 * @author shotbygun
 */
public class HeftyFrameAnalysis extends Application {
    
    @Override
    public void start(Stage stage) throws Exception {
        
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("MainGUI.fxml"));
        fxmlLoader.load();
        Statics.mainGuiController = fxmlLoader.getController();
        
        Parent root = fxmlLoader.getRoot();
        Scene scene = new Scene(root);
        
        stage.setTitle(Statics.applicationName + " " + Statics.version);
        
        stage.setScene(scene);
        stage.show();
        
        scene.getWindow().setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent ev) {
                if (!Statics.application.shutdown()) {
                    ev.consume();
                }
            }
        });
        
        
        // Add Drag and Drop functionality
        // Source: http://www.java2s.com/Code/Java/JavaFX/DraganddropfiletoScene.htm
        
        scene.setOnDragOver(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                Dragboard db = event.getDragboard();
                if (db.hasFiles()) {
                    event.acceptTransferModes(TransferMode.COPY);
                } else {
                    event.consume();
                }
            }
        });
        
        scene.setOnDragDropped(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                Dragboard db = event.getDragboard();
                boolean success = false;
                if (db.hasFiles()) {
                    success = true;
                    String filePath = null;
                    for (File file:db.getFiles()) {
                        Statics.application.createNewProject(file);
                    }
                }
                event.setDropCompleted(success);
                event.consume();
            }
        });
        
        // Start the application by initializing The main class of the application
        Statics.application = new HeftyApplication();

        Logger.log(Statics.applicationName, "Application started");
        
        
    }

    /**
     * The main() method is ignored in correctly deployed JavaFX application.
     * main() serves only as fallback in case the application can not be
     * launched through deployment artifacts, e.g., in IDEs with limited FX
     * support. NetBeans ignores main().
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}
