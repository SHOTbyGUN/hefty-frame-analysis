package hefty.frame.analysis;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
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
        
        // Start the application by initializing The main class of the application
        final GodObject application = new GodObject();
        
        
        Parent root = FXMLLoader.load(getClass().getResource("MainGUI.fxml"));
        
        Scene scene = new Scene(root);
        
        stage.setScene(scene);
        stage.show();
        
        scene.getWindow().setOnCloseRequest(new EventHandler<WindowEvent>() {
        @Override
        public void handle(WindowEvent ev) {
            if (!application.shutdown()) {
                ev.consume();
            }
        }
    });
        
        
        
        Logger.log(Statics.applicationName, "Application started");
        
        application.test();
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
