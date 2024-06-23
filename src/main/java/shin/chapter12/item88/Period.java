package shin.chapter12.item88;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.Date;

public final class Period implements Serializable {
    private static final long serialVersionUID = 4647424730390249716L;
    private Date start;
    private Date end;

    /**
     * @param  start 시작 시각
     * @param  end 종료 시각; 시작 시각보다 뒤여야 한다.
     * @throws IllegalArgumentException 시작 시각이 종료 시각보다 늦을 때 발생한다.
     * @throws NullPointerException start나 end가 null이면 발생한다.
     */
    public Period(Date start, Date end) {
        this.start = new Date(start.getTime()); // 가변인 Date 클래스의 위험을 막기 위해 새로운 객체로 방어적 복사를 한다.
        this.end = new Date(end.getTime());

        if (this.start.compareTo(this.end) > 0) {
            throw new IllegalArgumentException(start + " after " + end);
        }
    }

    public Date start() { return new Date(start.getTime()); }

    public Date end() { return new Date(end.getTime()); }

    @Override
    public String toString() { return start + " - " + end; }
    // ... 나머지 코드는 생략

//    // 불변식을 만족하는지 유효성 검사
//    private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
//        s.defaultReadObject(); // 기본 직렬화 수행
//
//        // 불변식을 만족하는지 검사
//        if(start.compareTo(end) > 0) { // 유효성 검사
//            throw new InvalidObjectException(start + "가 " + end + "보다 늦다.");
//        }
//    }

    // private 가변요소를 방어적 복사한다.
//    private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
//        s.defaultReadObject();
//
//        // 방어적 복사를 통해 인스턴스의 필드값 초기화
//        start = new Date(start.getTime());
//        end = new Date(end.getTime());
//
//        // 유효성 검사
//        if (start.compareTo(end) > 0)
//            throw new InvalidObjectException(start +" after "+ end);
//    }
}
