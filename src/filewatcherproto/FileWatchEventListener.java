package filewatcherproto;

import java.util.EventListener;

public interface FileWatchEventListener extends EventListener {
    public void newLines(FileWatchEvent e);
}
