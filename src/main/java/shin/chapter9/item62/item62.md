# Item 62 - 다른 타입이 적절하다면 문자열 사용을 피하라

## 시작하면서

- 다양한 데이터 타입을 문자열로 표현 할 수 있다.
- 하지만 데이터 타입을 문자열로 관리하는 것은 좋지 않다.

## **문자열은 다른 타입을 대신하기에 적절하지 않다. 🙅‍♂️**

- 파일, 네트워크, 키보드의 입력으로부터 데이터를 받을 때 `String`을 사용할 수 있다.
- 하지만, 입력 받을 데이터가 진짜 문자열인 때만 `String`으로 받는게 좋다.
- 입력 받은 데이터 타입
    - 수치형이라면 `int`, `float`, `BigInteger` 등 적절한 수치 타입으로 변환해야 한다.
    - ‘예/아니오’라면 `Enum` 타입이나 `boolean`으로 변환해야 한다.
- 적절한 타입이 있다면 그것을 사용하고, 없다면 새로운 타입을 하나 만들어 사용하자.

## **문자열은 열거 타입을 나타내기에 적절하지 않다. 🙅‍♂️**

- 상수를 열거 하는 경우 문자열보다는 열거 타입이 월등히 낫다.

## **문자열은 혼합 타입을 대신하기에 적합하지 않다. 🙅‍♂️**

여러 요소가 혼합된 데이터를 하나의 문자열로 표현하는 것은 좋지 않다.

```java
String compoundKey = className + "#" + i.next();
```

- #이 className, i.next()에 쓰인다면 혼란을 유발한다.
- className을 뽑아내기 위해서는 String을 파싱 해야 한다. → 느리고, 귀찮다.
- 적절한 `equals`, `toString`, `compareTo` 메서드를 제공할 수 없고, `String`이 제공하는 기능에만 의존해야 한다.
- 전용 클래스를 따로 만드는 게 낫다.

```java
private static class CompoundKey {
	  String className;
	  String next;
	  
	  public CompoundKey(String className, String next) {
		    this.className = className;
		    this.next = next;
	  }
}
```

## **문자열은 권한을 표현하기에 적합하지 않다. 🙅‍♂️**

권한(capacity)을 문자열로 표현하는 경우 보안이 취약해지며 의도적으로 같은 키를 사용하여 값을 탈취하는 문제점이 생길 수 있다.

### 권한을 문자열로 관리하는 ThreadLocal 예시

```java
// AS-IS : 클라이언트가 제공한 문자열 key로 스레드별 지역변수를 식별
public class ThreadLocal {
    private ThreadLocal() {...};

    // 현 스레드의 값을 키로 구분해 저장한다.
    public static void set(String key, Object value) {...};

    // (키가 가르키는) 현 스레드의 값을 반환한다.
    public static Object get(String key) {...};
}
```

위 방식에는 **두 가지 문제점**이 있다.

- 스레드 구분용 문자열 key가 전역 네임스페이스에서 공유된다.
- 여러 클라이언트가 변수를 공유하게 될 수도 있다. (보안에 취약하다.)

> 이를 해결하기 위해 API는 문자열 대신 위조할 수 없는 키를 사용하면 된다.
>

```java
// TO-BE : 스레드 지역변수가 위조할 수 없는 키(권한) 그 자체로 구현
public final class ThreadLocal<T> {
		public ThreadLocal() {...};
    public void set(T value) {...};
    public T get() {...};
}
```

- 여러 클라이언트가 공유할 수 없다.
- 추가로 매개변수화 타입을 사용하여 타입 안정성을 확보했다.

## **💡 핵심 정리**

- 데이터의 내용이 문자열인 경우에만 `String`으로 관리한다.
- 그 외에 이미 라이브러리 내에 존재하는 타입인 경우, 해당 타입에 맞춰서 관리한다.
- 특정 값에 대한 정의가 가능한 경우, 사용자 정의에 따른 클래스를 정의하여 관리한다.
- 문자열을 잘못 사용하는 흔한 예로는 기본 타입, 열거 타입, 혼합 타입등이 있다.
