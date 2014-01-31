/*
 * Software made by SHOT(by)GUN <https://twitter.com/SHOTbyGUN>
 */

package Data;

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Slider;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import lib.Logger;

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
    private final Slider fineTune = new Slider(-5, 5, 0);
    private boolean fineTuneIsActive = false;
    private int lastHowManyBars = 0;
    
    // private global local variables lol np
    private int i;
    private HashMap<String, String> frameData;
    
    // Layout
    private final StackPane stackPane = new StackPane();
    private final Pane bottomLayer = new Pane();
    private final Pane topLayer = new Pane();
    
    // Data change checks
    private boolean firstDrawDone = false;
    private final AtomicBoolean drawNeeded = new AtomicBoolean(false);
    private double lastWidth = 0;
    private double lastHeight = 0;
    
    
    
    public BarGraph(final Project project) {
        
        this.project = project;
        
        // Slider functionality
        slider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> ov, Number t, Number t1) {
                if(t1.intValue() < 0) {
                    slider.setValue(0);
                }
                drawNeeded();
            }
        });
        
        fineTune.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent t) {
                fineTuneIsActive = true;
            }
        });
        
        fineTune.setOnMouseReleased(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent t) {
                fineTuneIsActive = false;
                fineTune.setValue(0);
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
                        drawNeeded();
                    }
                });
            }
        });
        hBox.getChildren().add(refreshButton);
        
        CheckBox ignoreAudio = new CheckBox("ignore audio frames");
        ignoreAudio.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {
                drawNeeded();
            }
        });
        hBox.getChildren().add(ignoreAudio);
        
        
        // Put it all together
        stackPane.getChildren().add(bottomLayer);
        stackPane.getChildren().add(topLayer);
        vBox.getChildren().add(stackPane);
        vBox.getChildren().add(fineTune);
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
        
        // check if we need to draw bottom layer
        if(lastWidth != getWidth() || lastHeight != getHeight()) {
            drawBottomLayer();
            lastWidth = getWidth();
            lastHeight = getHeight();
            drawNeeded();
        }
        
        if(!firstDrawDone)
            return;
        
        // check if we need to redraw the bars
        if(drawNeeded.compareAndSet(true, false)) {
            drawTopLayer();
        }
        
    }
    
    private void drawBottomLayer() {
        
        try {
        
            Line bottomLine, topLine;

            // Clear the board!
            bottomLayer.getChildren().clear();

            // Draw top/bottom lines which hold the bars in between
            topLine = new Line(0, MARGIN, getWidth() + MARGIN, MARGIN);
            bottomLine = new Line(0, getHeight(), getWidth() + MARGIN, getHeight()); 

            bottomLayer.getChildren().addAll(bottomLine, topLine);

        } catch (Exception ex) {
            Logger.log(BarGraph.class.getSimpleName(), "Draw Bottom Layer", ex);
        }
        
    }
    
    private void drawTopLayer() {
        
        try {

            // clear the board!
            topLayer.getChildren().clear();

            // calculate how many bars we can fit into the window
            int howManyBars = (int) (getWidth() / OPTIMAL_BAR_SPACE);
            lastHowManyBars = howManyBars;

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

            for(i = 0; i < howManyBars; i++) {

                // get Frame
                frame = project.frames.get(framesFrom + i);

                // get packet size for this frame
                packetSize = frame.getPacketSize();

                // x location of a bar
                x = MARGIN / 2 + (barSpace * i);

                // Draw a bar, bottom to up
                line = new Line(x, yStart, x, getHeight() - (packetSize / maxPacketSize) * (getHeight() - MARGIN));

                // Style the line
                line.setStroke(frame.getFrameColor());
                line.setStrokeWidth(BAR_WIDTH);

                // add line to the Layer
                topLayer.getChildren().add(line);
            }

            // Draw start and end frame numbers
            Text textFramesFrom = new Text(0, MARGIN / 2, "frame " + framesFrom);
            Text textFramesTo = new Text("frame " + (framesFrom + howManyBars - 1));
            textFramesTo.setX(getWidth() - textFramesTo.getBoundsInLocal().getWidth());
            textFramesTo.setY(MARGIN / 2);

            topLayer.getChildren().addAll(textFramesFrom, textFramesTo);

        
        } catch (Exception ex) {
            Logger.log(BarGraph.class.getSimpleName(), "draw top layer", ex);
        }
    }
    
    private int getMaxPacketSize(int framesFrom, int howManyFrames) {
        int maxPacketSize = 0;
        int tester;
        for(i = 0; i < howManyFrames; i++) {
            // get packet size for a frame
            tester = project.frames.get(framesFrom + i).getPacketSize();
            
            // Did we get bigger size?
            if(tester > maxPacketSize)
                maxPacketSize = tester;
            
        }
        return maxPacketSize;
    } 
    
    private int getFramesFrom() {
        // Sliders max value should keep us safe from array index out of bounds exception
        return (int) slider.getValue();
    }
    
    public void updateSlider() {
        
        if(project.totalFrames - lastHowManyBars > 0) {
            slider.setMax(project.totalFrames - lastHowManyBars);
        }
        
        if(!firstDrawDone) {
            if(project.totalFrames > 500) {
                drawNeeded();
                firstDrawDone = true;
            }
        } else {
            if(fineTuneIsActive)
                slider.setValue(slider.getValue() + fineTune.getValue());
        }
    }
    
    public void drawNeeded() {
        drawNeeded.compareAndSet(false, true);
    }
    
}
