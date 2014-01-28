/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lib;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 *
 * @author shotbygun
 */
public abstract class RootThread implements Runnable {
    
    protected String threadName;
    protected Thread thread;
    protected boolean keepRunning;
    
    protected int ticks;
    public static final int ticksOutAt = 1000;
    private int ticksOut;
    private final Lock ticksOutLock = new ReentrantLock();
    
    public RootThread(String threadName) {
        this.threadName = threadName;
        thread = new Thread(this);
        thread.setName(threadName);
    }
    
    public final String getClassName(RootThread myClass) {
        return myClass.getClass().getSimpleName();
    }
    
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
    
    public void start() {
        keepRunning = true;
        thread.start();
    }
    
    public void stop() {
        keepRunning = false;
    }
    
}
