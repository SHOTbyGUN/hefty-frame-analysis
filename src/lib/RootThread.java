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
    
    
    public RootThread(String threadName) {
        this.threadName = threadName;
        thread = new Thread(this);
        thread.setName(threadName);
    }
    
    public void start() {
        keepRunning = true;
        thread.start();
    }
    
    public void stop() {
        keepRunning = false;
    }
    
}
