/*
 * Software made by SHOT(by)GUN <https://twitter.com/SHOTbyGUN>
 */

package Data;

import java.util.HashMap;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/**
 *
 * @author SHOT(by)GUN
 */
public class BarGraph {
    
    public static final int OPTIMAL_BAR_WIDTH = 20;
    
    private final Project project;
    private final VBox vBox = new VBox();
    
    // private global local variables lol np
    private int i;
    private HashMap<String, String> frameData;
    private Pane bottomLayer = new Pane();
    private Tab root;
    
    public BarGraph(final Project project, final Tab tab) {
        
        this.project = project;
        
        // Build Canvas
        bottomLayer.setMaxWidth(Double.MAX_VALUE);
        bottomLayer.setMaxHeight(Double.MAX_VALUE);
        
        // Build other components
        
        Button refreshButton = new Button("Refresh");
        refreshButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent t) {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        draw();
                    }
                });
            }
        });
        
        // Put it all together
        tab.setContent(vBox);
        vBox.getChildren().add(bottomLayer);
        vBox.getChildren().add(refreshButton);
        VBox.setVgrow(bottomLayer, Priority.ALWAYS);
    }
    
    public void addRow(XYChart.Series realSeries) {
        System.out.println("row data: " + Integer.toString(i) + ", " + frameData.get("pkt_size"));
    }
    
    public void draw() {
        
        
        // Clear the board!
        bottomLayer.getChildren().clear();
        
        // FINALLY I FOUND OUT HOW TO getWidth and getHeight of an "computed sized" node!!!
        // Damn that was hard to findout!
        Rectangle rect = new Rectangle(bottomLayer.getBoundsInLocal().getWidth(), bottomLayer.getBoundsInLocal().getHeight(), Color.GREY);
        Rectangle rect2 = new Rectangle(100, 100, Color.GREEN);
        
        bottomLayer.getChildren().add(rect);
        bottomLayer.getChildren().add(rect2);
        
        // Dont draw bars on slot 0 and max (because those are the borders
        // for(i = 1; i < howMany + 1; i++)
        
    }
    
}
