# java-producer-consumer

패키지 구조
```
src
├── common        // BoundedQueue(인터페이스), Order(데이터), Logger, ThreadUtil
├── v1_sync       // synchronized 사용 (wait / notify)
├── v2_lock       // ReentrantLock 사용 (await / signal)
└── v3_juc        // BlockingQueue API 사용
```

---

### Synchronized  버전 생산자-소비자 모델

[생산자 먼저 실행]
```java
[main] ============ [생산자 시작] ============
[생산자1] 〰️ [생산 시도]: 🍚#1(삼겹살)
[생산자1] ➕ [생산 완료]: 🍚#1(삼겹살) ==>> [🍚#1(삼겹살)]
[생산자2] 〰️ [생산 시도]: 🍚#2(피자)
[생산자2] ➕ [생산 완료]: 🍚#2(피자) ==>> [🍚#1(삼겹살), 🍚#2(피자)]
[생산자3] 〰️ [생산 시도]: 🍚#3(쌀국수)
[생산자3] ❌ 큐가 가득찼습니다. 대기합니다. [RUNNABLE -> WAITING]

[main] ============ 스레드 상태 출력 ============
[main] 현재 상태 출력, 큐 데이터: [🍚#1(삼겹살), 🍚#2(피자)]
[main] 생산자1 : TERMINATED
[main] 생산자2 : TERMINATED
[main] 생산자3 : WAITING

[main] ============ [소비자 시작] ============
[소비자1] 〰️ [소비 시도]
[생산자3] 생산을 시도합니다. [WAITING -> RUNNABLE] 
[생산자3] ➕ [생산 완료]: 🍚#3(쌀국수) ==>> [🍚#2(피자), 🍚#3(쌀국수)]
[소비자1] ➖ [소비 완료]: 🍚#1(삼겹살) <<== [🍚#2(피자)]
[소비자2] 〰️ [소비 시도]
[소비자2] ➖ [소비 완료]: 🍚#2(피자) <<== [🍚#3(쌀국수)]
[소비자3] 〰️ [소비 시도]
[소비자3] ➖ [소비 완료]: 🍚#3(쌀국수) <<== []

[main] ============ 스레드 상태 출력 ============
[main] 현재 상태 출력, 큐 데이터: []
[main] 생산자1 : TERMINATED
[main] 생산자2 : TERMINATED
[main] 생산자3 : TERMINATED
[main] 소비자1 : TERMINATED
[main] 소비자2 : TERMINATED
[main] 소비자3 : TERMINATED
```
&nbsp;

[소비자 먼저 실행]
```java
[main] ============ [소비자 시작] ============
[소비자1] 〰️ [소비 시도]
[소비자1] ❌ 큐에 데이터가 없습니다. 대기합니다. [RUNNABLE -> WAITING]
[소비자2] 〰️ [소비 시도]
[소비자2] ❌ 큐에 데이터가 없습니다. 대기합니다. [RUNNABLE -> WAITING]
[소비자3] 〰️ [소비 시도]
[소비자3] ❌ 큐에 데이터가 없습니다. 대기합니다. [RUNNABLE -> WAITING]

[main] ============ 스레드 상태 출력 ============
[main] 현재 상태 출력, 큐 데이터: []
[main] 소비자1 : WAITING
[main] 소비자2 : WAITING
[main] 소비자3 : WAITING

[main] ============ [생산자 시작] ============
[생산자1] 〰️ [생산 시도]: 🍚#1(파스타)
[소비자1] 소비를 시도합니다. [WAITING -> RUNNABLE]
[소비자2] 소비를 시도합니다. [WAITING -> RUNNABLE]
[소비자2] ❌ 큐에 데이터가 없습니다. 대기합니다. [RUNNABLE -> WAITING]
[생산자1] ➕ [생산 완료]: 🍚#1(파스타) ==>> [🍚#1(파스타)]
[소비자1] ➖ [소비 완료]: 🍚#1(파스타) <<== []
[생산자2] 〰️ [생산 시도]: 🍚#2(햄버거)
[소비자3] 소비를 시도합니다. [WAITING -> RUNNABLE]
[생산자2] ➕ [생산 완료]: 🍚#2(햄버거) ==>> [🍚#2(햄버거)]
[소비자2] 소비를 시도합니다. [WAITING -> RUNNABLE]
[소비자2] ❌ 큐에 데이터가 없습니다. 대기합니다. [RUNNABLE -> WAITING]
[소비자3] ➖ [소비 완료]: 🍚#2(햄버거) <<== []
[생산자3] 〰️ [생산 시도]: 🍚#3(초밥)
[소비자2] 소비를 시도합니다. [WAITING -> RUNNABLE]
[생산자3] ➕ [생산 완료]: 🍚#3(초밥) ==>> [🍚#3(초밥)]
[소비자2] ➖ [소비 완료]: 🍚#3(초밥) <<== []

[main] ============ 스레드 상태 출력 ============
[main] 현재 상태 출력, 큐 데이터: []
[main] 소비자1 : TERMINATED
[main] 소비자2 : TERMINATED
[main] 소비자3 : TERMINATED
[main] 생산자1 : TERMINATED
[main] 생산자2 : TERMINATED
[main] 생산자3 : TERMINATED
```
➡️ 불필요한 스레드가 깨워지는 걸 확인 가능

