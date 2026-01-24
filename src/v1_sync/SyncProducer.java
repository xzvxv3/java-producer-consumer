package v1_sync;

import common.BoundedQueue;
import common.Order;

import java.util.Random;

import static common.Logger.log;

public class SyncProducer implements Runnable {

    private final BoundedQueue queue;
    private Order order;

    public SyncProducer(BoundedQueue queue, Order order) {
        this.queue = queue;
        this.order = order;
    }

    @Override
    public void run() {
        // isInterrupted(), interrupted() 차이점
        log("〰️ [생산 시도]: " + order);
        queue.put(order);
        log("➕ [생산 완료]: " + order + " ==>> " + queue);

    }
}
