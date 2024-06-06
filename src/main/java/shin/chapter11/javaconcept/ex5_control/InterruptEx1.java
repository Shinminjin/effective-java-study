package shin.chapter11.javaconcept.ex5_control;

import javax.swing.*;

/**
 * 진행 중인 쓰레드의 작업이 끝나기 전에 취소시켜야할 때가 있다.
 * 예를 들어 큰 파일을 다운로드받을 때 시간이 너무 오래 걸리면 중간에 다운로드를 포기하고 취소할 수 있어야 한다.
 *
 * interrupt()는 쓰레드에게 작업을 멈추라고 요청한다.
 * 단지 멈추라고 요청만 하는 것일 뿐, 쓰레드를 강제로 종료시키지는 못한다.
 *
 * interrupt()는 그저 쓰레드의 interrupted 상태(인스턴스 변수)를 바꾸는 것일 뿐이다.
 * 그리고 interrupted()는 쓰레드에 대해 interrupt()가 호출되었는지 알려준다.
 * interrupt()가 호출되지 않았다면 false를, 호출되었다면 true를 반환한다.
 *
 * void interrupt() : 쓰레드의 interrupted 상태를 false에서 true로 변경
 * boolean isInterrupted() : 쓰레드의 interrupted 상태를 반환
 * static boolean interrupted() : 현재 쓰레드의 interrupted 상태를 반환 후, false로 변경
 * 
 * 쓰레드가 sleep(), wait(), join()에 의해 '일시정지 상태(WAITING)'에 있을 때,
 * 해당 쓰레드에 대해 interrupt()를 호출하면,
 * sleep(), wait(), join()에서 InterruptedException이 발생하고
 * 쓰레드는 '실행대기 상태(RUNNABLE)'로 바뀐다.
 * 즉, 멈춰있던 쓰레드를 깨워서 실행가능한 상태로 만드는 것이다.
 *
 * 아래 예제는 카운트다운 도중에 사용자의 입력이 들어오면 카운트다운을 종료한다.
 */
public class InterruptEx1 {
    public static void main(String[] args) throws Exception {
        InterruptThread1 thread = new InterruptThread1();
        thread.start();

        String input = JOptionPane.showInputDialog("아무 값이나 입력하세요.");
        System.out.println("입력하신 값은 " + input + "입니다.");
        thread.interrupt(); // interrupt()를 호출하면, interrupted 상태가 true가 된다.
        System.out.println("isInterrupted():" + thread.isInterrupted()); // true
    }
}

class InterruptThread1 extends Thread {
    public void run() {
        int i = 10;

        while (i != 0 && !isInterrupted()) {
            System.out.println(i--);
            for (long x = 0; x < 2500000000L; x++); // 시간 지연
        }
        System.out.println("카운트가 종료되었습니다.");
    }
}
