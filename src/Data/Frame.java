/*
 * Software made by SHOT(by)GUN <https://twitter.com/SHOTbyGUN>
 */

package Data;

import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Paint;
import javafx.scene.paint.Stop;
import lib.Logger;

/**
 *
 * @author SHOT(by)GUN
 */
public class Frame {
    
    public static Paint bColor = Color.GREEN;
    public static Paint pColor = Color.BLUE;
    public static Paint iColor = Color.RED;
    //public static Paint idrColor = getLinearPaint(Color.RED);
    public static Paint unknownColor = Color.GRAY;
    
    private final FrameType frameType;
    private final int frameSize;
    
    
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
    
    public Paint getFrameColor() {
        
        switch(frameType) {
            case B:
                return bColor;
            case P:
                return pColor;
            case I:
                return iColor;
            default:
                return unknownColor;
        }
    }
    
    
}
