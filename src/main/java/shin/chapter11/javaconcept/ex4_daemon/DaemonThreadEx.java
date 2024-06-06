package shin.chapter11.javaconcept.ex4_daemon;

public class DaemonThreadEx implements Runnable {
    static boolean autoSave = false;

    public static void main(String[] args) {
        Thread t = new Thread(new DaemonThreadEx());
        t.setDaemon(true); // 이 부분이 없으면 종료되지 않는다.
        t.start();

        for (int i = 1; i <= 10; i++) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {}
            System.out.println(i);

            if (i==5)
                autoSave = true;
        }

        System.out.println("프로그램을 종료합니다.");
    }

    // 3초마다 변수 autoSave의 값을 확인해서 그 값이 true면, autoSave()를 호출하는 일을 무한히 반복하도록 쓰레드를 작성하였다.
    // 만일 이 쓰레드를 데몬 쓰레드로 설정하지 않았다면, 이 프로그램은 강제종료하지 않는 한 영원히 종료되지 않을 것이다.
    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep(3 * 1000); // 3초마다
            } catch (InterruptedException e) {}

            // autoSave의 값이 true이면 autoSave()를 호출한다.
            if (autoSave) {
                autoSave();
            }
        }
    }

    public void autoSave() {
        System.out.println("작업파일이 자동저장되었습니다.");
    }
}

