/*
 * Software made by SHOT(by)GUN <https://twitter.com/SHOTbyGUN>
 */

package lib;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 *
 * @author SHOT(by)GUN
 */
public abstract class TickThread extends RootThread {
    
    protected int ticks;
    public static final int ticksOutAt = 1000;
    protected int ticksOut;
    protected final Lock ticksOutLock = new ReentrantLock();

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
    
    protected void addTick() {
        
        ticks++;
        if(ticks >= ticksOutAt) {
            if(ticksOutLock.tryLock()) {
                ticksOut += ticks;
                ticks = 0;
                ticksOutLock.unlock();
            }
        }
    }
    
    public int colletTicks() {
        ticksOutLock.lock();
        int myTicksOut = 0;
        try {
            myTicksOut = ticksOut;
            ticksOut = 0;
        } catch (Exception ex) {
            Logger.log(RootThread.class.getSimpleName(), "colletTicks lock error - " + threadName + " ticks compromised");
        } finally {
            ticksOutLock.unlock();
        }
        return myTicksOut;
    }
    
}
