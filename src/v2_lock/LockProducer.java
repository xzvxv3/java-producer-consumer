package v2_lock;

import common.BoundedQueue;
import common.Order;

import static common.Logger.log;

public class LockProducer implements Runnable {

    private final BoundedQueue queue;
    private final Order order;

    public LockProducer(BoundedQueue queue, Order order) {
        this.queue = queue;
        this.order = order;
    }

    @Override
    public void run() {
        log("〰️ [생산 시도]: " + order);
        queue.put(order);
        log("➕ [생산 완료]: " + order + " ==>> " + queue);
    }
}
