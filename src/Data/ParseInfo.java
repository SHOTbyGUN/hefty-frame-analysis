/*
 * Software made by SHOT(by)GUN <https://twitter.com/SHOTbyGUN>
 */

package Data;

import lib.Logger;
import lib.Statics;

/**
 *
 * @author SHOT(by)GUN
 */
public class ParseInfo {
    
    public static int getDurationInSeconds(String durationLine) {
        // TODO detect duration in seconds * fps * 2
        // = Expected frames total
        // = Progressbar possbile
        try {
            int startPoint = durationLine.indexOf(": ") + 2;
            int endPoint = durationLine.indexOf(", ") - 3;
            String timeString = durationLine.substring(startPoint, endPoint);
            String[] slices = timeString.split(":");

            // Seconds
            int result = Integer.parseInt(slices[2]);
            // Minutes
            result += Integer.parseInt(slices[1]) * 60;
            // Hours
            result += Integer.parseInt(slices[0]) * 60 * 60;


            if(Statics.debug) {
                System.out.println("getDurationInSeconds Debug:");
                System.out.println(durationLine);
                System.out.println(timeString);
                System.out.println(result);
            }

            return result;
        } catch (Exception ex) {
            Logger.log(ParseInfo.class.getSimpleName(), "getDurationInSeconds", ex);
            return 0;
        }
    }
    
    public static int getFrameRate(String fpsLine) {
        
        String locateString = " fps, ";
        
        // if we are not successfull return -1 to indicate null
        int fps = -1;
        try {
            
            if(!fpsLine.contains(locateString))
                return -1;
            
            int endPoint = fpsLine.indexOf(locateString);
            String fpsString = fpsLine.substring(endPoint - 2, endPoint);
            
            if(Statics.debug) {
                System.out.println("getFrameRate debug");
                System.out.println(fpsLine);
                System.out.println(fpsString);
            }
            
            fps = Integer.parseInt(fpsString);
            
        } catch (Exception ex) {
            Logger.log(ParseInfo.class.getSimpleName(), "getDurationInSeconds", ex);
        }
        
        return fps;
    }
    
}
