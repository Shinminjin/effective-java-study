# Item 63 - 문자열 연결은 느리니 주의하라

## **문자열 연결 연산자 (`+`)**

- 문자열 연결 연산자(`+`)는 여러 문자열을 하나로 합쳐주는 편리한 수단이지만, 본격적으로 사용하면 성능 저하를 감내하기 어렵다.
- 문자열 연결 연산자로 문자열 n개를 잇는 시간은 n^2에 비례한다. → 엄청 느리다.
- 그 이유는 문자열(`String`)이 **불변**이기 때문이다. 불변인 문자열을 연결하기 위해서는 양쪽 내용을 복사해 새로운 문자열을 만들어야 한다.

## **성능을 개선하기 위한 방안**

`String` 대신 `StringBuilder`나 `StringBuffer`를 사용하자.

`StringBuilder`와 `StringBuffer`는 가변성을 가지고 있다.

가변성을 가지고 있기 때문에 동일 객체 내에서 문자열을 변경할 수 있다.

## **StringBuilder**

https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/lang/StringBuilder.html.

- 동기화(`Synchronized`)를 지원하지 않는다. (**Non Thread Safe**)
- 대신, 싱글 스레드에서의 성능은 `StringBuffer`보다 뛰어나다.
- 기존 데이터에 더하는 방식이라 속도가 빠르다.

## **StringBuffer**

https://docs.oracle.com/en/java/javase/12/docs/api/java.base/java/lang/StringBuffer.html

- 멀티스레드 환경에서 안전하다 (**Thread Safe**) → `Synchronized` 키워드를 사용해 동기화 가능
- `String` 자료형 보다 메모리 사용량이 많고 속도가 느리다.
- `Buffer`라는 독립적인 공간을 가진다. 기본적으로 16개 단어 저장 가능

## **cf) String.join**

```java
public static String join(CharSequence delimiter, CharSequence... elements) {
		Objects.requireNonNull(delimiter);
    Objects.requireNonNull(elements);
    
    // Number of elements not likely worth Arrays.stream overhead.
    StringJoiner joiner = new StringJoiner(delimiter);
    for (CharSequence cs: elements) {
        joiner.add(cs);
    }
    
    return joiner.toString();
}
```

- `StringJoiner`를 사용하는데 위 코드와 같이 **구획 문자(Delimiter)**, **prefix**와 **suffix**가 있다.
- `StringBuilder`와 비슷하게 동작하지만, 구분자 넣는 기능이 추가되었다고 생각하면 된다.

## **💡 핵심 정리**

- 성능을 신경 써야 한다면 많은 문자열을 연결할 때는 문자열 연결 연산자(`+`)를 피하자.
- 대신 `StringBuilder`의 `append` 메서드를 사용하자.
- 문자 배열을 사용하거나, 문자열을 연결하지 않고 하나씩 처리하는 방법도 있다.
