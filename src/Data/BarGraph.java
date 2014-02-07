/*
 * Software made by SHOT(by)GUN <https://twitter.com/SHOTbyGUN>
 */

package Data;

import hefty.Settings;
import java.util.concurrent.atomic.AtomicBoolean;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.Tooltip;
import javafx.scene.effect.Light;
import javafx.scene.effect.Lighting;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.text.Text;
import lib.Logger;
import lib.Statics;

/**
 *
 * @author SHOT(by)GUN
 */
public class BarGraph {
    
    public static final int OPTIMAL_BAR_SPACE = 10;
    public static final int BAR_WIDTH = 8;
    public static final int MARGIN = 50;
    public static final int DETAILS_MAX_WIDTH = 250;
    
    private Project project;
    
    // UI Components
    private final Slider slider = new Slider(0, 0, 0);
    private final Slider fineTuneSlider = new Slider(-10, 10, 0);
    private boolean fineTuneIsActive = false;
    
    //private final TextArea fileInfoTextArea;
    //private final GridPane frameInfoPane;
    
    private final CheckBox ignoreAudio;
    
    
    private int lastHowManyBars = 0;
    
    private int i;
    
    // Layout
    private final HBox root = new HBox();
    //private final Accordion rightSideDetails = new Accordion();
    private final VBox leftSideVBox = new VBox();
    private final HBox controlsHBox = new HBox();
    private final StackPane stackPane = new StackPane();
    private final Pane bottomLayer = new Pane();
    private final Pane topLayer = new Pane();
    //private final TitledPane fileInfo, frameInfo;
    
    // Data change checks
    private boolean firstDrawDone = false;
    private final AtomicBoolean drawNeeded = new AtomicBoolean(false);
    private double lastWidth = 0;
    private double lastHeight = 0;
    
    public static final Light.Distant light = new Light.Distant();
    public static final Lighting lineLighting = new Lighting(light);
    static {
        light.setColor(Color.WHITE);
        light.setAzimuth(45);
        light.setElevation(130);
        lineLighting.setSurfaceScale(5);
    }
            
    
    
    
    
