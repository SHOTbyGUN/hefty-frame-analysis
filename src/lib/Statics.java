/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lib;

import hefty.HeftyApplication;
import hefty.MainGUIController;
import hefty.Settings;
import java.io.File;
import java.util.concurrent.ConcurrentLinkedQueue;
import javafx.stage.Stage;

/**
 *
 * @author shotbygun
 */
public class Statics {
    
    // Notes
    // http://stackoverflow.com/questions/14158104/javafx-barchart-color
    
    // Global Init Constants
    public static final String applicationName = "Hefty Frame Analysis";
    public static final String applicationShortName = applicationName.replace(" ", "");
    public static final String version = "v0.3";
    public static boolean debug = true;
    public static boolean dumpData = true;
    
    public static final String logFilePath = FileOperations.getWorkingDirectory() + File.separator + "HeftyFrameAnalysis.log";
    public static final String dataDumpFilePath = FileOperations.getWorkingDirectory() + File.separator + "dataDump.txt";
    
    
    // Public classes
    public static MainGUIController mainGuiController;
    public static HeftyApplication application;
    public static JobList jobList;
    public static UpdateThread updateThread;
    public static Logger logger;
    public static Settings settings;
    
    // Runtime variables
    public static Stage stage;
    public static String defaultDragAndDropText = "Drag and drop files here";
    public static ConcurrentLinkedQueue<File> importFileQueue = new ConcurrentLinkedQueue<>();
}
