package shin.chapter11.javaconcept.ex1_singlemulti;

public class MultiThreadEx {
    static long startTime = 0;
    public static void main(String[] args) {
        MultiThread th1 = new MultiThread();
        th1.start();
        startTime = System.currentTimeMillis();

        for (int i = 0; i < 300; i++) {
            System.out.printf("%s", new String("-"));
        }
        System.out.print("소요시간1:" + (System.currentTimeMillis() - MultiThreadEx.startTime));
    }
}

class MultiThread extends Thread {
    @Override
    public void run() {
        for (int i = 0; i < 300; i++) {
            System.out.printf("%s", new String("|"));
        }

        System.out.print("소요시간2:" + (System.currentTimeMillis() - MultiThreadEx.startTime));
    }
}
