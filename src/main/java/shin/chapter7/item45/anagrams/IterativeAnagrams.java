package shin.chapter7.item45.anagrams;

import org.junit.jupiter.api.Test;

import java.util.*;

public class IterativeAnagrams {

    @Test
    void anagramTest() {
        List<String> words = new ArrayList<>();
        words.add("stop");
        words.add("spot");
        words.add("post");
        words.add("ball");
        words.add("cake");
        words.add("desk");

        Map<String, Set<String>> groups = new HashMap<>();

        // 그룹핑하기
        for (String word : words) {
            groups.computeIfAbsent(alphabetize(word), (unused) -> new TreeSet<>()).add(word);
        }

        // 필터링하기
        for (Set<String> group : groups.values()) {
            if(group.size() >= 2) {
                System.out.println(group.size() + ": " + group);
            }
        }
    }

    private String alphabetize(String s) {
        char[] a = s.toCharArray();
        Arrays.sort(a);
        return new String(a);
    }
}