synchronized로 동시성을 제어하려고 하다 보니 다음과 같은 한계를 발견했다.

1. notify()와 notifyAll()의 가장 큰 한계는 **특정 스레드를 선택해서 깨울 수 없다**는 점이다.
- 큐의 버퍼가 가득 찬 뒤에는 소비자를 깨워야 자연스러운 흐름이 이어지지만, notify()는 대기 중인 스레드(WAITING) 중 하나를 임의로 선택해서 깨운다. 이로 인해 운 나쁜 스레드는 **계속 선택받지 못하는 스레드 기아(Starvation)'현상**이 발생할 수 있고 공정성이 없는 상황이 돼버린다. 이 경우에 생산자용, 소비자용 스레드 대기 집합을 따로 만들어야 해결이 된다.
- 이를 피하고자 notifyAll()을 사용한다면 **불필요한 스레드까지 모두 깨우게 되어 자원이 낭비되는 비효율**이 발생한다.

1. 이 예시에는 나오지 않지만, Synchronized을 사용하면 Lock을 얻고자 BLOCKED 상태로 스레드가 대기중일때는 인터럽트가 통하지 않는 문제도 있다. (데드락 상태)

이 모든 문제는 ReentrantLock을 통해 해결할 수 있다.

---

### ReentrantLock 버전 생산자-소비자 모델

[생산자 먼저 실행]

```java
[main] ============ [생산자 시작] ============
[생산자1] 〰️ [생산 시도]: 🍚#1(돈까스)
[생산자1] ➕ [생산 완료]: 🍚#1(돈까스) ==>> [🍚#1(돈까스)]
[생산자2] 〰️ [생산 시도]: 🍚#2(치킨)
[생산자2] ➕ [생산 완료]: 🍚#2(치킨) ==>> [🍚#1(돈까스), 🍚#2(치킨)]
[생산자3] 〰️ [생산 시도]: 🍚#3(햄버거)
[생산자3] ❌ 큐가 가득찼습니다. 대기합니다. [RUNNABLE -> WAITING]

[main] ============ 스레드 상태 출력 ============
[main] 현재 상태 출력, 큐 데이터: [🍚#1(돈까스), 🍚#2(치킨)]
[main] 생산자1 : TERMINATED
[main] 생산자2 : TERMINATED
[main] 생산자3 : WAITING

[main] ============ [소비자 시작] ============
[소비자1] 〰️ [소비 시도]
[생산자3] 생산을 시도합니다. [WAITING -> RUNNABLE] 
[생산자3] ➕ [생산 완료]: 🍚#3(햄버거) ==>> [🍚#2(치킨), 🍚#3(햄버거)]
[소비자1] ➖ [소비 완료]: 🍚#1(돈까스) <<== [🍚#2(치킨)]
[소비자2] 〰️ [소비 시도]
[소비자2] ➖ [소비 완료]: 🍚#2(치킨) <<== [🍚#3(햄버거)]
[소비자3] 〰️ [소비 시도]
[소비자3] ➖ [소비 완료]: 🍚#3(햄버거) <<== []

[main] ============ 스레드 상태 출력 ============
[main] 현재 상태 출력, 큐 데이터: []
[main] 생산자1 : TERMINATED
[main] 생산자2 : TERMINATED
[main] 생산자3 : TERMINATED
[main] 소비자1 : TERMINATED
[main] 소비자2 : TERMINATED
[main] 소비자3 : TERMINATED
```
&nbsp;

