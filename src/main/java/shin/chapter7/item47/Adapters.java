package shin.chapter7.item47;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static org.assertj.core.api.Assertions.assertThat;

public class Adapters {
    // Stream -> Iterable 로 중개해주는 어댑터
    public static <E> Iterable<E> iterableOf(Stream<E> stream) {
        return stream::iterator;
    }

    // Iterable -> Stream 로 중개해주는 어댑터
    public static <E> Stream<E> streamOf(Iterable<E> iterable) {
        return StreamSupport.stream(iterable.spliterator(), false);
    }

    @Test
    void foreach_iterable() {
        List<Integer> numbers = Arrays.asList(1, 2, 3);

        for (int number: numbers) {
            assertThat(number <= 3).isEqualTo(true);
        }
    }

    @Test
    void foreach_stream() {
        Stream<Integer> numberStream = Stream.of(1, 2, 3);

        // 형변환 코드 -> 난잡하다.
//        for (int number: (Iterable<Integer>) numberStream::iterator) {
//            assertThat(number <= 3).isEqualTo(true);
//        }

        for (int number: iterableOf(numberStream)) {
            assertThat(number <= 3).isEqualTo(true);
        }
    }

    @Test
    void foreach_stream_adaptor() {
        // given
        List<Integer> numbers = Arrays.asList(1, 2, 3);

        // when
        Stream<Integer> numberStream = streamOf(numbers);

        // then
        numberStream.forEach(number -> assertThat(number <= 3).isTrue());
    }
}
