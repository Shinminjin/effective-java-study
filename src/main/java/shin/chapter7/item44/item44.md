# Item44 - 표준 함수형 인터페이스를 사용하라

자바가 람다를 지원하면서 상위 클래스의 기본 메서드를 재정의해 원하는 동작을 구현하는 **템플릿 메서드 패턴의 매력은 크게 줄었다.**

모던 자바에서는 템플릿 메서드 패턴 대신 **함수 객체를 받는 정적 팩터리나 생성자를 제공하는 방식으로 해법을 제시하고 있다.**

### **`LinkedHashMap`의 `removeEldestEntry`를 재정의하여 `Cache`를 구현해보자.**

### **📌 As-is. Template method pattern**

```java
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
```

```bash
{1=1, 2=2, 3=3}
{2=2, 3=3, 4=4}
{3=3, 4=4, 5=5}
```

- `Map`의 새로운 키를 추가하는 `put` 메서드에서 `removeEldestEntry` 메서드를 호출해 `true`가 반환되면 맵에서 가장 오래된 원소를 제거한다.

위 방식도 잘 동작하지만, `removeEldestEntry`를 람다를 이용해 다시 구현해보자.

아래는 함수 객체를 받는 생성자를 제공하여 캐시를 구현한 코드이다.

### **📌 To-be. Functional Interface**

```java
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
```

```bash
{1=1, 2=2, 3=3}
{2=2, 3=3, 4=4}
{3=3, 4=4, 5=5}
```

- 템플릿 메서드 예제에서 `removeEldestEntry`가 `size()`를 호출해 맵 안의 원소 수를 알아내는데,
이는 `removeEledestEntry`가 인스턴스 메서드라 가능한 방식이다.
- 하지만 생성자에 넘기는 함수 객체는 인스턴스 메서드가 아니므로 다른 방식이 필요하다.
- 맵 자기 자신(this)을 함수 객체에 건네주는 방식으로 코드를 구성했다.

## 표준 함수형 인터페이스

위 `EldestEntryRemovalFunction` 인터페이스도 잘 동작하지만, 자바 표준 라이브러리에서 이미 제공해주므로 굳이 사용할 필요가 없다.

- `BiPredicate`로 대체 가능

```java
public class UtilFunctionCache<K, V> extends LinkedHashMap<K, V> {

    private final BiPredicate<Map<K, V>, Map.Entry<K, V>> eldestEntryRemovalFunction;

    public UtilFunctionCache(BiPredicate<Map<K, V>, Map.Entry<K, V>> el) {
        this.eldestEntryRemovalFunction = el;
    }

    @Override
    protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
        return eldestEntryRemovalFunction.test(this, eldest);
    }
}
```

이미 자바 표준 라이브러리에는 다양한 용도의 표준 함수형 인터페이스가 제공되고 있다.

**필요한 용도에 맞는 게 있다면, 직접 구현하기보다는 표준 함수형 인터페이스를 활용하자.**

관리할 대상이 줄고, 유용한 디폴트 메서드를 많이 제공해줘 다른 코드와의 상호 운용성도 좋아진다.

---
java.util.function은 총 43개의 함수형 인터페이스를 제공한다.

전부 기억하긴 어려워도 기본 인터페이스 6개만 기억하면 나머지를 충분히 유추해 낼 수 있다.

### **기본 함수형 인터페이스 정리 표**

| 인터페이스 | 함수 시그니처 | 의미  | 예 |
| --- | --- | --- | --- |
| UnaryOperator<T> | T apply(T t) | 반환 값과 인수의 타입이 같은 함수, 인수는 1개 | String::toLowerCase |
| BinaryOperator<T> | T apply(T t1, T t2) | 반환 값과 인수의 타입이 같은 함수, 인수는 2개 | BigInteger::add |
| Predicate<T> | boolean test(T t) | 인수 하나를 받아 boolean을 반환하는 함수 | Collection::isEmpty |
| Function<T, R> | R apply(T t) | 인수와 반환 타입이 다른 함수 | Arrays::asList |
| Supplier<T> | T get() | 인수를 받지 않고 값을 반환하는 함수 | Instant::now |
| Consumer<T> | void accept(T t) | 인수 하나 받고 반환 값은 없는 함수 | System.out::println |

### 기본 타입을 위한 함수형 인터페이스의 변형

