package shin.chapter7.item44;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.BiPredicate;

public class UtilFunctionCache<K, V> extends LinkedHashMap<K, V> {

    private final BiPredicate<Map<K, V>, Map.Entry<K, V>> eldestEntryRemovalFunction;

    public UtilFunctionCache(BiPredicate<Map<K, V>, Map.Entry<K, V>> el) {
        this.eldestEntryRemovalFunction = el;
    }

    @Override
    protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
        return eldestEntryRemovalFunction.test(this, eldest);
    }

    public static void main(String[] args) {
        UtilFunctionCache<String, String> cache = new UtilFunctionCache<>((map, eldest) -> map.size() > 3);
        cache.put("1", "1");
        cache.put("2", "2");
        cache.put("3", "3");
        System.out.println(cache);
        cache.put("4", "4");
        System.out.println(cache);
        cache.put("5", "5");
        System.out.println(cache);
    }
}