    public BarGraph(Project project) {
        
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
        
        fineTuneSlider.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent t) {
                fineTuneIsActive = true;
            }
        });
        
        fineTuneSlider.setOnMouseReleased(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent t) {
                fineTuneIsActive = false;
                fineTuneSlider.setValue(0);
            }
        });
        
        // File info, Frame info will be filled with listener
        /*
        fileInfoTextArea = new TextArea(project.videoInfoRaw);
        fileInfoTextArea.setMaxWidth(DETAILS_MAX_WIDTH);
        fileInfoTextArea.setWrapText(true);
        
        fileInfo = new TitledPane("File", fileInfoTextArea);
        
        frameInfo = new TitledPane();
        frameInfo.setText("Frame");
        frameInfoPane = new GridPane();
        frameInfoPane.setMaxWidth(DETAILS_MAX_WIDTH);
        
        frameInfo.setContent(frameInfoPane);
        
        rightSideDetails.getPanes().addAll(fileInfo, frameInfo);
        rightSideDetails.setExpandedPane(fileInfo);
        */
        
        
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
        controlsHBox.getChildren().add(refreshButton);
        
        ignoreAudio = new CheckBox("ignore audio frames");
        ignoreAudio.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {
                drawNeeded();
            }
        });
        controlsHBox.getChildren().add(ignoreAudio);
        
        
        // Put it all together
        stackPane.getChildren().add(bottomLayer);
        stackPane.getChildren().add(topLayer);
        leftSideVBox.getChildren().add(stackPane);
        leftSideVBox.getChildren().add(fineTuneSlider);
        leftSideVBox.getChildren().add(slider);
        leftSideVBox.getChildren().add(controlsHBox);
        VBox.setVgrow(stackPane, Priority.ALWAYS);
        
        root.getChildren().add(leftSideVBox);
        //root.getChildren().add(rightSideDetails);
        HBox.setHgrow(leftSideVBox, Priority.ALWAYS);
        project.getTab().setContent(root);
    }
    
    public Double getWidth() {
        //return stackPane.getBoundsInLocal().getWidth() - MARGIN;
        return stackPane.getLayoutBounds().getWidth() - MARGIN;
    }
    
    public Double getHeight() {
        //return stackPane.getBoundsInLocal().getHeight() - MARGIN;
        return stackPane.getLayoutBounds().getHeight() - MARGIN;
    }
    
    public void draw() {
        
        // check if we need to draw bottom layer
        //if(lastWidth != getWidth() || lastHeight != getHeight()) {
        if(Math.abs(lastWidth - getWidth()) > 1 || Math.abs(lastHeight - getHeight()) > 1) {
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
            
            HBox legendBox = new HBox();
            
            Line line;
            Label label = null;
            
            // Draw legend
            for(int i = 1; i < 4;i++) {
                
                line = new Line(0, 0, 20, 0);
                
                // Style the line
                line.setStrokeWidth(BAR_WIDTH);
                line.setStrokeLineCap(StrokeLineCap.BUTT);
                line.setEffect(lineLighting);
                
                switch(i) {
                    case 1:
                        label = new Label("B-frame");
                        line.setStroke(Frame.bColor);
                        break;
                    case 2:
                        label = new Label("P-frame");
                        line.setStroke(Frame.pColor);
                        break;
                    case 3:
                        label = new Label("I-frame");
                        line.setStroke(Frame.iColor);
                        break;
                }
                
                legendBox.getChildren().addAll(line,label);
                
            }
            
            legendBox.setSpacing(10);
            legendBox.setLayoutX(getWidth() / 2 - legendBox.getWidth() / 2 - MARGIN);
            legendBox.setLayoutY(getHeight() + MARGIN / 2);
            bottomLayer.getChildren().add(legendBox);
            

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
            double maxPacketSize = getMaxPacketSize(framesFrom, howManyBars);

            int packetSize;

            Line line;

            int frameID = 0;
            
            boolean lowGraphics = Boolean.parseBoolean(Statics.settings.getSettings().getProperty(Settings.lowGraphicsMode));

            double x;
            // All bars start from the 0 point
            double yStart = getHeight();

            for(i = 0; i < howManyBars; i++) {

                // get Frame
                Frame frame = project.frames.get(framesFrom + frameID++);
                    

                // get packet size for this frame
                packetSize = frame.getPacketSize();

                // x location of a bar
                x = MARGIN / 2 + (barSpace * i);

                // Draw a bar, bottom to up
                line = new Line(x, yStart, x, getHeight() - (packetSize / maxPacketSize) * (getHeight() - MARGIN));

                // Style the line
                line.setStroke(frame.getFrameColor());
                line.setStrokeWidth(BAR_WIDTH);
                line.setStrokeLineCap(StrokeLineCap.BUTT);
                if(!lowGraphics)
                    line.setEffect(lineLighting);
                
                // Tooltip to indicate the packet size - Meh tooltips suck REPLACE IT
                Tooltip t = new Tooltip(Integer.toString((int)packetSize));
                Tooltip.install(line, t);
                
                line.setUserData(frame);

                // add line to the Layer
                topLayer.getChildren().add(line);
            }

            // Draw start and end frame numbers
            Text textFramesFrom = new Text(0, MARGIN / 2, "frame " + framesFrom);
            Text textFramesTo = new Text("frame " + (framesFrom + howManyBars - 1));
            textFramesTo.setX(getWidth() + MARGIN / 2 - textFramesTo.getBoundsInLocal().getWidth());
            textFramesTo.setY(MARGIN / 2);

            topLayer.getChildren().addAll(textFramesFrom, textFramesTo);

        
        } catch (Exception ex) {
            Logger.log(BarGraph.class.getSimpleName(), "draw top layer", ex);
        }
    }
    
    private double getMaxPacketSize(int framesFrom, int howManyFrames) {
        int maxPacketSize = 0;
        int tester;
        int frameID = 0;
        Frame frame;
        for(i = 0; i < howManyFrames; i++) {
            // get packet size for a frame
            frame = project.frames.get(framesFrom + frameID++);
            
            tester = frame.getPacketSize();
            
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
            if(fineTuneIsActive) {
                if(fineTuneSlider.getValue() > 0) {
                    slider.setValue(slider.getValue() + (Math.pow(fineTuneSlider.getValue(), 2)) );
                } else {
                    slider.setValue(slider.getValue() - (Math.pow(fineTuneSlider.getValue(), 2)) );
                }
            }
                
        }
    }
    
    public void drawNeeded() {
        drawNeeded.compareAndSet(false, true);
    }
    
    public void close() {
        project = null;
    }
    
}
