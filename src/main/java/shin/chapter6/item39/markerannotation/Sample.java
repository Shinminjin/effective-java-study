package shin.chapter6.item39.markerannotation;

// 코드 39-2 마커 애너테이션을 사용한 프로그램 예 (239쪽)
public class Sample {
    @Test public static void m1() { } // 성공

    @Test public static void m2() {
        throw new RuntimeException("실패");
    }

    @Test public void m3() { }   // 잘못 사용한 예: 정적 메서드가 아니다.

    public static void m4() { } // 테스트가 아니다.
}
