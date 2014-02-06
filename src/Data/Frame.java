/*
 * Software made by SHOT(by)GUN <https://twitter.com/SHOTbyGUN>
 */

package Data;

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
    
    private FrameType frameType;
    private int frameSize;
    
    
    public Frame(FrameType frameType, int frameSize) {
        this.frameType = frameType;
        this.frameSize = frameSize;
    }
    
    
    public int getPacketSize() {
        return frameSize;
    }
    
    public static FrameType getFrameType(String frameTypeString) {
        
        try {
            if(frameTypeString.equals("B"))
                return FrameType.B;
            else if (frameTypeString.equals("P"))
                return FrameType.P;
            else if (frameTypeString.equals("I")) {
                return FrameType.I;
            } else {
                return FrameType.UNKNOWN;
            }
        } catch (Exception ex) {
            Logger.log(Frame.class.getSimpleName(), "error getFrameType", ex);
            return FrameType.UNKNOWN;
        }
    }
    
    public Color getFrameColor() {
        
        switch(frameType) {
            case B:
                return bColor;
            case P:
                return pColor;
            case I:
                return iColor;
            case IDR:
                return idrColor;
            default:
                return unknownColor;
        }
    }
    
    
}
