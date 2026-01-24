package v3_juc;

import common.BoundedQueue;
import common.Order;

import static common.Logger.log;

public class JucConsumer implements Runnable{

    private final BoundedQueue queue;

    public JucConsumer(BoundedQueue queue) {
        this.queue = queue;
    }

    @Override
    public void run() {
        log("〰️ [소비 시도]");
        Order order = queue.take();
        log("➖ [소비 완료]: " + order + " <<== " + queue);
    }
}
