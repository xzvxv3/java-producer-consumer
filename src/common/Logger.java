package common;

// 유틸리티 클래스 패턴
public abstract class Logger {

    public static void log(Object msg) {
        System.out.printf("[%4s] %s\n", Thread.currentThread().getName(), msg);
    }
}
