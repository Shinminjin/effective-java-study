package shin.chapter11.javaconcept.ex5_control;

public class SuspendResumeStopEx2 {
    public static void main(String[] args) {
        RunImplEx2 r1 = new RunImplEx2();
        RunImplEx2 r2 = new RunImplEx2();
        RunImplEx2 r3 = new RunImplEx2();

        Thread th1 = new Thread(r1, "*");
        Thread th2 = new Thread(r2, "**");
        Thread th3 = new Thread(r3, "***");
        th1.start();
        th2.start();
        th3.start();

        try {
            Thread.sleep(2000);
            r1.suspend(); // th1.suspend()이 아님에 주의
            Thread.sleep(2000);
            r2.suspend();
            Thread.sleep(3000);
            r1.resume();
            Thread.sleep(3000);
            r1.stop();
            r2.stop();
            Thread.sleep(2000);
            r3.stop();
        } catch (InterruptedException e) {}
    }
}

/**
 * stopped과 suspended라는 boolean타입의 두 변수를 인스턴스 변수로 선언하고,
 * 이 변수를 사용해서 반복문과 조건문의 조건식을 작성한다.
 * 그리고 이 변수의 값을 변경함으로써 쓰레드의 작업이 중지되었다가 재개되거나 종료될 수 있도록 할 수 있다.
 */
class RunImplEx2 implements Runnable {
    volatile boolean suspended = false;
    volatile boolean stopped = false;

    @Override
    public void run() {
        while (!stopped) { // stopped의 값이 false인 동안 반복한다.
            if (!suspended) { // suspended의 값이 false일때만 작업을 수행한다.
                System.out.println(Thread.currentThread().getName());
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {}
            }
        }
        System.out.println(Thread.currentThread().getName() + " - stopped");
    }

    public void suspend() {
        suspended = true;
    }

    public void resume() {
        suspended = false;
    }

    public void stop() {
        stopped = true;
    }
}
