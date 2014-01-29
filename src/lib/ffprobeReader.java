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
import java.util.logging.Level;

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
        
        thread.setDaemon(true);
        
        this.project = project;
    }
    
    public BufferedReader getVideoInfo() {
        return execute('"' + project.videoFileAbsolutePath + '"', false);
    }
    
    @Override
    public void run() {
        
        Statics.jobList.jobList.add(this);
        
        Frame frame = null;
        String[] splitString;
        
        BufferedReader data = execute("-show_frames \"" + project.videoFileAbsolutePath + '"', true);
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
                
                
                addTick();
            }
            
            // if we got here we succeeded
            flushTicks();
            Statics.jobList.removeJob.add(this);
            Logger.log(project.projectName, "Frames imported");
            
        } catch (IOException ex) {
            Logger.log(threadName, "error reading file: " + project.videoFileAbsolutePath, ex);
        }
    }

    public BufferedReader execute(String params, boolean runOnBackground) {
        try {
            // we should not need be able to read video info and frame data at the same time.... but this prevents it anyway
            readLock.lock();
            // Example command line: "D:\Ohjelmat\ffprobe\bin\ffprobe.exe" -show_frames "D:\Videot\Stream\filu (02).mp4"
            process = Runtime.getRuntime().exec(Statics.ffprobeExecutablePath + " " + params);
            input = new BufferedReader(new InputStreamReader(process.getInputStream()));
            error = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            
            if(runOnBackground) {
                Thread backGroundThread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            while((line = error.readLine()) != null) {
                                System.out.println(line);
                            }
                            process.waitFor();
                        } catch (InterruptedException ex) {
                            Logger.log("ffprobeReader", "BackGround thread interrupted", ex);
                        } catch (IOException ex) {
                            Logger.log("ffprobeReader", "BackGround thread input read error", ex);
                        }
                    }
                });
                backGroundThread.setDaemon(true);
                backGroundThread.start();
            } else {
                process.waitFor();
            }
            
            // ffprobe outputs video info to error stream and frame data to input stream
            if(runOnBackground)
                return input;
            else
                return error;
            
            
        } catch (IOException ex) {
            Logger.log(threadName, "error reading file: " + project.videoFileAbsolutePath, ex);
        } catch (Exception ex) {
            Logger.log(ex);
        } finally {
            readLock.unlock();
        }
        
        return null;
    }
    
    private void flushTicks() {
        ticksOutLock.lock();
        ticksOut += ticks;
        ticks = 0;
        ticksOutLock.unlock();
    }
    
    @Override
    public void stop() {
        Statics.jobList.removeJob.add(this);
        super.stop();
    }
    
}
