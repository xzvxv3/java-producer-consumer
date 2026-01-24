package common;

public interface BoundedQueue {
    void put(Order data);

    Order take();
}
