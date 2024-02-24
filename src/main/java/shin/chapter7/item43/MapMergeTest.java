package shin.chapter7.item43;

import java.util.HashMap;
import java.util.Map;

public class MapMergeTest {
    public static void main(String[] args) {
        Map<String, Integer> keyCount = new HashMap<>();

        keyCount.merge("A", 1, Integer::sum); // 메서드 참조 사용
        keyCount.merge("A", 1, Integer::sum);
        keyCount.merge("A", 1, Integer::sum);
        keyCount.merge("B", 1, Integer::sum);
        keyCount.merge("C", 1, Integer::sum);
        keyCount.merge("C", 1, (count, increment) -> count + increment); // 람다 사용

        for(String key : keyCount.keySet()) {
            System.out.println(key + " : " + keyCount.get(key));
        }
    }
}
