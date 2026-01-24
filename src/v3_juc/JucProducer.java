package v3_juc;

import common.BoundedQueue;
import common.Order;

import static common.Logger.log;

public class JucProducer implements Runnable {

    private final BoundedQueue queue;
    private final Order order;

    public JucProducer(BoundedQueue queue, Order order) {
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
