package shin.chapter6.item39.annotationwitharrayparameter;

import java.util.ArrayList;
import java.util.List;

// 배열 매개변수를 받는 애너테이션을 사용하는 프로그램
public class Sample3 {
    // 이 변형은 원소 하나짜리 매개변수를 받는 애너테이션도 처리할 수 있다.
    @ExceptionTest(ArithmeticException.class)
    public static void m1() {  // 성공
        int i = 0;
        i = i / i;
    }
    @ExceptionTest(ArithmeticException.class)
    public static void m2() {  // 실패 (다른 예외 발생)
        int[] a = new int[0];
        int i = a[1];
    }
    @ExceptionTest(ArithmeticException.class)
    public static void m3() { }  // 실패 (예외가 발생하지 않음)

    // 배열 매개변수를 받는 애너테이션을 사용하는 코드
    @ExceptionTest({ IndexOutOfBoundsException.class,
                     NullPointerException.class })
    public static void doublyBad() {   // 성공해야 한다.
        List<String> list = new ArrayList<>();

        // IndexOutOfBoundsException or NullPointerException
        list.addAll(5, null);
    }
}
