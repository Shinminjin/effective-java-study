package shin.chapter11.javaconcept.ex6_synchronization;

/**
 * 멀티쓰레드 프로세스의 경우
 * 여러 쓰레드가 같은 프로세스 내의 자원을 공유해서 작업하기 떄문에 서로의 작업에 영향을 주게된다.
 * <br/>
 * <br/>
 * 만일 쓰레드 A가 작업하던 도중에 다른 쓰레드 B에게 제어권이 넘어갔을 때,
 * 쓰레드 A가 작업하던 공유데이터를 쓰레드 B가 임의로 변경한다면,
 * 다시 쓰레드 A가 제어권을 받아서 나머지 작업을 마쳤을 때 원래 의도했던 것과는 다른 결과를 얻을 수 있다.
 * <br/>
 * <br/>
 * 이러한 일이 발생하는 것을 방지하기 위해서
 * 한 쓰레드가 특정 작업을 끝마치기 전까지
 * 다른 쓰레드에 의해 방해받지 않도록 하는 것이 필요하다.
 * 그래서 도입된 개념이 바로 '임계 영역(critical section)'과 '잠금(락, lock)'이다.
 * <br/>
 * <br/>
 * 공유 데이터를 사용하는 코드 영역을 임계 여역으로 지정해놓고,
 * 공유 데이터(객체)가 가지고 있는 lock을 획득한 단 하나의 쓰레드만 이 영역 내의 코드를 수행할 수 있게 한다.
 * 그리고 해당 쓰레드가 임계 영역 내의 모든 코드를 수행하고 벗어나서 lock을 반납해야만 다른 쓰레드가 반납된 lock을 획득하여
 * 임계 영역의 코드를 수행할 수 있게 된다.
 * <br/>
 * <br/>
 * 이처럼 한 쓰레드가 진행 중인 작업을 다른 쓰레드가 간섭하지 못하도록 막는 것을
 * '쓰레드의 동기화(synchronization)'라고 한다.
 * <br/>
 * <br/>
 * 가장 간단한 동기화 방법인 synchronized 키워드를 이용한 동기화에 대해서 알아보자.
 * 이 키워드는 임계 영역을 설정하는데 사용된다.
 * <br/>
 * 1. 메서드 전체를 임계 영역으로 지정
 * 메서드 전체가 임계영역으로 설정된다.
 * 쓰레드는 synchronized 메서드가 호출된 시점부터
 * 해당 메서드가 포함3된 객체의 lock을 얻어 작업을 수행하다가 메서드가 종료되면 lock을 반환한다.
 * public synchronized void calcSum() {
 *     //...
 * }
 * 2. 특정한 영역을 임계 영역으로 지정
 * 메서드 내의 코드 일부를 블럭{}으로 감싸고 블럭 앞에 'synchronized(참조변수)'를 붙이는 것인데,
 * 이 때  참조 변수는 락을 걸고자하는 객체를 참조하는 것이어야 한다.
 * 이 블럭을 synchronized 블럭이라고 부르며,
 * 이 블럭의 영역안으로 들어가면서부터 쓰레드는 지정된 객체의 lock을 걸 얻게 되고, 이 블럭을 벗어나면 lock을 반납한다.
 * synchronized(객체의 참조변수) {
 *     //...
 * }
 * 두 방법 모두 lock의 획득과 반납이 모두 자동적으로 이루어지므로 우리가 해야 할 일은 그저 임계 영역만 설정해주는 것뿐이다.
 * 모든 객체는 lock을 하나씩 가지고 있으며, 해당 객채의 lock을 가지고 있는 쓰레드만 임계 영역의 코드를 수행할 수 있다.
 * 그리고 다른 쓰레드들은 lock을 얻을 때까지 기다리게 된다.
 * <br/>
 * <br/>
 * 임계 영역은 멀티쓰레드 프로그램의 성능을 좌우하기 때문에 가능하면 메서드 전체에 락을 거는 것보다
 * synchronized 블럭으로 임계 영역을 최소화해서 보다 효율적인 프로그램이 되도록 노력해야한다.
 */
public class SynchronizedEx {
    public static void main(String[] args) {
        Runnable r = new RunnableAccount();
        new Thread(r).start(); // ThreadGroup에 의해 참조되므로 gc 대상이 아니다.
        new Thread(r).start(); // ThreadGroup에 의해 참조되므로 gc 대상이 아니다.
    }
}

/**
 * 은행계좌에서 잔고를 확인하고 임의의 금액을 출금하려는 예제이다.
 * 아래의 코드를 보면 잔고가 출금하려는 금액보다 큰 경우에만 출금하도록 되어 있는 것을 알 수 있따.
 */
class Account {
    private int balance = 1000;

    public int getBalance() {
        return balance;
    }

    /**
     * 아래의 코드를 보면 잔고가 출금하려는 금액보다 큰 경우에만 출금하도록 되어있다.
     * 하지만 실행결과를 보면 잔고가 음수인 것을 볼 수 있다.
     * 그 이유는 한 쓰레드가 if문의 조건식을 통과하고 출금하기 바로 직전에 다른 쓰레드가 끼어들어서 출금을 먼저 했기 때문이다.
     *
     * 예를들어 한 쓰레드가 if문의 조건식을 계산했을 때는 잔고가 200이고 출금하려는 금액이 100이라서
     * 조건식(balance >= money)이 true가 되어 출금(balance -= money)을 수행하려는 순간 다른 쓰레드에게 제어권이 넘어가서
     * 다른 쓰레드가 200을 출금하여 잔고가 0이 되었다. 다시 이전의 쓰레드로 제어권이 넘어오면 if문 다음부터 수행하게 되므로
     * 잔고가 0인 상태에서 100을 출금하여 잔고가 -100이 된다.
     *
     * 그래서 잔고를 확인하는 if문과 출금하는 문장은 하나의 임계영역으로 묶여져야 한다.
     * 예제에서는 상황을 보여주기 위해
     * 일부러 Thread.sleep(1000)을 사용해서 if문을 통과하자마자
     * 다른 쓰레드에게 제어권을 넘기도록 하였지만,
     * 굳이 이렇게 하지 않더라도
     * 한 쓰레드의 작업이 다른 쓰레드에 의해서 영향을 받는 일이 발생할 수 있기 때문에
     * 동기화가 반드시 필요하다.
     */
    public void withdraw(int money) {
        if (balance >= money) {
            try { Thread.sleep(1000); } catch (InterruptedException e) {}
            balance -= money;
        }
    } // withdraw

    public synchronized void synchronizedMethodWithdraw(int money) {
        if (balance >= money) {
            try { Thread.sleep(1000); } catch (Exception e) {}
            balance -= money;
        }
    }

    public void synchronizedBlockWithdraw(int money) {
        synchronized (this) {
            if (balance >= money) {
                try { Thread.sleep(1000); } catch (Exception e) {}
                balance -= money;
            }
        }
    }
}


class RunnableAccount implements Runnable {
    Account acc = new Account();

    @Override
    public void run() {
        while (acc.getBalance() > 0) {
            // 100, 200, 300 중의 한 값을 임의로 선택해서 출금(withdraw)
            int money = (int) (Math.random() * 3 + 1) * 100;
            acc.withdraw(money);
//            acc.synchronizedMethodWithdraw(money);
//            acc.synchronizedBlockWithdraw(money);
            System.out.println("balance:" + acc.getBalance());
        }
    } // run()
}