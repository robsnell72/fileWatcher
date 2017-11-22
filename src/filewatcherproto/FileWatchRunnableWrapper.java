package filewatcherproto;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FileWatchRunnableWrapper extends GracefulShutdownRunnable {

    private List<FileWatchEventListener> listeners = new ArrayList<>();

    public void addListener(FileWatchEventListener listener) {
        listeners.add(listener);
    }
    
    public void removeListener(FileWatchEventListener listener) {
        listeners.remove(listener);
    }
    
    public void fireNewLines(long lineNumber, String line) {
        listeners.stream().forEach(x -> x.newLines(new FileWatchEvent(this, lineNumber, line)));
    }
    
    @Override
    public void run() {
        FileWatchService logWatchService;
        File logFile = getLogfile().toFile();
        
        try {
            logWatchService = new FileWatchService(logFile);
        } catch (IOException ex) {
            ex.printStackTrace();
            return;
        }

        while (!shutdown) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException ex) {
                Logger.getLogger(FileWatchProto.class.getName()).log(Level.INFO, "Watcher thread exiting. (Expected.)", ex);
                Logger.getLogger(FileWatchProto.class.getName()).log(Level.INFO, null, ex);
            }

            FileWatchService.FileReadInfo fileReadInfo = null;

            try {
                fileReadInfo = logWatchService.getFileReadInfo();
            } catch (IOException ex) {
                Logger.getLogger(FileWatchProto.class.getName()).log(Level.SEVERE, null, ex);
            }

            if (fileReadInfo.getChanged()) {

                try {
                    printNewLines(logFile, fileReadInfo.getFromLine(), fileReadInfo.getToLine(), System.out);
                } catch (IOException ex) {
                    Logger.getLogger(FileWatchProto.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }
    
    private int printNewLines(File fileName, long readFromLine, long readThroughLine, PrintStream odIo) throws IOException {
        int lineNumber = 0;

        // This will reference one line at a time
        String line = null;
        FileReader fileReader = null;
        BufferedReader bufferedReader = null;

        try {
            // FileReader reads text files in the default encoding.
            fileReader = new FileReader(fileName);

            // Always wrap FileReader in BufferedReader.
            bufferedReader = new BufferedReader(fileReader);

            while ((line = bufferedReader.readLine()) != null) {
                lineNumber++;

                if (lineNumber >= readFromLine && lineNumber <= readThroughLine) {
                    //odIo.printf("%d: %s%s", lineNumber, line, System.lineSeparator());
                    fireNewLines(lineNumber, line);
                }
            }
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            if (fileReader != null) {
                // Always close files.
                bufferedReader.close();
            }
        }

        return lineNumber;
    }

    private static Path getLogfile() {
        return Paths.get("c:\\temp\\log.txt");
    }
}
