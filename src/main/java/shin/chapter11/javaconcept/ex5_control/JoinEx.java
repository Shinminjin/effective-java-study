package shin.chapter11.javaconcept.ex5_control;

/**
 * 쓰레드 자신이 하던 작업을 잠시 멈추고
 * 다른 쓰레드가 지정된 시간동안 작업을 수행하도록 할 때 join()을 사용한다.
 *
 * void join(), void join(long millis), void join(long millis, int nanos)
 * 시간을 지정하지 않으면, 해당 쓰레드가 작업을 모두 마칠 때까지 기다리게 된다.
 *
 * 작업 중에 다른 쓰레드의 작업이 먼저 수행되어야할 필요가 있을 때 join()을 사용한다.
 *
 * join()도 sleep()처럼 interrupt()에 의해 대기상태에서 벗어날 수 있으며,
 * join()이 호출되는 부분을 try-catch문으로 감싸야 한다.
 *
 * join()은 sleep()과 유사한 점이 많은데,
 * sleep()과 다른 점은 join()은 현재 쓰레드가 아닌 특정 쓰레드에 대해 동작하므로 static 메서드가 아니라는 것이다.
 */
public class JoinEx {
    static long startTime = 0;

    /**
     * join()을 사용하지 않았으면 main 쓰레드는 바로 종료되었겠지만,
     * join()으로 쓰레드 th1과 th2의 작업을 마칠 때까지 main쓰레드가 기다리도록 했다.
     * 그래서 main쓰레드가 두 쓰레드의 작업에 소요된 시간을 출력할 수 있다.
     */
    public static void main(String[] args) {
        JoinExThread1 th1 = new JoinExThread1();
        JoinExThread2 th2 = new JoinExThread2();
        th1.start();
        th2.start();
        startTime = System.currentTimeMillis();

        try {
            th1.join();
            th2.join();
        } catch (InterruptedException e) {}

        System.out.print("소요시간:" + (System.currentTimeMillis() - JoinEx.startTime));
    } // main
}

class JoinExThread1 extends Thread {
    @Override
    public void run() {
        for (int i = 0; i < 300; i++) {
            System.out.print(new String("-"));
        }
    } // run()
}

class JoinExThread2 extends Thread {
    @Override
    public void run() {
        for (int i = 0; i < 300; i++) {
            System.out.print(new String("|"));
        }
    } // run()
}
