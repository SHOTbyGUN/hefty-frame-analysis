/*
 * Software made by SHOT(by)GUN <https://twitter.com/SHOTbyGUN>
 */

package Data;

import java.util.HashMap;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;

/**
 *
 * @author SHOT(by)GUN
 */
public class BarGraph {
    
    public static final int OPTIMAL_BAR_SPACE = 10;
    public static final int BAR_WIDTH = 5;
    public static final int MARGIN = 50;
    
    private final Project project;
    private final VBox vBox = new VBox();
    private final HBox hBox = new HBox();
    private final Slider slider = new Slider(0, 0, 0);
    
    // private global local variables lol np
    private int i;
    private HashMap<String, String> frameData;
    private boolean firstDrawDone = false;
    
    // Layout
    private final StackPane stackPane = new StackPane();
    private final Pane bottomLayer = new Pane();
    private final Pane topLayer = new Pane();
    
    public BarGraph(final Project project) {
        
        this.project = project;
        
        // Slider functionality
        slider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> ov, Number t, Number t1) {
                draw();
            }
        });
        
        
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
        hBox.getChildren().add(refreshButton);
        
        // Put it all together
        stackPane.getChildren().add(bottomLayer);
        stackPane.getChildren().add(topLayer);
        vBox.getChildren().add(stackPane);
        vBox.getChildren().add(slider);
        vBox.getChildren().add(hBox);
        VBox.setVgrow(stackPane, Priority.ALWAYS);
        project.getTab().setContent(vBox);
    }
    
    public void addRow(XYChart.Series realSeries) {
        System.out.println("row data: " + Integer.toString(i) + ", " + frameData.get("pkt_size"));
    }
    
    public Double getWidth() {
        return stackPane.getBoundsInLocal().getWidth() - MARGIN;
    }
    
    public Double getHeight() {
        return stackPane.getBoundsInLocal().getHeight() - MARGIN;
    }
    
    public void draw() {
        
        
        // TODO check if we need to draw bottom layer
        drawBottomLayer();
        
        // TODO check if we need to redraw the bars
        drawTopLayer();
        
    }
    
    private void drawBottomLayer() {
        
        Line bottomLine, topLine;
        
        // Clear the board!
        bottomLayer.getChildren().clear();
        
        // Draw top/bottom lines which hold the bars in between
        topLine = new Line(0, MARGIN, getWidth() + MARGIN, MARGIN);
        bottomLine = new Line(0, getHeight(), getWidth() + MARGIN, getHeight()); 
        
        bottomLayer.getChildren().addAll(bottomLine, topLine);
        
    }
    
    private void drawTopLayer() {
        
        // clear the board!
        topLayer.getChildren().clear();
        
        // calculate how many bars we can fit into the window
        int howManyBars = (int) (getWidth() / OPTIMAL_BAR_SPACE);
        
        // calculate bar delimiter
        int barSpace = (int) (getWidth() / howManyBars);
        
        // Define starting point for frames
        int framesFrom = getFramesFrom();
        
        // Bar height = packet size / max packet size
        int maxPacketSize = getMaxPacketSize(framesFrom, howManyBars);
        
        // Packet size is double so we don't lose accuracy
        double packetSize;
        
        Line line;
        
        Frame frame;
        
        double x;
        // All bars start from the 0 point
        double yStart = getHeight();
        
        for(i = 0; i < howManyBars && framesFrom + i < project.totalFrames; i++) {
            
            // get Frame
            frame = project.frames.get(framesFrom + i);
            
            // get packet size for this frame
            packetSize = frame.getPacketSize();
            
            // x location of a bar
            x = MARGIN + (barSpace * i);
            
            // Draw a bar, bottom to up
            line = new Line(x, yStart, x, getHeight() - (packetSize / maxPacketSize) * (getHeight() - MARGIN));
            
            // Style the line
            line.setStroke(frame.getFrameColor());
            line.setStrokeWidth(BAR_WIDTH);
            
            // add line to the Layer
            topLayer.getChildren().add(line);
        }
    }
    
    private int getMaxPacketSize(int framesFrom, int howManyFrames) {
        int maxPacketSize = 0;
        int tester;
        for(i = 0; i < howManyFrames && framesFrom + i < project.totalFrames; i++) {
            // get packet size for a frame
            tester = project.frames.get(framesFrom + i).getPacketSize();
            
            // Did we get bigger size?
            if(tester > maxPacketSize)
                maxPacketSize = tester;
            
        }
        return maxPacketSize;
    } 
    
    private int getFramesFrom() {
        return (int) slider.getValue();
    }
    
    private void testDraw() {
        
        // Kinda obsolete "window" size test method
        
        // FINALLY I FOUND OUT HOW TO getWidth and getHeight of an "computed sized" node!!!
        // Damn that was hard to findout!
        // stackPane.getBoundsInLocal().getHeight()
        Rectangle rect = new Rectangle(getWidth(), getHeight(), Color.GREY);
        Rectangle rect2 = new Rectangle(100, 100, Color.GREEN);
        
        bottomLayer.getChildren().add(rect);
        bottomLayer.getChildren().add(rect2);
    }
    
    public void updateSlider() {
        slider.setMax(project.totalFrames);
        if(!firstDrawDone) {
            if(project.totalFrames > 100) {
                draw();
                firstDrawDone = true;
            }
        }
    }
    
}
