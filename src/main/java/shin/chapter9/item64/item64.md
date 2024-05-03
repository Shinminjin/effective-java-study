# Item 64 - 객체는 인터페이스를 사용해 참조하라

## **객체는 클래스가 아닌 인터페이스 타입으로 선언하라**

```java
// 좋은 예. 인터페이스를 타입으로 사용했다.
Set<String> set = new LinkedHashSet<>();

// 나쁜 예. 클래스를 타입으로 사용했다.
LinkedHashSet<String> set = new LinkedHashSet<>();
```

- 인터페이스를 타입으로 사용하게 되면 프로그램이 훨씬 유연해진다.

### **인터페이스를 타입으로 사용할 경우**

```java
// LinkedHashSet -> HashSet으로 구현체를 변경해도 코드가 변하지 않음
Set<String> set = new HashSet<>();

set.add(...);
set.remove(...);
set.size(...);
set.contains(...);

public void ...(Set<String> set, ...)

public int ...(Set<String> set, ...)

public String ...(Set<String> set, ...)

public boolean ...(Set<String> set, ...)

public long ...(Set<String> set, ...)
```

- 인터페이스를 참조하여 만든 객체는 구현 타입이 변하더라도 인터페이스에 있는 메서드를 그대로 사용할 수 있다.

### **클래스를 타입으로 사용할 경우**

```java
// LinkedHashSet -> HashSet으로 구현체를 변경
HashSet<String> set = new HashSet<>();

set.add(...);
set.remove(...);
set.size(...);
set.contains(...);

// 컴파일 에러 발생
public void ...(LinkedHashSet<String> set, ...)
```

- 객체 클래스로 만든다면 관련된 모든 코드를 수정해야 컴파일 오류가 발생하지 않는다.

> **그러나, 인터페이스가 항상 정답은 아니다. 🙅**
>

## **클래스를 써야 하는 경우**

### **적합한 인터페이스가 없는 경우**

- `String`, `BigInteger` 같은 값 클래스

```java
public final class String
    implements java.io.Serializable, Comparable<String>, CharSequence {

    /**
     * The value is used for character storage.
		**/
```

- 클래스 기반으로 작성된 프레임 워크가 제공하는 객체
  - `OutputStream` 등 [java.io](http://java.io/) 패키지의 여러 클래스

```java
public abstract class OutputStream implements Closeable, Flushable {
    ...
}

OutputStream outputStream = new FileOutputStream("");
```

- `OutputStream`을 구현하는 클래스는 `FileOutputStream`, `ByteArrayOutputStream` 가 있다.
- 확장한 기능을 사용하기 위해선 클래스를 참조해야 한다.
- 특정 구현 클래스 보다는 기반 클래스인 `OutputStream`을 사용하는 것이 더 좋다.

https://docs.oracle.com/javase/7/docs/api/java/io/ByteArrayOutputStream.html

### **해당 클래스에서만 제공하는 특수한 메서드를 사용하는 경우**

- `PriorityQueue`의 `comparator` 메서드는 `Queue` 인터페이스에서는 제공하지 않는다.

> 다만 클래스를 남발하진 말고, 되도록 인터페이스를 쓰자.


## **💡 핵심 정리**
- 객체는 최대한 인터페이스를 활용하여 유연성을 유지 하자.
- 단, 적합한 인터페이스가 없다면 클래스의 계층구조 중 필요한 기능을 만족하는 가장 덜 구체적인(상위의) 클래스를 타입으로 사용하자.
