package shin.chapter11.javaconcept.ex4_daemon;

import java.util.Iterator;
import java.util.Map;

class DaemonThreadEx2 {
    public static void main(String[] args) throws Exception {
        A t1 = new A("Thread1");
        B t2 = new B("Thread2");
        t1.start();
        t2.start();
    }
}

class A extends Thread {
    A(String name) {
        super(name);
    }

    @Override
    public void run() {
        try {
            sleep(5 * 1000); // 5초동안 기다린다.
        } catch (InterruptedException e) {}
    }
}

class B extends Thread {
    B(String name) {
        super(name);
    }

    @Override
    public void run() {
        Map map = getAllStackTraces();
        Iterator it = map.keySet().iterator();

        int x = 0;
        while (it.hasNext()) {
            Object obj = it.next();
            Thread t = (Thread) obj;
            StackTraceElement[] ste = (StackTraceElement[]) (map.get(obj));

            System.out.println("[" + ++x + "] name : " + t.getName()
                    + ", group : " + t.getThreadGroup().getName()
                    + ", daemon : " + t.isDaemon());

            for (int i = 0; i < ste.length; i++) {
                System.out.println(ste[i]);
            }

            System.out.println();
        }

    }
}