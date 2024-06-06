package shin.chapter11.javaconcept.ex7_waitnotify;

import java.util.ArrayList;
import java.util.List;

/**
 * synchronized로 동기화해서 공유 데이터를 보호하는 것까지는 좋은데,
 * 특정 쓰레드가 객체의 락을 가진 상태로 오랜 시간을 보내지 않도록 하는 것도 중요하다.
 * <p>
 * 만일 계좌에 출금할 돈이 부족해서 한 쓰레드가 락을 보유한 채로 돈이 입금될 때까지 오랜 시간을 보낸다면,
 * 다른 쓰레드들은 모두 해당 객체의 락을 기다리느라 다른 작업들도 원활히 진행되지 않을 것이다.
 * <p>
 * 이러한 상황을 개선하기 위해 고안된 것이 바로 wait()과 notify()이다.
 * <p>
 * 동기화된 임계 영역의 코드를 수행하다가 작업을 더 이상 진행할 상황이 아니면,
 * 일단 wait()을 호출하여 쓰레드가 락을 반납하고 기다리게 한다.
 * <p>
 * 그러면 다른 쓰레드가 락을 얻어 해당 객체에 대한 작업을 수행할 수 있게 된다.
 * 나중에 작업을 진행할 수 있는 상황이 되면 notify()를 호출해서,
 * 작업을 중단했던 쓰레드가 다시 락을 얻어 작업을 진행할 수 있게 한다.
 * <p>
 * 이는 마치 빵을 사려고 빵집 앞에 줄을 서있는 것과 유사한데,
 * 자신의 차례가 되었는데도 자신이 원하는 빵이 나오지 않았으면,
 * 다음 사람에게 순서를 양보하고 기다리다가 자신이 원하는 빵이 나오면 통보를 받고 빵을 사가는 것이다.
 * <p>
 * 차이가 있따면, 오래 기다린 쓰레드가 락을 얻는다는 보장이 없다는 것이다.
 * wait()이 호출되면, 실행 중이던 쓰레드는 해당 객체의 대기실(waiting pool)에서 통지를 기다린다.
 * <p>
 * notify()가 호출되면, 해당 객체의 대기실에 있던 모든 쓰레드 중에서 임의의 쓰레드만 통지를 받는다.
 * notifyAll()은 기다리고 있는 모든 쓰레드에게 통보를 하지만,
 * 그래도 lock을 얻을 수 있는 것은 단 하나의 쓰레드일 뿐이고 나머지 쓰레드는 통보를 받긴 했지만,
 * lock을 얻지 못하면 다시 lock을 기다리는 신세가 된다.
 * <p>
 * wait()은 notify() 또는 notifyAll()이 호출될 때까지 기다리지만,
 * 매개변수가 있는 wait()은 지정된 시간동안만 기다린다.
 * 즉, 지정된 시간이 지난 후에 자동적으로 notify()가 호출되는 것과 같다.
 * <p>
 * 그리고 waiting pool은 객체마다 존재하는 것이므로 notifyAll()이 호출된다고 해서
 * 모든 객체의 waiting pool에 있는 쓰레드가 깨워지는 것은 아니다.
 * notifyAll()이 호출된 객체의 waiting pool에 대기 중인 쓰레드만 해당된다는 것을 기억하자.
 */
public class ThreadWaitEx {
    /**
     * 식당에서 음식(Dish)을 만들어서 테이블(Table)에 추가(add)하는 요리사(Cook)와
     * 테이블의 음식을 소비(remove)하는 손님(Customer)을 쓰레드로 구현했다.
     *
     * 발생할 수 있는 예외 - ConcurrentModificationException, IndexOutOfBoundsException
     *
     * ConcurrentModificationException
     * 요리사(Cook) 쓰레드가 테이블에 음식을 놓는 도중에,
     * 손님(Customer) 쓰레드가 음식을 가져가려 했기 때문에 발생하는 예외이다.
     *
     * IndexOutOfBoundsException
     * 손님 쓰레드가 마지막 남은 음식을 테이블에서 제거하려했기 때문에 발생하는 예외이다.
     *
     * 이런 예외들이 발생하는 이유는 여러 쓰레드가 테이블을 공유하는데도 동기화를 하지 않았기 떄문이다.
     * 이제 이 예제에 동기화를 추가해서 예외가 발생하지 않도록 해보자. (ThreadWaitEx2)
     */
    public static void main(String[] args) throws Exception{
        Table table = new Table(); // 여러 쓰레드가 공유하는 객체

        new Thread(new Cook(table), "COOK1").start();
        new Thread(new Customer(table, "donut"), "CUST1").start();
        new Thread(new Customer(table, "burger"), "CUST2").start();

//        Thread.sleep(100); // 0.1초(100 밀리 세컨트) 후에 강제 종료시킨다.
        Thread.sleep(5000);
        System.exit(0); // 프로그램 전체를 종료. (모든 쓰레드가 종료됨)
    }
}

class Customer implements Runnable {
    private Table table;
    private String food;

    Customer(Table table, String food) {
        this.table = table;
        this.food = food;
    }

    @Override
    public void run() {
        while (true) {
            try { Thread.sleep(10); } catch (InterruptedException e) {}
            String name = Thread.currentThread().getName();

            if (eatFood())
                System.out.println(name + " ate a " + food);
            else
                System.out.println(name + " failed to eat. :(");
        } // while
    }

    boolean eatFood() {
//        return table.remove(food);
        return table.removeSynchronized(food);
    }
}

class Cook implements Runnable {
    private Table table;

    Cook(Table table) {
        this.table = table;
    }

    @Override
    public void run() {
        while (true) {
            // 임의의 요리를 하나 선택해서 table에 추가한다.
            int idx = (int) (Math.random()*table.dishNum());
            table.add(table.dishNames[idx]);

//            try { Thread.sleep(1); } catch (InterruptedException e) {}
            try { Thread.sleep(100); } catch (InterruptedException e) {}
        } // while
    }
}

class Table {
    String[] dishNames = {"donut", "donut", "burger"}; // donut이 더 자주 나온다.
    final int MAX_FOOD = 6; // 테이블에 놓을 수 있는 최대 음식의 개수

    private List<String> dishes = new ArrayList<>();

    public void add(String dish) {
        // 테이블에 음식이 가득찼으면, 테이블에 음식을 추가하지 않는다.
        if (dishes.size() >= MAX_FOOD) {
            return;
        }
        dishes.add(dish);
        System.out.println("Dishes:" + dishes.toString());
    }

    public boolean remove(String dishName) {
        // 지정된 요리와 일치하는 요리를 테이블에서 제거한다.
        for (int i = 0; i < dishes.size(); i++) {
            if (dishName.equals(dishes.get(i))) {
                dishes.remove(i);
                return true;
            }
        }
        return false;
    }

    public boolean removeSynchronized(String dishName) {
        synchronized (this) {
            while (dishes.size() == 0) {
                String name = Thread.currentThread().getName();
                System.out.println(name + " is waiting.");
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {}
            }
            for (int i = 0; i < dishes.size(); i++) {
                if (dishName.equals(dishes.get(i))) {
                    dishes.remove(i);
                    return true;
                }
            }
        } // synchronized
        return false;
    }

    public int dishNum() {
        return dishNames.length;
    }
}
