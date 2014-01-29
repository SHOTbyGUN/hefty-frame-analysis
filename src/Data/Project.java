/*
 * Software made by SHOT(by)GUN <https://twitter.com/SHOTbyGUN>
 */

package Data;

import java.io.BufferedReader;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import lib.Logger;
import lib.Statics;
import lib.ffprobeReader;

/**
 *
 * @author SHOT(by)GUN
 */
public class Project {
    
    // Video file path
    public final String videoFileAbsolutePath;
    public final String projectName;
    
    // data
    public List frames;
    public String videoInfoRaw;
    public long totalFrames;
    public int durationInSeconds;
    public int frameRate;
    
    // data reader
    private final ffprobeReader reader;
    
    // class specific variables
    
    
    
    public Project(String videoFileAbsolutePath) throws Exception {
        this.videoFileAbsolutePath = videoFileAbsolutePath;
        projectName = new File(videoFileAbsolutePath).getName();
        reader = new ffprobeReader(this);
        
        // Init variables
        // TODO frames list should be initialized as about correct size
        createFrameList(1024);
        
        // After init
        readVideoInfo();
    }
    
    public void createFrameList(int size) {
        frames = Collections.synchronizedList(new ArrayList(size));
    }
    
    public void startReadingFrames() {
        // Todo slice read
        reader.start();
    }
    
    private void readVideoInfo() throws Exception {
        BufferedReader input = reader.getVideoInfo();
        if(input == null)
            throw new Exception("Unable to read video info");
        
        videoInfoRaw = "";
        String line;
        String text = "";
        boolean startReading = false;
        
        if(Statics.dumpData) {
            while ((line = input.readLine()) != null) {
                text += line + "\n";
            }

            Logger.log("ReadVideoInfo", text);
        } else {
            while ((line = input.readLine()) != null) {
                if(!startReading) {
                    if(line.startsWith("Input #"))
                        startReading = true;
                    
                } else {
                    videoInfoRaw += line + "\n";
                    
                    // Parse video info here
                    
                    if(line.startsWith("  Duration:")) {
                        durationInSeconds = ParseInfo.getDurationInSeconds(line);
                    }
                    
                    if(line.startsWith("    Stream #")) {
                        int fps = ParseInfo.getFrameRate(line);
                        if(fps > 0)
                            frameRate = fps;
                    }
                }
            }
            
            if(Statics.dumpData) {
                System.out.println("Video Info Raw:");
                System.out.println(videoInfoRaw);
            }
        }
    }
    
    public void closeProject() {
        reader.stop();
    }
}
