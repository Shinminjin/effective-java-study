package shin.chapter6.item39.repeatableannotation;

import java.util.ArrayList;
import java.util.List;

// 반복 가능한 애너테이션을 사용한 프로그램 (244쪽)
public class Sample4 {
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

    // 반복 가능 애너테이션을 두 번 단 코드
    @ExceptionTest(IndexOutOfBoundsException.class)
    @ExceptionTest(NullPointerException.class)
    public static void doublyBad() { // 성공
        List<String> list = new ArrayList<>();

        // IndexOutOfBoundsException or NullPointerException
        list.addAll(5, null);
    }
}
