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
    
    // Background Thread
    Thread backGroundThread;
    
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
        
        Statics.jobList.addJob.add(this);
        
        Frame frame;
        String[] splitRow, frameType, frameSize;
        
        String rowSplitter = ";";
        String kvSplitter = "=";
        
        // -v quiet -show_frames -of compact=s=;:p=0 -show_entries frame=pkt_size,pict_type:frame_tags -select_streams v:0 "D:\Videot\Stream\filu (05).mp4"
        BufferedReader data = execute("-v quiet -show_frames -of compact=s=;:p=0 -show_entries frame=pkt_size,pict_type:frame_tags -select_streams v:0 \"" 
                + project.videoFileAbsolutePath + '"', true);
        
        // Expected output per line: pkt_size=407|pict_type=B
        
        try {
            while (keepRunning && (line = data.readLine()) != null) {
                // close the thread (aborted) OR do something with each line
                
                
                splitRow = line.split(rowSplitter);
                frameSize = splitRow[0].split(kvSplitter);
                frameType = splitRow[1].split(kvSplitter);
                
                frame = new Frame(Frame.getFrameType(frameType[1]), Integer.parseInt(frameSize[1]));
                project.frames.add(frame);
                project.totalFrames++;
            }
            
            // if we got here we succeeded
            data.close();
            Logger.log(project.projectName, project.frames.size() + " frames imported");
            if(Statics.dumpData) {
                System.out.println("project.totalFrames " + project.totalFrames);
                System.out.println("project.frames.size() " + project.frames.size());
            }
        } catch (IOException ex) {
            Logger.log(threadName, "error reading file: " + project.videoFileAbsolutePath, ex);
        } finally {
            Statics.jobList.removeJob.add(this);
        }
    }

    public BufferedReader execute(String params, boolean runOnBackground) {
        try {
            // we should not need be able to read video info and frame data at the same time.... but this prevents it anyway
            readLock.lock();
            // Example command line: "D:\Ohjelmat\ffprobe\bin\ffprobe.exe" -show_frames "D:\Videot\Stream\filu (02).mp4"
            // Example command line version 2:
            // -v quiet -show_frames -of compact=p=0 -show_entries frame=pkt_size,pict_type:frame_tags -select_streams v:0 "D:\Videot\Stream\filu (05).mp4"
            // TODO a way to start ffprobe in low priority
            
            // -select_streams v:0
            
            process = Runtime.getRuntime().exec(Statics.settings.getSettings().getProperty("ffprobePath") + " " + params);
            input = new BufferedReader(new InputStreamReader(process.getInputStream()));
            error = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            
            if(runOnBackground) {
                backGroundThread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            while(keepRunning && (line = error.readLine()) != null) {
                                if(Statics.dumpData) {
                                    System.out.println("Background thread output, SHOULD BE NOTHING HERE!");
                                    System.out.println(line);
                                }
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
                backGroundThread.setName("ffProbeReaderBG");
                backGroundThread.start();
            } else {
                process.waitFor();
            }
            
            // ffprobe outputs video info to error stream
            // frame data to input stream
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
    
    @Override
    public void stop() {
        super.stop();
        //Statics.jobList.removeJob.add(this);
        process.destroy();
    }
    
    public void close() {
        stop();
    }
    
}
