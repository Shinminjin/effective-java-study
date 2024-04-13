# Item 55 - 옵셔널 반환은 신중히 하라

## **메서드가 특정 조건에서 값을 반환할 수 없을 때**

### **자바 8 이전**

**(1) 예외 던지기**,  **(2) null 반환** 두 가지 선택지가 존재했다.

하지만 이 방법들에는 모두 단점이 존재한다.

1. 예외를 던지는 방법
   - 진짜 예외적인 상황에서만 사용해야 한다.
   - 예외 생성 시, 스택 추적 전체를 캡처하는 비용이 만만치 않다.

2. null을 반환하는 방법
   - 별도의 null 처리 코드를 추가해야 한다.
   - null 처리를 제대로 하지 않으면, `NPE` 발생 가능성이 높다.

### **자바 8 이후 도입된 Optional<T>**

- T 타입 참조를 하나 담거나, 혹은 아무것도 담지 않을 수 있다.
- `Optional<T>`는 원소를 최대 1개 가질 수 있는 ‘불변’ 컬렉션이다.
- 예외를 던지는 메서드보다 유연하고 사용하기 쉽다.
- null을 반환하는 메서드보다 오류 가능성이 작다.

## **Optional 적용시켜보기 - 최댓값 구하기**

### **적용 전**

```java
public static <E extends Comparable<E>> E max(Collection<E> c) {
    if (c.isEmpty())
        throw new IllegalArgumentException("빈 컬렉션");

    E result = null;
    for (E e : c)
        if (result == null || e.compareTo(result) > 0)
            result = Objects.requireNonNull(e);

    return result;
}
```
- 컬렉션에서 가장 큰 값을 구하는 메서드이다.
- 컬렉션이 비었을 때, `IllegalArgumentException` 예외를 던진다.

### **적용 후**

```java
public static <E extends Comparable<E>>
        Optional<E> max2(Collection<E> c) {
    if (c.isEmpty())
        return Optional.empty();

    E result = null;
    for (E e : c)
        if (result == null || e.compareTo(result) > 0)
            result = Objects.requireNonNull(e);

    return Optional.of(result);
}
```
- 컬렉션이 비었을 때, `Optional.empty()` 를 반환한다.
- 컬렉션이 비어있지 않다면, `Optional.of(result)` 를 반환한다.
- `Optional`을 사용한다면, null 때문에 예외를 던지거나 null을 반환할 일이 없다.

> 위와 같이 빈 컬렉션을 건넸을 때, `IllegalArgumentException`을 던지는 것보다 `Optional<E>`를 반환하는 편이 더 낫다. - item30


### **테스트**

```java
@Test
public void maxTest() {
    List<Integer> integers = List.of(2, 4, 10, 22, 33, 11, 55, 25, 29);

    Integer max = max(integers);
    Optional<Integer> optional = max2(integers);

    System.out.println(max);
    optional.ifPresent(System.out::println);
}
```
output
```bash
55
55
```

## **Optional과 Stream 함께 이용해보기**

```java
public static <E extends Comparable<E>>
        Optional<E> max3(Collection<E> c) {
    return c.stream().max(Comparator.naturalOrder());
}

@Test
public void maxTest2() {
    List<Integer> integers = List.of(2, 4, 10, 22, 33, 11, 55, 25, 29);
    Optional<Integer> optional = max3(integers);
    optional.ifPresent(System.out::println); // 55
}
```
- 코드가 매우 짧아졌지만, 기존의 동작을 잘 수행하고 있다.

## **Optional을 왜 사용할까?**

- 비검사 예외를 던지거나 null을 반환한다면 API 사용자가 이를 인식하지 못해 런타임에 예상치 못한 장애가 발생할 수 있다.
- 하지만 검사 예외를 던지면 클라이언트는 반드시 이에 대처하는 코드를 작성해야 한다.
- **옵셔널은 검사 예외(Checked Exception)와 취지가 비슷하다.**
- 반환값이 없을 수도 있음을 API 사용자에게 명확히 알려준다.

## **옵셔널 활용하기**

### **1. 기본 값 정하기 (`optional.orElse()`)**

```java
@Test
public void optionalDefaultValue() {
    List<Integer> integers = new ArrayList<>();
    Integer optional = max3(integers).orElse(0);
}
```
- `orElse()`는 `Optional` 내부 값이 empty 라면 지정된 값을 반환한다.
- 만일 내부 값이 존재한다면 `get()` 과 같은 결과를 갖는다.

### **2. 기본 예외 정하기 (`optional.orElseThrow()`)**

