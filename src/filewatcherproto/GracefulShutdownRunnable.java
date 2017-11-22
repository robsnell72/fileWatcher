package filewatcherproto;

public abstract class GracefulShutdownRunnable implements Runnable {
    boolean shutdown = false;

    public void shutdownGracefully() {
        shutdown = true;
    }
}
