package v1_sync;

import common.BoundedQueue;
import common.Order;

import static common.Logger.log;

public class SyncConsumer implements Runnable {
    private final BoundedQueue queue;

    public SyncConsumer(BoundedQueue queue) {
        this.queue = queue;
    }

    @Override
    public void run() {
        log("〰️ [소비 시도]");
        Order data = queue.take();
        log("➖ [소비 완료]: " + data + " <<== " + queue);
    }
}
