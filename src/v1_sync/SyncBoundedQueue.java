package v1_sync;

import common.BoundedQueue;
import common.Order;

import java.util.ArrayDeque;
import java.util.Queue;

import static common.Logger.log;

public class SyncBoundedQueue implements BoundedQueue {

    private final Queue<Order> queue = new ArrayDeque<>();
    private final int max;

    public SyncBoundedQueue(int max) {
        this.max = max;
    }

    @Override
    public synchronized void put(Order data) {

        // 큐가 꽉 차있으면 생산자는 대기
        while (queue.size() == max) {
            log("❌ 큐가 가득찼습니다. 대기합니다. [RUNNABLE -> WAITING]");
            try {
                wait(); // 생산자 스레드: RUNNABLE -> WAITING
                log("생산을 시도합니다. [WAITING -> RUNNABLE] "); // 생산자 깨어남: WAITING -> RUNNABLE
            } catch (InterruptedException e) {
                return;
            }
        }

        queue.offer(data);
        notify(); // 임의의 대기 스레드에게 notify 신호: WAITING -> RUNNABLE
    }

    @Override
    public synchronized Order take() {

        // 큐가 비어있으면 소비자는 대기
        while (queue.isEmpty()) {
            log("❌ 큐에 데이터가 없습니다. 대기합니다. [RUNNABLE -> WAITING]");
            try {
                wait(); // 소비자 스레드 : RUNNABLE -> WAITING
                log("소비를 시도합니다. [WAITING -> RUNNABLE]"); // 소비자 깨어남: WAITING -> RUNNABLE
            } catch (InterruptedException e) {
                return null;
            }
        }

        Order data = queue.poll();
        notify(); // 임의의 대기 스레드에게 notify 신호: WAITING -> RUNNABLE

        return data;
    }

    @Override
    public String toString() {
        return queue.toString();
    }
}
