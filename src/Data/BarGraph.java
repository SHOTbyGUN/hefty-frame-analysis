/*
 * Software made by SHOT(by)GUN <https://twitter.com/SHOTbyGUN>
 */

package Data;

import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import lib.Logger;

/**
 *
 * @author SHOT(by)GUN
 */
public class BarGraph {
    
    public static final int DEFAULT_HOWMANY = 20;
    
    private final Project project;
    private final BarChart<String, Number> barChart;
    private final VBox vBox = new VBox();
    
    private final XYChart.Series bFrames, pFrames, iFrames, idrFrames, audioFrames;
    
    // private global local variables lol np
    private int i;
    private HashMap<String, String> frameData;
    
    public BarGraph(final Project project) {
        
        this.project = project;
        
        // Build barChart
        final CategoryAxis xAxis = new CategoryAxis();
        final NumberAxis yAxis = new NumberAxis();
        barChart = new BarChart<>(xAxis,yAxis);
        barChart.setTitle(project.projectName);
        barChart.setAnimated(false);
        xAxis.setLabel("Frame number");  
        xAxis.setTickLabelRotation(90);
        yAxis.setLabel("Bitrate");
        
        // Build batChart series
        bFrames = new XYChart.Series();
        bFrames.setName("B-frames");
        
        pFrames = new XYChart.Series();
        pFrames.setName("P-frames");
        
        iFrames = new XYChart.Series();
        iFrames.setName("I-frames");
        
        idrFrames = new XYChart.Series();
        idrFrames.setName("IDR-frames");
        
        audioFrames = new XYChart.Series();
        audioFrames.setName("Audio-frames");
        
        barChart.getData().addAll(bFrames, pFrames, iFrames, idrFrames, audioFrames);
        
        vBox.getChildren().add(barChart);
        
        // Build other components
        
        Button refreshButton = new Button("Refresh");
        refreshButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent t) {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        updateBarGraph(100, DEFAULT_HOWMANY);
                    }
                });
            }
        });
        
        vBox.getChildren().add(refreshButton);
        
    }
    
    public Node getBarChartNode() {
        updateBarGraph(0, DEFAULT_HOWMANY);
        return vBox;
    }
    
    public void addRow(XYChart.Series realSeries) {
        System.out.println("row data: " + Integer.toString(i) + ", " + frameData.get("pkt_size"));
        int bits = Integer.parseInt(frameData.get("pkt_size").split(" ")[0]);
        realSeries.getData().add(new XYChart.Data<>(Integer.toString(i), bits));

        for(Series<String, Number> fakeSeries : barChart.getData()) {
            if(!fakeSeries.equals(realSeries))
                ((XYChart.Series) fakeSeries).getData().add(new XYChart.Data<>(Integer.toString(i), 0));
            
        }
        
    }
    
    public void updateBarGraph(int from, int howMany) {
        
        if(howMany > project.totalFrames)
            howMany = project.totalFrames;
        
        System.out.println("how many " + howMany);
        
        for(Series<String, Number> series : barChart.getData()) {
            series.getData().clear();
        }
        
        for(i = from; i < from + howMany; i++) {
            frameData = project.frames.get(i).frameData;
            
            if(frameData.get("media_type").equals("video")) {
                
                // Video frame type
                
                switch(frameData.get("pict_type")) {
                    case "B":
                        //bFrames.getData().add(new XYChart.Data<>(i, frameData.get("size")));
                        addRow(bFrames);
                        break;
                    case "P":
                        addRow(pFrames);
                        break;
                    case "I":
                        if(frameData.get("key_frame").equals("0")) {
                            addRow(iFrames);
                        } else {
                            addRow(idrFrames);
                        }
                        break;

                    default:
                        Logger.log(BarGraph.class.getSimpleName(), "unknown frame type detected: " + frameData.get("pict_type"));
                        break;
                }
            
            } else {
                // Audio frame type
                
                addRow(audioFrames);
                
            }
        }
        
    }
    
}
