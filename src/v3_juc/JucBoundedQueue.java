package v3_juc;

import common.BoundedQueue;
import common.Order;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class JucBoundedQueue implements BoundedQueue {

    private BlockingQueue<Order> queue;

    public JucBoundedQueue(int max) {
        this.queue = new ArrayBlockingQueue<>(max);
    }

    @Override
    public void put(Order order) {
        try {
            queue.put(order);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Order take() {
        try {
            return queue.take();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String toString() {
        return queue.toString();
    }
}