- 6개의 인터페이스는 기본 타입인 int, long, double 용으로 각 3개씩 변형이 있다.

  **(6 x (1 + 3) =24가지)** : 6 x (자신 + 변형)

  ex) IntPredicate, LongBinaryOperator, IntFunction…

- Function 인터페이스에는 기본 타입을 반환하는 변형이 총 **9가지**가 있다.
    - SrcToResult 변형 - 입력과 결과 모두 기본 타입

      ex) LongToIntFunction, IntToLongFunction… (**6가지**)

    - ToResult 변형 - 입력은 객체 참조, 결과는 기본 타입

      ex) ToLongFunction<int []>… (**3가지**)

- 인수를 2개씩 받는 변형이 **9가지** 있다.
    - BiPredicate<T, U> (**1가지**)
    - BiFunction<T,U,R> (**1(자신) + 3(변형) = 4가지**)
        - `ToIntBiFunction<T,U,R>`
        - `ToLongBiFunction<T,U,R>`
        - `ToDoubleBiFunction<T,U,R>`
    - BiConsumer<T, U> (**1(자신) + 3(변형) = 4가지**)
        - ObjDoubleConsumer<T>
        - ObjIntConsumer<T>
        - ObjLongConsumer<T>
- boolean을 반환하는 BooleanSupplier는 Supplier의 변형 (**1가지**)

표준 함수형 인터페이스는 총 43(=24 + 9 + 9 + 1)개다.

### **💡** 기본 함수형 인터페이스 사용 시 주의사항

표준 함수형 인터페이스 대부분은 기본 타입만 지원한다.

그렇다고 **기본 함수형 인터페이스에 박싱된 기본 타입을 넣어 사용하지 말자.**

- 동작은 하지만, 계산량이 많을 경우 성능히 처참히 저하된다.

## 그렇다면 코드를 직접 작성해야 할 때는 언제일까?

대부분 상황에서는 직접 작성하는 것보다 표준 함수형 인터페이스를 사용하는 편이 낫다.

그런데 구조적으로 같은 표준 함수형 인터페이스가 있더라도 직접 작성해야만 할 때가 있다.

`Compartor<T>` 인터페이스를 떠올려보자.

```java
@FunctionalInterface
public interface Comparator<T> {
    int compare(T o1, T o2);
}
```

구조적으로 표준 함수형 인터페이스인 `ToIntBiFunction<T, U>`와 동일하다.

```java
@FunctionalInterface
public interface ToIntBiFunction<T, U> {
    int applyAsInt(T t, U u);
}
```

그런데도 `Comparator<T>`를 `ToIntBiFunction<T, U>`로 대체하지 않았다.

**Compartor가 독자적인 인터페이스로 남은 이유**

1. 자주 쓰이며, 이름 자체가 용도를 명확히 설명해준다.
2. 구현하는 쪽에서 반드시 지켜야 할 규약을 담고 있다.
3. 유용한 디폴트 메서드를 제공할 수 있다.

위 조건 중 하나 이상을 만족한다면,
**전용 함수형 인터페이스를 구현할지 고민해보는 것이 좋다.**

## **@FunctionalInterface**

**직접 만든 함수형 인터페이스에는 항상 @FunctionalInterface 애너테이션을 사용하자.**

1. 해당 클래스의 코드나 설명 문서를 읽을 이에게 람다용으로 설계된 것임을 알려준다.
2. 인터페이스가 하나의 추상 메서드만을 담고 있어야 컴파일되게 해 준다.
3. 유지보수 과정에서 누군가 실수로 메서드를 추가하지 못하게 막아준다.

## 함수형 인터페이스를 API에서 사용할 때 주의 사항

서로 다른 함수형 인터페이스를 같은 위치의 인수로 받는 메서드들을 다중 정의해서는 안된다.

`ExecutorService`의 `submit` 메서드

```java
public interface ExecutorService extends Executor {
    ...
    <T> Future<T> submit(Callable<T> task);

    Future<?> submit(Runnable task);
    ...
}
```

- `Callable<T>`와 `Runnable`을 받는 것을 다중 정의했다.
- 보통 람다를 매개변수로 넘기는데, 이 때 강제 형변환이 필요할 수도 있고, 코드가 더러워진다.

그러니 **서로 다른 함수형 인터페이스를 같은 위치의 인수로 사용하는 다중 정의를 피하자.**

### 참고
https://jaeseo0519.tistory.com/214
