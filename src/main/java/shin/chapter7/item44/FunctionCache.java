package shin.chapter7.item44;

import java.util.LinkedHashMap;
import java.util.Map;

@FunctionalInterface interface EldestEntryRemovalFunction<K, V> {
    boolean remove(Map<K, V> map, Map.Entry<K, V> eldest);
}

public class FunctionCache<K, V> extends LinkedHashMap<K, V> {
    private final EldestEntryRemovalFunction<K, V> eldestEntryRemovalFunction;

    public FunctionCache(EldestEntryRemovalFunction<K, V> el) {
        this.eldestEntryRemovalFunction = el;
    }

    @Override
    protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
        return eldestEntryRemovalFunction.remove(this, eldest);
    }

    public static void main(String[] args) {
        FunctionCache<String, String> cache = new FunctionCache<>((map, eldest) -> map.size() > 3);
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
