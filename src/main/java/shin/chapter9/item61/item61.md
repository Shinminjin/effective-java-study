# Item 61 - 박싱된 기본 타입보다는 기본 타입을 사용하라

## **자바의 데이터 타입**

- 기본 타입 : `byte`, `short`, `int`, `long`, `float`, `double`, `char`, `boolean`
- 참조 타입 : 클래스 타입, 인터페이스 타입, 배열 타입, 열거 타입 (ex. `String`, `List`..)
- 기본 타입은 모두 대응되는 참조 타입이 하나씩 있으며, 이를 **박싱된 기본 타입**이라고 한다.
    - `Byte`, `Short`, `Integer`, `Long`, `Float`, `Double`, `Character`, `Boolean`
- 오토 박싱과 오토 언박싱 덕분에 두 타입을 크게 구분하지 않고 사용할 수는 있지만, 둘의 차이가 사라지는 것은 아니다.

## **기본 타입 vs 박싱된 기본 타입**

1. 기본 타입은 값만 가지고 있으나, **박싱된 기본 타입**은 값에 더해 **식별성**이란 속성을 갖는다.

   즉, 박싱된 기본 타입의 두 인스턴스는 값이 같아도 서로 다르다고 식별될 수 있다.


```java
@Test
void int_Integer_difference_test() {
    final int num1 = 1;
    final int num2 = 1;
    assertTrue(num1 == num2);

    final Integer num3 = 1;
    final Integer num4 = 1;
    assertTrue(num3 == num4);
    assertTrue(num3.equals(num4));

    final Integer num5 = new Integer(1);
    final Integer num6 = new Integer(1);
    assertFalse(num5 == num6); // 두 인스턴스가 서로 다르다고 식별됨.
    assertTrue(num5.equals(num6));
}
```

2. 기본 타입의 값은 null을 가질 수 없으나 **박싱된 기본 타입**의 값은 **null을 가질 수 있다.**

```java
int number = null; // 에러
Integer number = null; // 가능
```

3. 기본 타입이 박싱된 기본 타입보다 **시간과 메모리 사용면**에서 더 효율적이다.

## **식별성(identity)**

```java
// Integer 값을 오름차순으로 정렬하는 비교자
// 잘못 구현된 비교자
Comprator<Integer> naturalOrder = (i, j) -> (i < j) ? -1 : (i == j ? 0 : 1);
```

- 위 코드는 웬만하면 버그 없이 코드가 잘 돌아간다.

**하지만 다음과 같은 경우는 어떨까?**

```java
naturalOrder.compare(new Integer(42), new Integer(42))
```

- 두 인스턴스의 값이 42이므로 0을 출력해야 할 것 같지만, 실제로는 1을 출력한다.
- 동일성 비교 연산자(`==`)는 기본 타입에서는 서로의 값을 비교해 주지만, 참조 타입에서는 객체가 서로 같은지를 판별한다.
- 즉, 박싱된 기본 타입에서는 값은 서로 같지만 객체가 서로 다르므로 `i == j`가 `false`이다.

### **해결 방법**

```java
// 문제를 수정한 비교자
Comprator<Integer> naturalOrder = (iBoxed, jBoxed) -> {
		int i = iBoxed, j = jBoxed; // 오토 언박싱
		return (i < j) ? -1 : (i == j ? 0 : 1);
}
```

- 박싱된 `Integer` 매개변수 값을 기본 타입 정수로 저장한 다음, 모든 비교를 기본 타입 변수로 수행하면 해결된다.

## **null 가능성**

```java
// 기이하게 동작하는 프로그램
public class Unbelievable {
    static Integer i;
  
    public static void main(String[] args) {
		    if (i == 42) // NPE 발생 : i 객체가 null (0이 아니다.)
		        System.out.println("믿을 수 없군!");
    }
}
```

- 기본 타입과 박싱된 기본 타입을 혼용한 연산에서는 기본적으로 박싱된 기본 타입의 박싱이 풀리게 된다. (**오토 언박싱**)
- null 참조를 언박싱하면 `NPE`가 발생한다. 위 예시에서는 `Integer` i의 초깃값이 null이다.
- **해결 방법?** i를 `int`로 선언하면 해결된다.

## **성능 문제**

```java
// 끔찍이 느린 코드
public static void main(String[] args) {
		Long sum = 0L;
    for (long i = 0; i <= Integer.MAX_VALUE; i++) {
        sum += i;
    }
    System.out.println(sum);
}
```

- 위 코드는 지역변수 `sum`을 박싱된 기본 타입(`Long`)으로 선언하여 느려졌다.
- 박싱과 언박싱이 반복해서 일어나 체감될 정도로 성능이 느려진다.

## **그럼 언제 박싱된 기본 타입은 언제 써야 하는가?**

1. 컬렉션의 원소, 키, 값으로 사용할 때
    - 컬렉션은 기본 타입을 담을 수 없으므로 어쩔 수 없이 박싱된 기본 타입을 써야 한다.
2. 제네릭을 사용할 때
    - 제네릭에서도 기본 타입을 지원하지 않기 때문에 박싱된 기본 타입을 써야 한다.
3. 리플렉션을 통해 메서드를 호출할 때

## **💡 핵심 정리**

- 오토박싱이 박싱된 기본 타입을 사용할 때의 번거로움을 줄여주지만, 위험까지 없애주지 않는다.
- 오토 언방식 과정에서 `NPE`를 던질 수 있다.
- 기본 타입을 박싱하는 작업은 필요 없는 객체를 생성하는 부작용을 일으킬 수도 있다.
