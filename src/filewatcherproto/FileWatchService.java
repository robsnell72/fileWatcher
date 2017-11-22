package filewatcherproto;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.EventObject;

public class FileWatchService {

    private static long getNumberOfLinesInFile(File file) throws IOException {
        if (file.exists()) {
            // FileReader reads text files in the default encoding.
            InputStreamReader fileReader = new FileReader(file);

            // Always wrap FileReader in BufferedReader.
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            long lineNumber = 0;

            try {
                String line;

                while ((line = bufferedReader.readLine()) != null) {
                    lineNumber++;
                }
                
                return lineNumber;
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
        } else {
            return 0;
        }
        
        return 0;
    }

    private static File file;
    private long lastLineReported;

    public FileWatchService(File file) throws FileNotFoundException, IOException {
        this.file = file;

        if (file.exists()) {
            initFile();
        }
    }

    private void initFile() {
        this.lastLineReported = 0;
    }

    FileReadInfo getFileReadInfo() throws IOException {
        if (file.exists()) {
            //if this is the first time you are reporting on file or it has been 
            //updated and has more lines since the last time it was reported.
            long fileLengthInLines = FileWatchService.getNumberOfLinesInFile(this.file);

            if ((this.lastLineReported == 0)
                    || (fileLengthInLines > this.lastLineReported)) {
                long startLine = this.lastLineReported+1;
                long stopLine = fileLengthInLines;
                this.lastLineReported = stopLine;

                return new FileReadInfo(startLine, stopLine, true);
            }
        } else {
            return new FileReadInfo(this.lastLineReported, this.lastLineReported, false);
        }
        
        return new FileReadInfo(0, 0, false);
    }

    public class FileReadInfo {

        private long fromLine;
        private long toLine;
        private boolean changed;

        public FileReadInfo(long fromLine, long toLine, boolean read) {
            this.fromLine = fromLine;
            this.toLine = toLine;
            this.changed = read;
        }

        public long getFromLine() {
            return this.fromLine;
        }

        public long getToLine() {
            return this.toLine;
        }
        
        public boolean getChanged() {
            return this.changed;
        }
    }
}
