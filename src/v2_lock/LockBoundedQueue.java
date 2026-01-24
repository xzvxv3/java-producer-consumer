package v2_lock;

import common.BoundedQueue;
import common.Order;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static common.Logger.log;

public class LockBoundedQueue implements BoundedQueue {

    private final Lock lock = new ReentrantLock();
    private final Condition producerCond = lock.newCondition();
    private final Condition consumerCond = lock.newCondition();

    private final Queue<Order> queue = new ArrayDeque<>();
    private final int max;

    public LockBoundedQueue(int max) {
        this.max = max;
    }

    @Override
    public void put(Order data) {
        lock.lock();
        try {
            while(queue.size() == max) {
                log("❌ 큐가 가득찼습니다. 대기합니다. [RUNNABLE -> WAITING]");
                try {
                    producerCond.await(); // 생산자 스레드: RUNNABLE -> WAITING
                    log("생산을 시도합니다. [WAITING -> RUNNABLE] "); // 생산자 깨어남: WAITING -> RUNNABLE
                } catch (InterruptedException e) {
                    return;
                }
            }
            queue.offer(data);
            consumerCond.signal(); // 대기중인 소비자 스레드에게 notify 신호: WAITING -> RUNNABLE
        } finally {
            lock.unlock();
        }
    }

    @Override
    public Order take() {
        lock.lock();
        try {
            // 큐가 비어있으면 소비자는 대기
            while (queue.isEmpty()) {
                log("❌ 큐에 데이터가 없습니다. 대기합니다. [RUNNABLE -> WAITING]");
                try {
                    consumerCond.await(); // 소비자 스레드 : RUNNABLE -> WAITING
                    log("소비를 시도합니다. [WAITING -> RUNNABLE]"); // 소비자 깨어남: WAITING -> RUNNABLE
                } catch (InterruptedException e) {
                    return null;
                }
            }
            Order data = queue.poll();
            producerCond.signal(); // 대기중인 생성자 스레드에게 notify 신호: WAITING -> RUNNABLE
            return data;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public String toString() {
        return queue.toString();
    }
}
