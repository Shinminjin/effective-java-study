# Item 49 - 매개변수가 유효한지 검사하라

## **메서드와 생성자**

📌 *일반적으로 메서드와 생성자 대부분은 입력 매개변수의 값이 특정 조건을 만족하기를 바란다.*

```java
@Data
@AllArgsConstructor
public class Order {
    private String product; // 제품명
    private double amount; // 가격
}

public void processOrder(Order order) {
    // 주문 처리 로직
}
```

주문을 위한 `Order` 클래스가 있고,

주문 처리 로직이 포함된 `processOrder` 메서드가 있다고 가정한다.

- `product`가 null 이어선 안된다.
- `amount`가 음수여선 안된다.

이런 제약은 반드시 문서화해야 하며 메서드 몸체가 실행되기 전에 검증을 해야 한다.

## **오류를 즉시 잡아라** 🚑

> *오류는 가능한 한 빨리 발생한 곳에서 잡아야 한다.*
>

그렇지 않으면 해당 오류를 감지하기 더욱 어려워지고, 오류 발생 지점을 찾기 힘들어진다.

따라서 메서드 몸체가 실행되기 전에 매개변수를 확인한다면,

잘못된 값이 넘어왔을 때 깔끔한 방식으로 예외를 던질 수 있다.

## **매개변수 검증이 제대로 이뤄지지 않을 때 생기는 문제**

1. 메서드가 수행되는 중간에 모호한 예외를 던지며 실패할 수 있다.
2. 메서드가 잘 수행되지만 잘못된 결과를 반환한다.
3. 메서드는 문제 없이 수행됐지만, 메서드에서 사용한 어떤 객체를 이상한 상태로 만들어 미래의 알 수 없는 시점에 이 메서드와 관련 없는 오류를 낼 수 있다.

> *아래 단계의 현상일수록 문제는 더 심각해진다.*
>

1번의 경우, 예외를 던지며 에러로그를 남기게 되지만,

2, 3번의 경우, 실제로 데이터 정합성이 깨진 것을 확인했을 때 인지가 가능하기 때문이다.

이렇게 매개변수 검증에 실패하면 **실패 원자성(failure atomicity)을 어기는 결과가 나타난다.**

**실패 원자성❓**(item76)

호출된 메서드가 실패하더라도 해당 객체는 호출 전의 상태를 유지해야 한다.

## **예외의 문서화**

- Javadoc의 `@throws` 태그를 이용한다.
- 보통 `IllegalArgumentException`, `IndexOutOFBoundsException`, `NullPointerException` 중 하나가 될 것이다.

### **예외 문서화 코드의 예시**

```java
/**
 * Returns a BigInteger whose value is {@code (this mod m}).  This method
 * differs from {@code remainder} in that it always returns a
 * <i>non-negative</i> BigInteger.
 *
 * @param  m the modulus.
 * @return {@code this mod m}
 * @throws ArithmeticException {@code m} &le; 0
 * @see    #remainder
 */
public BigInteger mod(BigInteger m) {
    if (m.signum <= 0)
        throw new ArithmeticException("BigInteger: modulus not positive");

    BigInteger result = this.remainder(m);
    return (result.signum >= 0 ? result : result.add(m));
}
```

- 위는 `BigInteger.mod` 메서드로 매개변수 m이 0 이하일 때, `ArithmeticException`이 발생하는 것을 `@throws` 태그로 명시하고 있다.

> *모든 메서드에 적용되는 문서화를 하고 싶다면 클래스 레벨에서 Javadoc을 작성하라.*

```java
All methods and constructors in this class throw {@code NullPointerException}
when passed a null object reference for any input parameter
```

- m이 null일 때, `NullPointerException`을 던지지만 메서드 주석에 언급이 없다.
- 위와 같이, 클래스(BigInteger) 레벨 주석에서 모든 메서드와 생성자가 `NullPointerException`을 던질 수 있다는 점을 기술했기 때문이다.

## **null check 🕶️**

```java
this.strategy = Objects.requireNonNull(strategy, "전략");
```

- @Nullable이나 비슷한 목적의 애너테이션은 비표준 방식이다.
- Java7에 추가된 `java.util.Objects.requireNonNull` 메서드는 유연하고 편하다.
    - null check를 수동으로 하지 않아도 된다.
    - 예외 메시지를 지정할 수 있다.
    - 입력을 그대로 반환하므로, 값을 사용하면서 동시에 null check가 가능하다.
    - 반환값은 무시하고 순수한 null 검사 목적으로 어디서든 사용 가능하다.
- Java9에서 Objects의 범위 검사 기능이 추가되었다.
    - `checkFromIndexSize`, `checkFromToIndex`, `checkIndex`
    - 예외 메시지 지정이 불가능하고, 리스트와 배열 전용이면서 닫힌 범위는 다루지 못한다.

## **자바의 `assert`기능**

https://www.baeldung.com/java-assert

> *공개되지 않은 메서드라면 패키지 제작자가 메서드가 호출되는 상황을 통제할 수 있다.*

즉, 유효한 값만이 메서드에 넘겨진다는 것을 보증할 수 있다는 뜻이다.

다시 말해 `public`이 아닌 메서드라면 `assert` 단언문을 통해서 매개변수 유효성을 검증할 수 있다.

```java
private void processOrder(Order order) {
    assert order.getProduct() != null;
    assert order.getAmount() >= 0;
    
    // 주문처리 로직
}
```

- `assert` 단언문은 자신이 선언한 조건이 무조건 참이어야 다음 로직을 수행한다.
- 실패하면, `AssertionError`를 던진다.
- 보통 개발 중에 테스팅 목적으로 사용된다.

## **나중에 쓰기 위해 저장하는 매개변수의 유효성 검사**

**✔️ 입력 받은 int 배열을 List 로 변경해주는 정적 팩터리 메서드**

```java
static List<Integer> intArrayAsList(int[] a){
    Objects.requireNonNull(a); // null 검사

    return new AbstractList<Integer>() {
        @Override
        public Integer get(int index) {
            return a[index]; // 0x124124 + index
        }

        @Override
        public int size() {
            return a.length;
        }
    };
}
```

- `Objects.requireNonNull`을 이용해 배열의 유효성을 검사하지 않았다면, 클라이언트가 반환된 `List`를 사용하려고 하는 시점에서야 `NullPointerException`을 받게 된다.
- 이런 경우, 오류 발생 지점을 추적하기 어려워진다.

**✔️ 생성자**

- “**나중에 쓰려고 저장하는 매개변수의 유효성을 검사하라**”는 원칙의 특수한 사례
- 생성자 매개변수의 유효성 검사는 클래스 불변식을 위해 꼭 필요하다.

## **규칙의 예외**

- 유효성 검사 비용이 지나치게 높거나, 실용적이지 않을 때
- 계산 과정에서 암묵적으로 검사가 수행될 때
    - `Collections.sort(List)`는 리스트 내 객체들이 모두 상호 비교 될 수 있어야 하며, 정렬 과정에서 이 비교가 이뤄진다. 만약 상호 비교될 수 없는 타입의 객체가 들어 있다면 `ClassCastException` 을 던질 것이다.
    - 단, 암묵적 유효성 검사에 너무 의존하면 실패 원자성을 해칠 수 있다.

## **💡 정리**

- 메서드나 생성자를 작성할 때 그 매개변수들에 어떠한 제약이 있을지 생각하자.
- 제약들을 문서화하고, 메서드 코드 시작 부분에서 명시적으로 검사하자.
