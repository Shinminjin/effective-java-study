package shin.chapter11.javaconcept.ex7_waitnotify;

import java.util.ArrayList;
import java.util.List;

/**
 * wait()과 notify() 추가.
 * 테이블에 음식이 없을 때뿐만 아니라, 원하는 음식이 없을 때도 손님이 기다리도록 바꾸었다.
 *
 * 근데 아래의 코드에도 문제가 있다.
 * 테이블 객체의 waiting pool에 요리사 쓰레드와 손님 쓰레드가 같이 기다린다는 것이다.
 *
 * 그래서 notify()가 호출되었을 때, 요리사 쓰레드와 손님 쓰레드 중에서 누가 통지 받을지 알 수 없다.
 * 만일 테이블의 음식이 줄어들어서 notify()가 호출되었다면, 요리사 쓰레드가 통지를 받아야 한다.
 *
 * 그러나 notify()는 그저 waiting pool에서 대기 중인 쓰레드 중에서 하나를 임의로 선택해서 동지할 뿐,
 * 요리사 쓰레드를 선택해서 통지할 수 없다.
 * 운 좋게 요리사 쓰레드가 통지를 받으면 다행인데, 손님 쓰레드가 통지를 받으면 lock을 얻어도 여전히
 * 자신이 원하는 음식이 없어서 다시 waiting pool에 들어가게 된다.
 */
public class ThreadWaitNotifyEx {
    public static void main(String[] args) throws Exception{
        Table2 table = new Table2(); // 여러 쓰레드가 공유하는 객체

        new Thread(new Cook2(table), "COOK1").start();
        new Thread(new Customer2(table, "donut"), "CUST1").start();
        new Thread(new Customer2(table, "burger"), "CUST2").start();

        Thread.sleep(2000);
        System.exit(0); // 프로그램 전체를 종료. (모든 쓰레드가 종료됨)
    }
}

class Customer2 implements Runnable {
    private Table2 table;
    private String food;

    Customer2(Table2 table, String food) {
        this.table = table;
        this.food = food;
    }

    @Override
    public void run() {
        while (true) {
            try { Thread.sleep(100); } catch (InterruptedException e) {}
            String name = Thread.currentThread().getName();

            table.remove(food);
            System.out.println(name + " ate a " + food);
        } // while
    }

}

class Cook2 implements Runnable {
    private Table2 table;

    Cook2(Table2 table) {
        this.table = table;
    }

    @Override
    public void run() {
        while (true) {
            // 임의의 요리를 하나 선택해서 table에 추가한다.
            int idx = (int) (Math.random()*table.dishNum());
            table.add(table.dishNames[idx]);
            try { Thread.sleep(10); } catch (InterruptedException e) {}
        } // while
    }
}

/**
 * 지독히 운이 나쁘면 요리사 쓰레드는 계속 통지를 받지 못하고 오랫동안 기다리게 되는데
 * 이것을 기아 현상이라고 한다. 이 현상을 막으려면, notify() 대신 notifyAll()을 사용해야 한다.
 * 일단 모든 쓰레드에게 통지를 하면,
 * 손님 쓰레드는 다시 waiting pool에 들어가더라도 요리사 쓰레드는 결국 lock을 얻어서 작업을 진행할 수 있기 때문이다.
 *
 * notifyAll()로 요리사 쓰레드의 기아 현상은 막았지만,
 * 손님 쓰레드까지 통지를 받아서 불필요하게 요리사 쓰레드와 lock을 얻기 위해 경쟁하게 된다.
 * 이처럼 여러 쓰레드가 lock을 얻기 위해 서로 경쟁하는 것을 경쟁 상태라고 하는데,
 * 이 경쟁 상태를 개선하기 위해서는 요리사 쓰레드와 손님 쓰레드를 구별해서 통지하는 것이 필요하다.
 *
 * ex8의 lock과 condition을 이용하면, wait() & notify()로는 불가능한 선별적인 통지가 가능하다.
 */
class Table2 {
    String[] dishNames = {"donut", "donut", "burger"};
    final int MAX_FOOD = 6;
    private List<String> dishes = new ArrayList<>();

    public synchronized void add(String dish) {
        while (dishes.size() >= MAX_FOOD) {
            String name = Thread.currentThread().getName();
            System.out.println(name + " is waiting.");
            try {
                wait();
                Thread.sleep(500);
            } catch (InterruptedException e) {}
        }
        dishes.add(dish);
        notify();
        System.out.println("Dishes:" + dishes.toString());
    }

    public void remove(String dishName) {
        synchronized (this) {
            String name = Thread.currentThread().getName();

            while (dishes.size() == 0) {
                System.out.println(name + " is waiting.");
                try {
                    wait(); // CUST2 쓰레드를 기다리게 한다.
                    Thread.sleep(500);
                } catch (InterruptedException e) {}
            }

            while (true) {
                for (int i = 0; i < dishes.size(); i++) {
                    if (dishName.equals(dishes.get(i))) {
                        dishes.remove(i);
                        notify(); // 잠자고 있는 COOK을 깨우기 위함.
                        return;
                    }
                } // for문의 끝
                try {
                    System.out.println(name + " is waiting.");
                    wait(); // 원하는 음식이 없는 CUST 쓰레드를 기다리게 한다.
                    Thread.sleep(500);
                } catch (InterruptedException e) {}
            } // synchronized
        }
    }

    public int dishNum() {
        return dishNames.length;
    }
}