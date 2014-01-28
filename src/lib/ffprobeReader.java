/*
 * Software made by SHOT(by)GUN <https://twitter.com/SHOTbyGUN>
 */

// Uses ffmpeg ffprobe executable, get it from
// http://www.ffmpeg.org/download.html

package lib;

import Data.Frame;
import Data.Project;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.locks.ReentrantLock;

/**
 *
 * @author SHOT(by)GUN
 */
public class ffprobeReader extends RootThread {
    
    // Required info
    Project project;
    
    // internal variables
    BufferedReader input, error;
    Process process;
    String line;
    String errorString, inputString;
    
    // Locking
    ReentrantLock readLock = new ReentrantLock();
    
    public ffprobeReader(Project project) throws Exception {
        super(ffprobeReader.class.getSimpleName());
        this.project = project;
    }
    
    public BufferedReader getVideoInfo() {
        return execute('"' + project.videoFileAbsolutePath + '"');
    }
    
    @Override
    public void run() {
        
        Frame frame = null;
        String[] splitString;
        
        BufferedReader data = execute("-show_frames \"" + project.videoFileAbsolutePath + '"');
        try {
            while (keepRunning && (line = data.readLine()) != null) {
                // close the thread (aborted) OR do something with each line
                
                if(frame == null) {
                    // We dont have frame open
                    // Look for new frame opening
                    if(line.startsWith("[FRAME")) {
                        frame = new Frame();
                    }
                } else {
                    // we have frame, do we want to close it?
                    if(line.startsWith("[/FRAME")) {
                        frame = null;
                    } else {
                        // ok we did not close the frame
                        // we must have new data to the current one
                        
                        splitString = line.split("=");
                        frame.frameData.put(splitString[0], splitString[1]);
                    }
                }
                
                
                
            }
        } catch (IOException ex) {
            Logger.log(threadName, "error reading file: " + project.videoFileAbsolutePath, ex);
        }
    }

    public BufferedReader execute(String params) {
        try {
            // we should not need be able to read video info and frame data at the same time.... but this prevents it anyway
            readLock.lock();
            // Example command line: "D:\Ohjelmat\ffprobe\bin\ffprobe.exe" -show_frames "D:\Videot\Stream\filu (02).mp4"
            process = Runtime.getRuntime().exec(Statics.ffprobeExecutablePath + " " + params);
            process.waitFor();
            input = new BufferedReader(new InputStreamReader(process.getInputStream()));
            error = new BufferedReader(new InputStreamReader(process.getErrorStream()));

            /*
            // read errors
            errorString = "";
            while ((line = error.readLine()) != null) {
                errorString += line;
            }
            */
            
            // No idea why program outputs everything in error stream instead of normal stream
            return error;
            
            
        } catch (IOException ex) {
            Logger.log(threadName, "error reading file: " + project.videoFileAbsolutePath, ex);
        } catch (InterruptedException ex) {
            Logger.log(threadName, "reading interrupted");
        } catch (Exception ex) {
            Logger.log(ex);
        } finally {
            readLock.unlock();
        }
        
        return null;
    }
    
}
