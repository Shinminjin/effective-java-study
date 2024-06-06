package shin.chapter11.javaconcept.ex5_control;

import javax.swing.*;

public class InterruptEx2 {
    public static void main(String[] args) {
        InterruptThreadEx2 thread = new InterruptThreadEx2();
        thread.start();

        String input = JOptionPane.showInputDialog("아무 값이나 입력하세요.");
        System.out.println("입력하신 값은 " + input + "입니다.");
        thread.interrupt();
        System.out.println("isInterrupted():" + thread.isInterrupted());
    }
}

/**
 * InterruptEx1에서 시간지연을 사용했던 for문 대신 Thread.sleep(1000)으로 1초동안 지연되도록 변경했음
 * 카운트가 종료되지 않았다. 또, isInterrupted()의 결과 역시 false다. 왜 그럴까?
 *
 * 그 이유는 Thread.sleep(1000)에서 InterruptedException이 발생했기 때문이다.
 * sleep()에 의해 쓰레드가 잠시 멈춰있을 때, interrupt()를 호출하면 InterruptedException이 발생되고
 * 쓰레드의 interrupted 상태는 false로 자동 초기화된다.
 */
class InterruptThreadEx2 extends Thread {
    public void run() {
        int i = 10;

        while (i != 0 && !isInterrupted()) {
            System.out.println(i--);
            try {
                Thread.sleep(1000); // 1초 지연
            } catch (InterruptedException e) {}
        }
        System.out.println("카운트가 종료되었습니다.");
    }
}

/**
 * 아래와 같이 catch 블록에 interrupt()를 추가로 넣어서 쓰레드의 interrupted 상태를 true로 다시 바꿔줘야한다.
 */
class InterruptEx2Answer extends Thread {
    public void run() {
        int i = 10;

        while (i != 0 && !isInterrupted()) {
            System.out.println(i--);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                interrupt(); // 추가
            }
        }
    }
}
