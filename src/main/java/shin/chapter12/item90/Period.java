package shin.chapter12.item90;

import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.Date;

public final class Period implements Serializable {
    private final Date start;
    private final Date end;

    public Period(Date start, Date end) {
        this.start = new Date(start.getTime());
        this.end = new Date(end.getTime());

        if (this.start.compareTo(this.end) > 0) {
            throw new IllegalArgumentException(
                    start + " after " + end);
        }
    }

    public Date start() {
        return new Date(start.getTime());
    }

    public Date end() {
        return new Date(end.getTime());
    }

    public String toString() {
        return start + " - " + end;
    }

    // 직렬화 프록시 패턴용 writeReplace 메서드
    // 자바의 직렬화 시스템이 바깥 클래스의 인스턴스 말고 SerializationProxy의 인스턴스를 반환하게 하는 역할
    // 이 메서드 덕분에 직렬화 시스템은 바깥 클래스의 직렬화된 인스턴스를 생성해낼 수 없다.
    // "프록시야 너가 대신 직렬화되라"
    private Object writeReplace() {
        return new SerializationProxy(this);
    }

    // 직렬화 프록시 패턴용 readObject 메서드
    // 불변식을 훼손하고자 하는 시도를 막을 수 있는 메서드
    // "Period 인스턴스로 역직렬화를 하려고해? 안돼"
    private void readObject(ObjectInputStream stream)
            throws InvalidObjectException {
        throw new InvalidObjectException("프록시가 필요합니다");
    }

    // 바깥 클래스의 논리적 상태를 정밀하게 표현하는 중첩 클래스(Period 클래스의 직렬화 프록시)
    private static class SerializationProxy implements Serializable {
        private static final long serialVersionUID = 234098243823485285L; // Any number will do (Item 87)

        private final Date start;
        private final Date end;

        // 생성자는 단 하나여야 하고, 바깥 클래스의 인스턴스를 매개변수로 받고 데이터를 복사해야 함
        SerializationProxy(Period p) {
            this.start = p.start;
            this.end = p.end;
        }

        // 역직렬화시 직렬화 시스템이 직렬화 프록시를 다시 바깥 클래스의 인스턴스로 변환하게 해줌.
        // 역직렬화는 불변식을 깨뜨릴 수 있다는 불안함이 있는데,
        // 이 메서드가 불변식을 깨뜨릴 위험이 적은 정상적인 방법(생성자, 정적 팩터리, 다른 메서드를 사용)으로 역직렬화된 인스턴스를 얻게 한다.
        // "역직렬화 하려면 이거로 대신해"
        private Object readResolve() {
            return new Period(start, end);    // public 생성자를 사용한다.
        }
    }
}
