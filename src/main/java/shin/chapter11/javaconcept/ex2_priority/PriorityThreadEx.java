package shin.chapter11.javaconcept.ex2_priority;

/**
 * 쓰레드는 우선순위(priority)라는 속성(멤버변수)을 가지고 있는데,
 * 이 우선순위의 값에 따라 쓰레드가 얻는 실행시간이 달라진다.
 *
 * 쓰레드가 수행하는 작업의 중요도에 따라 쓰레드의 우선순위를 서로 다르게 지정하여
 * 특정 쓰레드가 더 많은 작업시간을 갖도록 할 수 있다.
 *
 * ex. 파일 전송기능이 있는 메신저의 경우, 파일 다운로드를 처리하는 쓰레드보다
 * 채팅 내용을 전송하는 쓰레드의 우선순위가 더 높아야 사용자가 채팅하는데 불편함이 없을 것이다.
 *
 * 쓰레드가 가질 수 있는 우선순위의 범위는 1 ~ 10이며 숫자가 높을수록 우선순위가 높다.
 * 쓰레드의 우선순위는 쓰레드를 생성한 쓰레드로부터 상속받는다.
 * - main 메서드를 수행하는 쓰레드는 우선순위가 5이므로 main메서드 내에서 생성하는 쓰레드의 우선순위는 자동적으로 5가 된다.
 *
 * but, 내 컴퓨터(멀티코어)에서는 쓰레드의 우선순위에 따른 차이가 거의 없었다.
 * 그저 쓰레드에 높은 우선순위를 주면 더 많은 실행시간과 실행기회를 갖게 될 것이라고 기대할 수는 없는 것이다.
 * - 굳이 우선순위에 차등을 두어 쓰레드를 실행하려면, 특정 OS의 스케줄링 정책과 JVM 구현을 직접 확인해봐야한다.
 * - 차라리 작업에 우선순위를 두어 PriorityQueue에 저장해놓고, 우선순위가 높은 작업이 먼저 처리되도록 하는게 나을 수 있다.
 */
public class PriorityThreadEx {
    public static void main(String[] args) {
        PriorityThread1 th1 = new PriorityThread1();
        PriorityThread2 th2 = new PriorityThread2();

        th2.setPriority(7);

        System.out.println("Priority of th1(-) : " + th1.getPriority());
        System.out.println("Priority of th2(|) : " + th2.getPriority());

        th1.start();
        th2.start();
    }
}

class PriorityThread1 extends Thread {
    public void run() {
        for (int i = 0; i < 300; i++) {
            System.out.print("-");
            for (int x = 0; x < 1000000000; x++) ;
        }
    }
}

class PriorityThread2 extends Thread {
    public void run() {
        for (int i = 0; i < 300; i++) {
            System.out.print("|");
            for (int x = 0; x < 1000000000; x++) ;
        }
    }
}
