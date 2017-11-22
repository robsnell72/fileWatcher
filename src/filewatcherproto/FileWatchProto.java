package filewatcherproto;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author rjsnell
 */
public class FileWatchProto {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        System.out.println("before thread spawned...");
        
        FileWatchRunnableWrapper mgr = new FileWatchRunnableWrapper();
        mgr.addListener(e -> {
            System.out.printf("%d: %s%s", e.getLineNumber(), e.getLine(), System.lineSeparator());
        });

        Thread thread = new Thread(mgr);
        thread.setDaemon(true);
        thread.start();
        
        System.out.println("after...");
        System.out.println("after...");
        System.out.println("after...");
        
        try {
            //wait
            System.in.read();
            mgr.shutdownGracefully();
            try {
                thread.join();
                System.out.println("thread returned/joined...");
            } catch (InterruptedException ex) {
                Logger.getLogger(FileWatchProto.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (IOException ex) {
            Logger.getLogger(FileWatchProto.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
