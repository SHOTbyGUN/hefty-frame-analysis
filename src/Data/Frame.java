/*
 * Software made by SHOT(by)GUN <https://twitter.com/SHOTbyGUN>
 */

package Data;

import java.util.HashMap;
import javafx.scene.paint.Color;
import lib.Logger;

/**
 *
 * @author SHOT(by)GUN
 */
public class Frame {
    
    public static Color bColor = Color.GREEN;
    public static Color pColor = Color.BLUE;
    public static Color iColor = Color.ORANGE;
    public static Color idrColor = Color.RED;
    public static Color audioColor= Color.BLACK;
    public static Color unknownColor = Color.GRAY;
    
    public HashMap<String, String> frameData = new HashMap();
    
    public int getPacketSize() {
        try {
            return Integer.parseInt(frameData.get("pkt_size"));
        } catch (Exception ex) {
            Logger.log(Frame.class.getSimpleName(), "unable to get Packet Size", ex);
            return 0;
        }
    }
    
    public FrameType getFrameType() {
        
        try {
            if(frameData.get("media_type").equals("audio")) {
                return FrameType.Audio;
            } else {

                String frameTypeString = frameData.get("pict_type");

                if(frameTypeString.equals("B"))
                    return FrameType.B;
                else if (frameTypeString.equals("P"))
                    return FrameType.P;
                else if (frameTypeString.equals("I")) {
                    if(frameData.get("key_frame").equals("0"))
                        return FrameType.I;
                    else
                        return FrameType.IDR;
                } else {
                    return FrameType.UNKNOWN;
                }
            }
        } catch (Exception ex) {
            Logger.log(Frame.class.getSimpleName(), "error getFrameType", ex);
            return FrameType.UNKNOWN;
        }
    }
    
    public Color getFrameColor() {
        
        final FrameType myEnum = getFrameType();
        
        switch(myEnum) {
            case B:
                return bColor;
            case P:
                return pColor;
            case I:
                return iColor;
            case IDR:
                return idrColor;
            case Audio:
                return audioColor;
            default:
                return unknownColor;
        }
    }
    
    
}
