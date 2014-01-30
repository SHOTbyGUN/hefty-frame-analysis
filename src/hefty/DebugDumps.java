/*
 * Software made by SHOT(by)GUN <https://twitter.com/SHOTbyGUN>
 */

package hefty;

import Data.Project;
import java.util.Map.Entry;

/**
 *
 * @author SHOT(by)GUN
 */
public class DebugDumps {
    
    public static void dumpFrameInfo(Project project, int frame) {
        System.out.println("dumping frame info");
        for(Entry<String, String> item : project.frames.get(500).frameData.entrySet()) {
            System.out.println(item.getKey() + " " + item.getValue());
        }
    }
    
}
