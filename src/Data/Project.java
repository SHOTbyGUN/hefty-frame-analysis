/*
 * Software made by SHOT(by)GUN <https://twitter.com/SHOTbyGUN>
 */

package Data;

import java.io.BufferedReader;
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
    
    // data
    public List frames;
    public List<String> videoInfo;
    
    // data reader
    ffprobeReader reader;
    
    // class specific variables
    
    
    
    public Project(String videoFileAbsolutePath) throws Exception {
        this.videoFileAbsolutePath = videoFileAbsolutePath;
        reader = new ffprobeReader(this);
        
        // Init variables
        // TODO frames list should be initialized as correct size
        frames = new ArrayList();
        videoInfo = new ArrayList();
        
        // After init
        readVideoInfo();
    }
    
    public void createFrameList(int size) {
        frames = Collections.synchronizedList(new ArrayList(size));
    }
    
    public void startReadingFrames(int start, int end) {
        reader.start();
    }
    
    private void readVideoInfo() throws Exception {
        BufferedReader input = reader.getVideoInfo();
        if(input == null)
            throw new Exception("Unable to read video info");
        
        if(Statics.dumpData) {
            String line;
            String text = "";
            while ((line = input.readLine()) != null) {
                text += line + "\n";
            }

            Logger.log("ReadVideoInfo", text);
        } else {
            String line;
            String text = "";
            while ((line = input.readLine()) != null) {
                line = line.trim();
                if(line.startsWith("Stream #"))
                    videoInfo.add(line);
            }
            
            System.out.println("Video Info:");
            for(String entry : videoInfo) {
                System.out.println(entry);
            }
        }
    }
}
