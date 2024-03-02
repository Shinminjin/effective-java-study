package shin.chapter7.item45.anagrams;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class StreamAnagrams {

    @Test
    void anagramTest() {
        List<String> words = new ArrayList<>();
        words.add("stop");
        words.add("spot");
        words.add("post");
        words.add("ball");
        words.add("cake");
        words.add("desk");

        words.stream().collect(
                        Collectors.groupingBy(
                                word -> word.chars().sorted()
                                        .collect(StringBuilder::new,
                                                (sb, c) -> sb.append((char) c),
                                                StringBuilder::append).toString()))
                .values().stream()
                .filter(group -> group.size() >= 2)
                .map(group -> group.size() + ": " + group)
                .forEach(System.out::println);
    }
}
