/*
 * Software made by SHOT(by)GUN <https://twitter.com/SHOTbyGUN>
 */

package lib;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 *
 * @author SHOT(by)GUN
 */
public class DataDumper extends RootThread {
    
    private final ConcurrentLinkedQueue<String> data;
    private final PrintWriter out;
    
    public DataDumper(ConcurrentLinkedQueue data) throws Exception {
        super(DataDumper.class.getSimpleName());
        this.data = data;
        thread.setDaemon(true);
        out = new PrintWriter(new BufferedWriter(new FileWriter(FileOperations.getOrCreateFile(Statics.dataDumpFilePath), true)));
    }

    @Override
    public void run() {
        
        String line;
        
        while(keepRunning) {
            try {
                
                Thread.sleep(1000);
                
                while((line = data.poll()) != null) {
                    out.println(line);
                }
                out.flush();
                
            } catch (Exception ex) {
                Logger.log(DataDumper.class.getSimpleName(), "exception while running", ex);
            }
        }
    }
    
}
