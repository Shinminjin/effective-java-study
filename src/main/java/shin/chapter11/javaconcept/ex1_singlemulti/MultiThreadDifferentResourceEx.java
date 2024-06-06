package shin.chapter11.javaconcept.ex1_singlemulti;

import javax.swing.*;

/**
 * 사용자로부터 입력받는 부분과 화면에 숫자를 출력하는 부분을 두 개의 쓰레드로 나누어서 처리했기 때문에
 * 사용자가 입력을 마치지 않았어도 화면에 숫자가 출력된다.
 */
public class MultiThreadDifferentResourceEx {
    public static void main(String[] args) throws Exception {
        // 화면 출력 쓰레드 분리
        MultiThreadDifferentResource th1 = new MultiThreadDifferentResource();
        th1.start();

        String input = JOptionPane.showInputDialog("아무 값이나 입력하세요.");
        System.out.println("입력하신 값은 " + input + "입니다.");

    }
}

class MultiThreadDifferentResource extends Thread {
    public void run() {
        for (int i = 10; i > 0; i--) {
            System.out.println(i);
            try {
                sleep(1000); // 1초간 시간 지연
            } catch (Exception e) {

            }
        }
    }
}
