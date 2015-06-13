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
import java.util.Arrays;
import java.util.LinkedList;
import java.util.concurrent.locks.ReentrantLock;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;

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
        LinkedList<String> paramList = new LinkedList<>();
        paramList.add(project.videoFileAbsolutePath);
        return execute(paramList, false);
    }
    
    @Override
    public void run() {
        
        Statics.jobList.addJob.add(this);
        
        Frame frame;
        String[] splitRow, frameType, frameSize;
        
        String rowSplitter = ";";
        String kvSplitter = "=";
        
        LinkedList<String> paramList = new LinkedList<>();
        
        // -v quiet -show_frames -of compact=s=;:p=0 -show_entries frame=pkt_size,pict_type:frame_tags -select_streams v:0 "D:\Videot\Stream\filu (05).mp4"
        
        // Build the param list
        
        String[] tempParam = "-v quiet -show_frames -of compact=s=;:p=0 -show_entries frame=pkt_size,pict_type:frame_tags -select_streams v:0".split(" ");
        paramList.addAll(Arrays.asList(tempParam));
        paramList.add(project.videoFileAbsolutePath);
        
        BufferedReader data = execute(paramList, true);
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

    public BufferedReader execute(LinkedList<String> paramList, boolean runOnBackground) {
        try {
            // we should not need be able to read video info and frame data at the same time.... but this prevents it anyway
            readLock.lock();
            // -v quiet -show_frames -of compact=p=0 -show_entries frame=pkt_size,pict_type:frame_tags -select_streams v:0 "D:\Videot\Stream\filu (05).mp4"
            
            // -select_streams v:0
            /*
            It is impossible to start executable with low priority , and then to be able to destroy it in windows
            if(SystemUtils.IS_OS_WINDOWS)
                process = Runtime.getRuntime().exec("cmd /c start /B /low " + Statics.settings.getSettings().getProperty("ffprobePath") + " " + params);
            else 
                */
            paramList.addFirst(Statics.settings.getSettings().getProperty("ffprobePath"));
            
            
            if(SystemUtils.IS_OS_LINUX || SystemUtils.IS_OS_UNIX || SystemUtils.IS_OS_MAC) {
                paramList.addFirst("-n 15");
                paramList.addFirst("nice");
            }
            
            // Create array
            String[] params = new String[paramList.size()];
            params = paramList.toArray(params);
            
            // Debug params
            //process = Runtime.getRuntime().exec("/usr/bin/nice -n 15 " + Statics.settings.getSettings().getProperty("ffprobePath") + " " + params);
            System.out.println(Arrays.toString(params));
            
            // execute command
            process = Runtime.getRuntime().exec(params);
            
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
                        } finally {
                            System.out.println("BackGround thread finished executing");
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
