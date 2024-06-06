package shin.chapter11.javaconcept.ex5_control;

/**
 * suspend()는 sleep()처럼 쓰레드를 멈추게한다.
 * suspend()에 의해 정지된 쓰레드는 resume()을 호출해야 다시 실행대기 상택가 된다.
 * stop()은 호출되는 즉시 쓰레드가 종료된다.
 *
 * suspend(), resume(), stop()은 쓰레드의 실행을 제어하는 가장 손쉬운 방법이지만,
 * suspend()와 stop()이 교착상태(deadlock)를 일으키기 쉽게 작성되어있으므로 사용이 권장되지 않는다.
 *
 * 그래서 이 3개의 메서드들은 모두 'deprecated' 되었다.
 *
 * 아래 예제는 suspend()와 resume(), stop()의 상태를 보여 주는 예이다.
 * sleep(2000)은 쓰레드를 2초간 멈추게 하지만, 2초 후에 바로 실행상태가 아닌 실행대기상태가 된다.
 *
 * 이 예제는 간단하기 때문에 교착상태가 일어날 일이 없으므로 suspend()와 stop()을 사용해도 아무런 문제가 없지만,
 * 좀 더 복잡한 경우에는 사용하지 않는 것이 좋다.
 */
public class SuspendResumeStopEx1 {
    public static void main(String[] args) {
        RunImplEx1 r = new RunImplEx1();
        Thread th1 = new Thread(r, "*");
        Thread th2 = new Thread(r, "**");
        Thread th3 = new Thread(r, "***");

        th1.start();
        th2.start();
        th3.start();

        try {
            Thread.sleep(2000);
            th1.suspend(); // 쓰레드 th1을 잠시 중단시킨다.
            Thread.sleep(2000);
            th2.suspend();
            Thread.sleep(3000);
            th1.resume(); // 쓰레드 th1이 다시 동작하도록 한다.
            Thread.sleep(3000);
            th1.stop(); // 쓰레드 th1을 강제종료 시킨다.
            th2.stop();
            Thread.sleep(2000);
            th3.stop();
        } catch (InterruptedException e) {}
    } // main
}

class RunImplEx1 implements Runnable {
    @Override
    public void run() {
        while (true) {
            System.out.println(Thread.currentThread().getName());
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {}
        }
    } // run()
}
