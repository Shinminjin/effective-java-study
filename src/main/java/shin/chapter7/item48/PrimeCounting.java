package shin.chapter7.item48;


import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.util.stream.LongStream;

public class PrimeCounting {
    // 소수 계산 스트림 파이프라인
    static long pi(long n) {
        return LongStream.rangeClosed(2, n)
                .mapToObj(BigInteger::valueOf)
                .filter(i -> i.isProbablePrime(50))
                .count();
    }

    // 병렬화 버전
    static long parallelPi(long n) {
        return LongStream.rangeClosed(2, n)
                .parallel()
                .mapToObj(BigInteger::valueOf)
                .filter(i -> i.isProbablePrime(50))
                .count();
    }

    @Test
    void primeCountingTest() {
        System.out.println(pi(10_000_000));
    }

    @Test
    void parallelPrimeCountingTest() {
        System.out.println(parallelPi(10_000_000));
    }

}