```java
@Test
public void optionalDefaultThrow() {
    List<Integer> integers = new ArrayList<>();
    Integer optional = max3(integers).orElseThrow(IllegalArgumentException::new);
}
```
- `orElseThrow()` 는 `Optional` 내부 값이 empty 라면 지정된 예외를 반환한다.
- 만일 내부 값이 존재한다면 `get()` 과 같은 결과를 갖는다.

### **3. 항상 값이 채워져 있다고 가정한다. (`optional.get()`)**

```java
@Test
public void optionalDefaultGet() {
    List<Integer> integers = new ArrayList<>(List.of());
    Integer optional = max3(integers).get();
}
```
- 값이 무조건 채워져있다고 가정한다.
- 없다면, `NoSuchElementException` 을 맞이하게 된다.

### **4. 기본값을 설정하는 비용이 아주 큰 경우**

```java
public T orElse(T other)

public static String orElseBenchmark() {
    return Optional.of("baeldung").orElse(getRandomName());

```
```java
public T orElseGet(Supplier<? extends T> other)

public static String orElseGetBenchmark() {
    return Optional.of("baeldung").orElseGet(() -> getRandomName());
}
```
- `Supplier<T>`를 인수로 받는 `orElseGet`을 사용하면 초기 설정 비용을 낮출 수 있다.
- 값이 처음 필요할 때, `Supplier<T>`를 사용해 생성하기 때문이다.

## **유용한 메서드**

- 더 특별한 쓰임에 대비한 메서드도 있다. 바로 `fliter`, `map`, `flatMap`, `ifPresent` 이다.
- 앞선 기본 메서드로 처리하기 어려워 보인다면 위의 고급 메서드들이 문제를 해결해줄 수 있을지 검토해보자.

### **isPresent 메서드**

- 옵셔널 객체 내부의 값이 있는 경우 `true`, 비어 있으면 `false`를 반환한다.
- 이 메서드로 원하는 모든 작업을 수행할 수 있지만, **신중히 사용해야 한다.**
- 앞서 언급한 메서드들로 대체할 수 있는지 확인하자. 그 편이 더 짧고 명확하며 용법에 맞는 코드가 된다.

```java
public static void main(String[] args) {
    ProcessHandle ph = ProcessHandle.current();

    // isPresent를 적절치 못하게 사용했다.
    Optional<ProcessHandle> parentProcess = ph.parent();
    System.out.println("부모 PID: " + (parentProcess.isPresent() ?
            String.valueOf(parentProcess.get().pid()) : "N/A"));
}
```

상위 코드는 Optional의 `map`을 사용하여 아래와 같이 다듬을 수 있다.
```java
// 같은 기능을 Optional의 map를 이용해 개선한 코드
System.out.println("부모 PID: " +
    ph.parent().map(h -> String.valueOf(h.pid())).orElse("N/A"));
```

## **Optional을 사용하면 안되는 경우 ❌**

반환값으로 옵셔널을 사용한다고 해서 무조건 득이 되는 건 아니다.

### **1. 컬렉션, 스트림, 배열, 옵셔널 같은 컨테이너 타입은 옵셔널로 감싸면 안된다.**

- 빈 `Optional<List<T>>`를 반환하기보다는 빈 `List<T>`를 반환하는게 좋다. - *item 54*
- 빈 컨테이너를 반환하면 클라이언트에 옵셔널 처리 코드를 넣지 않아도 된다.

### **2. 박싱된 기본 타입을 담은 옵셔널을 반환하면 안 된다.**

- 박싱된 기본 타입을 담는 옵셔널은 기본 타입 자체보다 무겁다.
- 대신 `OptionalInt`, `OptionalLong`, `OptionalDouble` 전용 옵셔널 클래스를 사용하자.

### **3. 옵셔널을 컬렉션의 키, 값, 원소나 배열의 원소로 사용하지 말자**

- 쓸데없이 복잡하고, 혼란과 오류 가능성을 키운다.

## **💡 핵심 정리**

- 값을 반환하지 못할 가능성이 있고, 호출할 때마다 반환값이 없을 가능성을 염두에 둬야 하는 메서드라면 옵셔널을 반환해야 할 상황일 수 있다.
- 하지만 옵셔널 반환에는 성능 저하가 뒤따르니, 성능에 민감한 메서드라면 null을 반환하거나 예외를 던지는 편이 나을 수 있다.
- 옵셔널을 반환값 이외의 용도로 쓰는 경우는 매우 드물다.
