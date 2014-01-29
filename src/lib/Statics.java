/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lib;

import hefty.frame.analysis.HeftyApplication;
import hefty.frame.analysis.MainGUIController;
import java.io.File;
import javafx.application.HostServices;
import javafx.scene.Parent;

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
    public static final String version = "v0.1";
    public static boolean debug = true;
    public static boolean dumpData = false;
    
    public static final String logFilePath = FileOperations.getWorkingDirectory() + File.separator + "HeftyFrameAnalysis.log";
    
    
    // Public classes
    public static MainGUIController mainGuiController;
    public static HeftyApplication application;
    public static JobList jobList;
    public static Logger logger;
    
    // Runtime variables
    public static String ffprobeExecutablePath = "D:\\Ohjelmat\\ffprobe\\bin\\ffprobe.exe";
}
