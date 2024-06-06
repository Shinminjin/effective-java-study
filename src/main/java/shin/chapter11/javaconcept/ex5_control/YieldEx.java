package shin.chapter11.javaconcept.ex5_control;

/**
 * yield()는 쓰레드 자신에게 주어진 실행시간을 다음 차례의 쓰레드에게 양보(yield)한다.
 * 예를 들어 스케쥴러에 의해 1초의 실행시간을 할당받은 쓰레드가 0.5초의 시간동안 작업한 상태에서 yield()가 호출되면,
 * 나머지 0.5초는 포기하고 다시 실행대기상태가 된다.
 *
 * yield()와 interrupt()를 적절히 사용하면, 프로그램의 응답성을 높이고 보다 효율적인 실행이 가능하게 할 수 있다.
 * 이 메서드들이 실제로 어떻게 사용되는지 아래의 예제를 직접 실행해보자.
 */
public class YieldEx {
    public static void main(String[] args) {
        YieldExThread th1 = new YieldExThread("*");
        YieldExThread th2 = new YieldExThread("**");
        YieldExThread th3 = new YieldExThread("***");

        th1.start();
        th2.start();
        th3.start();

        try {
            Thread.sleep(2000);
            th1.suspend();
            Thread.sleep(2000);
            th2.suspend();
            Thread.sleep(3000);
            th1.resume();
            Thread.sleep(3000);
            th1.stop();
            th2.stop();
            Thread.sleep(2000);
            th3.stop();
        } catch (InterruptedException e) {}
    }
}

/**
 * if문에 yield()를 호출하는 else 블록이 추가되었다.
 *
 */
class YieldExThread implements Runnable {
    boolean suspended = false;
    boolean stopped = false;

    Thread th;

    YieldExThread(String name) {
        th = new Thread(this, name); // Thread(Runnable r, String msg)
    }

    @Override
    public void run() {
        String name = th.getName();

        /**
         * 만약 else문이 없었다면,
         * suspended 값이 true일 때,
         * 주어진 실행시간을 그저 while문을 의미없이 돌면서 낭비할 것이다. - 바쁜 대기상태(busy-waiting)
         *
         * 그러나 else문에 yield()를 호출해서 남은 실행시간을 while문에서 낭비하지 않고
         * 다른 쓰레드에게 양보하게 되므로 더 효율적이다.
         */
        while (!stopped) {
            if (!suspended) {
                System.out.println(name);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    System.out.println(name + " - interrupted");
                }
            } else {
                Thread.yield();
            }
        }
        System.out.println(name + " - stopped");
    }

    /**
     * suspend()와 stop() 메서드에 interrupt()를 호출하는 코드를 추가했다.
     * 만일 stop()이 호출되었을 때 Thread.sleep(1000)에 의해 쓰레드가 일시정지 상태에 머물러 있는 상황이라면,
     * stopped의 값이 true로 바뀌었어도 쓰레드가 정지될 때까지 최대 1초의 시간지연이 생긴다.
     *
     * 그러나 같은 상황에서 interrupt()를 호출하면, sleep()에서 InterruptedException이 발생하여
     * 즉시 일시정지 상태에서 벗어나게 되므로 응답성이 좋아진다.
     */
    public void suspend() {
        suspended = true;
        th.interrupt();
        System.out.println(th.getName() + " - interrupt() by suspend()");
    }

    public void stop() {
        stopped = true;
        th.interrupt();
        System.out.println(th.getName() + " - interrupt() by stop()");
    }

    public void resume() {
        suspended = false;
    }

    public void start() {
        th.start();
    }
}