[소비자 먼저 실행]
```java
[main] ============ [소비자 시작] ============
[소비자1] 〰️ [소비 시도]
[소비자1] ❌ 큐에 데이터가 없습니다. 대기합니다. [RUNNABLE -> WAITING]
[소비자2] 〰️ [소비 시도]
[소비자2] ❌ 큐에 데이터가 없습니다. 대기합니다. [RUNNABLE -> WAITING]
[소비자3] 〰️ [소비 시도]
[소비자3] ❌ 큐에 데이터가 없습니다. 대기합니다. [RUNNABLE -> WAITING]

[main] ============ 스레드 상태 출력 ============
[main] 현재 상태 출력, 큐 데이터: []
[main] 소비자1 : WAITING
[main] 소비자2 : WAITING
[main] 소비자3 : WAITING

[main] ============ [생산자 시작] ============
[생산자1] 〰️ [생산 시도]: 🍚#1(치킨)
[소비자1] 소비를 시도합니다. [WAITING -> RUNNABLE]
[생산자1] ➕ [생산 완료]: 🍚#1(치킨) ==>> [🍚#1(치킨)]
[소비자1] ➖ [소비 완료]: 🍚#1(치킨) <<== []
[생산자2] 〰️ [생산 시도]: 🍚#2(치킨)
[소비자2] 소비를 시도합니다. [WAITING -> RUNNABLE]
[생산자2] ➕ [생산 완료]: 🍚#2(치킨) ==>> [🍚#2(치킨)]
[소비자2] ➖ [소비 완료]: 🍚#2(치킨) <<== []
[생산자3] 〰️ [생산 시도]: 🍚#3(파스타)
[생산자3] ➕ [생산 완료]: 🍚#3(파스타) ==>> [🍚#3(파스타)]
[소비자3] 소비를 시도합니다. [WAITING -> RUNNABLE]
[소비자3] ➖ [소비 완료]: 🍚#3(파스타) <<== []

[main] ============ 스레드 상태 출력 ============
[main] 현재 상태 출력, 큐 데이터: []
[main] 소비자1 : TERMINATED
[main] 소비자2 : TERMINATED
[main] 소비자3 : TERMINATED
[main] 생산자1 : TERMINATED
[main] 생산자2 : TERMINATED
[main] 생산자3 : TERMINATED
```

➡️ Synchronized을 이용한 방법과 달리, 불필요한 스레드 깨움 없이 깔끔하게 흐름이 이어지는 것을 확인할 수 있다.

ReentrantLock은 synchronized 키워드 대신 lock.lock()과 lock.unlock()을 호출하여 임계 영역의 범위를 설정한다. 이때 lock.unlock()은 반드시 수행돼야 하므로 finally 블록에서 호출하도록 해야한다. 이유는 lock.unlock()을 호출하지 않아서 락을 반납하지 못하는 경우, 다른 스레드들이 락을 할당받지 못해 데드락 현상을 유발시키기 때문이다.

1. ReentrantLock을 사용하면 소비자 대기 집합과 생성자 대기 집합을 따로 생성할 수 있기 때문에 불필요한 스레드 깨움 현상을 일으키지 않는다. 이를 통해 notify()와 notifyAll()의 단점을 극복할 수 있다.

1. lockInterruptibly()를 사용하면 인터럽트를 날려 강제로 대기 상태를 풀 수 있다. → 데드락  해결
- 참고로 스레드가 락을 얻기 위해 대기할때는 synchronized일 경우 BLOCKED 상태를 사용하고, ReentrantLock일 경우에는 내부적으로 WAITING을 사용하여 인터럽트 문제를 해결한다.

```java
public void put(Order data) throws InterruptedException {
    lock.lockInterruptibly(); 
    try {
        while (queue.size() == max) {
            producerCond.await();
        }
        // ... 생략
    } finally {
        lock.unlock();
    }
}
```

---

### BlockingQueue 버전 생산자-소비자 모델

앞서 구현했던 ReentrantLock 버전의 BoundedQueue와 자바에서 제공하는 유틸리티인 ArrayBlockingQueue의 메서드는 거의 비슷하다고 볼 수 있다. 따라서 이러한 유틸리티를 사용하면 더 편하게 사용 가능하다.

아래는 직접 ArrayBlockingQueue의 클래스 구조를 살펴본 결과다.

```java
public void put(E e) throws InterruptedException {
        Objects.requireNonNull(e);
        final ReentrantLock lock = this.lock;
        lock.lockInterruptibly();
        try {
            while (count == items.length)
                notFull.await();
            enqueue(e);
        } finally {
            lock.unlock();
        }
    }
    
    ...
    

public E take() throws InterruptedException {
        final ReentrantLock lock = this.lock;
        lock.lockInterruptibly();
        try {
            while (count == 0)
                notEmpty.await();
            return dequeue();
        } finally {
            lock.unlock();
        }
    }
```

---
