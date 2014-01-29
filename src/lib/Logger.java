package lib;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.concurrent.ConcurrentLinkedQueue;
import javafx.application.Platform;
import javafx.scene.control.TextArea;
import org.apache.commons.lang3.exception.ExceptionUtils;

/**
 *
 * @author shotbygun
 */
public class Logger extends TickThread {
    
    private final File logFile;
    private final PrintWriter out;
    
    private String textOut;
    
    public Logger(File logFile) throws IOException {
        super(Logger.class.getSimpleName());
        this.logFile = logFile;
        
        // access the log file with append mode
        out = new PrintWriter(new BufferedWriter(new FileWriter(logFile, true)));
    }

    @Override
    public void runTick() {
        try {
            
            Thread.sleep(1000);
            
            // Sleep is before writing because,
            // if this thread is keepRunning = false state
            // we still flush the last remaining log data
            // before exiting
            
            do {
                textOut = logToFilePool.poll();
                if(textOut != null)
                    out.println(textOut);
            } while(textOut != null);
            
            out.flush();
            
            Platform.runLater(updateLogGui);
        } catch (Exception ex) {
            log(ex);
        } finally {
            
            // if this is the last tick, close the file
            
            if(!keepRunning)
                out.close();
            
        }
    }
    
    
    // STATICS
    
    // LOGGER VARIABLES
    public static final String dateTimeFormat = "yyyy.MM.dd_HH:mm:ss";
    public static boolean showSourceNameInConsoleMessages = true;
    private static final String consoleTextSeparator = " ";
    private static final String logFileTextSeparator = ";";
    private static final String logFileEmptyException = "null";
    public static volatile ConcurrentLinkedQueue<String> logToFilePool = new ConcurrentLinkedQueue<>();
    public static volatile ConcurrentLinkedQueue<String> logToGUI = new ConcurrentLinkedQueue<>();
    
    // MAKE ALL MESSAGES GO TROUGH THIS FUNCTION
    
    public static void log(String source, String message) {
        log(source, message, null);
    }
    
    public static void log(Exception exception) {
        log(exception.getClass().getSimpleName(), exception.getMessage(), exception);
    }
    
    public static void log(String source, String message, Exception exception) {
        // add message to logToFilePool
        String timeStamp = new SimpleDateFormat(dateTimeFormat).format(Calendar.getInstance().getTime());
        
        if(exception == null) {
            logToFilePool.add(timeStamp + logFileTextSeparator 
                    + source + logFileTextSeparator 
                    + message + logFileTextSeparator
                    + logFileEmptyException);
        } else {
            logToFilePool.add(timeStamp + logFileTextSeparator
                    + source + logFileTextSeparator
                    + message + logFileTextSeparator 
                    + ExceptionUtils.getStackTrace(exception));
        }
        
        // print message to GUI log pool
        if(showSourceNameInConsoleMessages) {
            logToGUI.add(source + consoleTextSeparator + message);
        } else {
            logToGUI.add(message);
        }
        
        // print message to system out
        if(Statics.debug) {
            if(exception == null) {
                System.out.println(source + consoleTextSeparator
                    + message);
            } else {
                System.out.println(source + consoleTextSeparator
                    + message + consoleTextSeparator
                    + ExceptionUtils.getStackTrace(exception));
            }
        }
    }
    
    private final Runnable updateLogGui = new Runnable() {

        String logLine;
        
        @Override
        public void run() {
            TextArea textArea = Statics.mainGuiController.getLogTextArea();
            while((logLine = logToGUI.poll()) != null) {
                textArea.appendText(logLine + "\n");
            }
        }
    };
    
}
