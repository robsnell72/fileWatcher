package filewatcherproto;

import java.util.EventObject;

public final class FileWatchEvent extends EventObject {
    private long lineNumber;
    private String line;
    
    public FileWatchEvent(Object source, long lineNumber, String line) {
        super(source);
        this.lineNumber = lineNumber;
        this.line = line;
    }
    
    public long getLineNumber() {
        return this.lineNumber;
    }
    
    public String getLine() {
        return this.line;
    }
}
