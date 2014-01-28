/*
 * Software made by SHOT(by)GUN <https://twitter.com/SHOTbyGUN>
 */

package lib;

/**
 *
 * @author SHOT(by)GUN
 */
public abstract class TickThread extends RootThread {

    public TickThread(String threadName) {
        super(threadName);
    }
    
    @Override
    public void run() {
        while(keepRunning) {
            runTick();

            // Keep track of performance
            addTick();
        }
    }
    
    public abstract void runTick();
    
}
