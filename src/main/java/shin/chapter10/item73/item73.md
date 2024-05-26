# Item73 - 추상화 수준에 맞는 예외를 던지라

## **예외 번역(Exception Translation)**

상위 계층에서 저수준 예외를 잡아 자신의 추상화 수준에 맞는 예외로 바꿔 던지는것

```java
try {
    ... // 저수준 추상화를 이용한다.
} catch (LowerLevelException e) {
    throw new HigherLevelException(...); // 추상화 수준에 맞게 번역한다.
}
```

```java
/**
* 이 리스트 안의 지정한 위치의 원소를 반환한다.
* @throws IndexOutOfBoundsException index가 범위 밖이라면,
*         즉 {@code index < 0 || index >= size()}이면 발생한다.
*/
public E get(int index) {
    try {
        return listIterator(index).next();
    } catch (NoSuchElementException exc) {
        throw new IndexOutOfBoundsException("Index: " + index);
    }
}
```

**예외 번역을 하지 않으면? 😵**

- 수행하려는 일과 관련없어 보이는 예외가 튀어나와 프로그래머를 당황시킨다.
- 내부 구현 방식을 드러내어 윗 레벨 API를 오염시킨다.
- 다음 릴리즈에서 구현 방식을 바꾸면 다른 예외가 튀어나와서 기존 클라이언트 프로그램을 깨지게 할 수 있다.

예외 연쇄를 사용해서 번역할 수도 있다.

### **예외 연쇄(Exception Chaining) 🖇**

- 예외 연쇄란? 문제의 근본 원인인 저수준 예외를 고수준 예외에 실어 보내는 방식
- Throwable.getCause()를 통해 언제든 저수준 예외를 꺼내볼 수 있다.
- 저수준 예외가 디버깅에 도움을 줄 수 있다.

```java
try {
    ... // 저수준 추상화를 이용한다.
} catch (LowerLevelException cause) {
    // 저수준 예외를 고수준 예외에 실어 보낸다.
    throw new HigherLevelException(cause);
}
```

```java
// 예외 연쇄용 생성자
class HigherLevelException extends Exception {
    HigherLevelException(Throwable cause) {
        super(cause);
  }
}
```

### **예외 번역을 남용해서는 안된다. 🙅**

> 가능하다면 저수준 메서드가 반드시 성공하도록 하여 하위 계층에서는 예외가 발생하지 않도록 하는 것이 최선이다.

**How?**

```java
public void function1(...) {
    // 미리 검사
    function2(...);
}
```
- 상위 계층 메서드의 매개변수 값을 하위 계층 메서드로 건네기 전에 미리 검사한다.
- 그래서 예외가 발생할 만한 매개변수를 하위 계층 메서드로 전달되지 않게끔 한다.

**하위 계층에서의 예외를 피할 수 없다면? (차선책)**

> 상위계층에서 그 예외를 조용히 처리하여 예외를 API 호출자에까지 전파하지 않는 방법도 있다

- java.util.logging 같은 적절한 로깅 기능을 활용하여 기록해두자.
- 이렇게 하면 클라이언트 코드에 문제를 전파하지 않으면서도 프로그래머가 로그를 분석해 추가 조치를 취할 수 있게 해준다.

```java
public void function1(...) {
    ...
    try {
        function2(...);
    } catch(...) {
        // 로그 기록
    }
}
```
