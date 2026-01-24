package v3_juc;

import common.BoundedQueue;
import common.Order;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static common.Logger.log;
import static common.ThreadUtil.sleep;

public class JucMain {
    private static final String[] menus = {"치킨", "피자", "햄버거", "떡볶이", "초밥", "파스타", "돈까스", "쌀국수", "짜장면", "삼겹살"};

    public static void main(String[] args) {
        BoundedQueue queue = new JucBoundedQueue(2);

        producerFirst(queue);
        // consumerFirst(queue);
    }

    private static void consumerFirst(BoundedQueue queue) {
        List<Thread> threads = new ArrayList<>();

        startConsumer(queue, threads);
        printThreadsState(queue, threads);

        startProducer(queue, threads);
        printThreadsState(queue, threads);
    }

    private static void producerFirst(BoundedQueue queue) {
        List<Thread> threads = new ArrayList<>();

        startProducer(queue, threads);
        printThreadsState(queue, threads);

        startConsumer(queue, threads);
        printThreadsState(queue, threads);
    }

    private static void startProducer(BoundedQueue queue, List<Thread> threads) {
        log("============ [생산자 시작] ============");

        for(int i = 1; i <= 3; i++) {
            Thread producer = new Thread(new JucProducer(queue, new Order(i, menus[new Random().nextInt(menus.length)])), "생산자" + i);
            threads.add(producer);
            producer.start();
            sleep(500);
        }

        System.out.println();
    }


    private static void startConsumer(BoundedQueue queue, List<Thread> threads) {
        log("============ [소비자 시작] ============");
        for(int i = 1; i <= 3; i++) {
            Thread consumer = new Thread(new JucConsumer(queue), "소비자" + i);
            threads.add(consumer);
            consumer.start();
            sleep(500);
        }

        System.out.println();
    }

    private static void printThreadsState(BoundedQueue queue, List<Thread> threads) {
        log("============ 스레드 상태 출력 ============");

        log("현재 상태 출력, 큐 데이터: " + queue);
        for (Thread thread : threads) {
            log(thread.getName() + " : " + thread.getState());
        }

        System.out.println();
    }
}
