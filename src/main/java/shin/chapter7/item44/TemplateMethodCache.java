package shin.chapter7.item44;

import java.util.LinkedHashMap;
import java.util.Map;

public class TemplateMethodCache<K, V> extends LinkedHashMap<K, V> {

    private final int maxSize;

    public TemplateMethodCache(int maxSize) {
        this.maxSize = maxSize;
    }

    @Override
    protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
        return size() > this.maxSize;
    }

    public static void main(String[] args) {
        TemplateMethodCache<String, String> cache = new TemplateMethodCache<>(3);
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
