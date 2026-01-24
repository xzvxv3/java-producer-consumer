package v2_lock;

import common.BoundedQueue;
import common.Order;

import static common.Logger.log;

public class LockConsumer implements Runnable{

    private final BoundedQueue queue;

    public LockConsumer(BoundedQueue queue) {
        this.queue = queue;
    }

    @Override
    public void run() {
        log("〰️ [소비 시도]");
        Order data = queue.take();
        log("➖ [소비 완료]: " + data + " <<== " + queue);
    }
}
