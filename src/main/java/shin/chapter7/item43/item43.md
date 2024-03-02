# Item43 - 람다보다는 메서드 참조를 사용하라

## 메서드 참조의 장점

람다의 가장 큰 장점은 간결함이다. 그런데 함수객체를 람다보다 더 간결하게 만드는 방법이 있다.

바로 **메서드 참조(Method Reference)다.**

### 자바8 Map merge 메서드
```java
default V merge(K key, V value,
        BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
    Objects.requireNonNull(remappingFunction);
    Objects.requireNonNull(value);
    V oldValue = get(key);
    V newValue = (oldValue == null) ? value :
               remappingFunction.apply(oldValue, value);
    if (newValue == null) {
        remove(key);
    } else {
        put(key, newValue);
    }
    return newValue;
}
```
- 키, 값, 함수를 인수로 받으며, 주어진 키가 맵 안에 없다면 주어진 [키, 값] 쌍을 그대로 저장
- 주어진 키가 맵 안에 있다면 `remappingFunction`의 `apply` 인자로 이전 값과 현재 값을 대입하여 [키, 함수의 결과] 쌍을 저장한다.

```java
// key가 맵 안에 없으면 키와 숫자 1을 매핑하고,
// 이미 있다면 기존 매핑 값을 증가시킨다.
map.merge(key, 1, (count, incr) -> count + incr);
```

람다는 두 인수의 합을 단순히 반환할 뿐이지만, 매개변수인 `count`와 `incr` 는 크게 하는 일 없이 공간을 차지하고 있다.

자바8부터 `Integer` 클래스는 람다와 기능이 같은 정적 메서드 `sum` 을 제공하기 시작했다.

```java
public final class Integer extends Number
    implements Comparable<Integer>, Constable, ConstantDesc {
    
    ...
    public static int sum(int a, int b) {
        return a + b;
    }
}
```

merge 메서드 매개변수에 람다대신 메서드 참조를 전달하면, 똑같은 결과를 더 보기 좋게 표현가능하다.

```java
// 정적 메서드 참조
map.merge(key, 1, Integer::sum);
```

## 메서드 참조 사용 시 주의사항

메서드 참조를 사용하면 대부분 코드가 간결해지고 명확해지지만, 그렇지 않은 경우도 있다.

**람다에서 사용하는 매개변수 이름이 좋은 가이드가 될 경우, 코드의 길이가 더 길어도 메서드 참조보다 읽기 쉽고 유지보수도 쉬울 수 있다.**

아래 예시의 경우, 람다를 사용한 경우가 좀 더 읽기 쉽고 명확해 더 좋은 코드인것 같다.

```java
Consumer<String> printString1 = (str) -> System.out.println(str);  // 람다 사용
Consumer<String> printString2 = System.out::println;               // 메서드 참조
```

**메서드 참조에 사용하는 클래스 이름이 길다면 오히려 람다가 더 간결하게 표현될 수 있다.**

예를 들어 `GoshThisClassNameIsHumongous` 라는 클래스에 `action` 메서드를 참조한다고 해보자.

```java
// 메서드 참조
service.execute(GoshThisClassNameIsHumongous::action);
```

너무 긴 이름 때문에 오히려 가독성이 떨어진다.

이를 람다로 표현하면 훨씬 간결해진다.

```java
// 람다
service.execute(() -> action());
```

### 결론
따라서 람다와 메서드 참조를 선택할 때, **메서드 참조 쪽이 짧고 명확한 표현을 할 수 있다면 메서드 참조를 쓰고, 그렇지 않을 때만 람다를 사용하면 된다.**

## 메서드 참조의 유형

| 메서드 참조 유형 | 예 | 같은 기능을 하는 람다 |
| --- | --- | --- |
| 정적 | Integer::parseInt | str → Integer.parseInt(str) |
| 한정적(인스턴스) | Instant.now()::isAfter | Instant then = Instant.now(); t -> then.isAfter(t) |
| 비한정적(인스턴스) | String::toLowerCase | str -> str.toLowerCase() |
| 클래스 생성자 | TreeMap<K,V>::new | () -> new TreeMap<K,V>() |
| 배열 생성자 | int[]::new | len -> new int[len] |
- 한정적 (인스턴스) : 수신 객체(참조 대상 인스턴스)를 특정하는 한정적 인스턴스 메서드 참조
- 비한정적 (인스턴스) : 수신객체를 특정하지 않음

## 한정적 메서드 참조와 비한정적 메서드 참조의 차이점

### 1) 비한정적 메서드 참조

```java
// 비한정적 메서드 참조
// ClassName::method
String::toUpperCase
```

- 비한정적(unbound) 메서드 참조 시, 정적 메서드 참조처럼 매개변수의 클래스 타입명을 기재합니다.
- 비한정적이라는 표현은 특정한 객체를 참조하기 위한 변수를 지정하지 않는다는 의미이다.
- String 클래스의 toUpperCase 메서드는 정적메서드가 아닌 인스턴스 메서드이므로 반드시 String 클래스가 객체화되어야만 호출가능하다.

```java
// 람다로 표현
(String str) -> str.toUpperCase()
```

- 람다로 내용을 풀어서 보면, 객체의 생성을 매개변수로 받는다.
- 람다 표현 식 내부에서 객체 생성이 일어났기 때문에 객체를 참조할만한 변수가 외부에 존재하지 않는다.

```java
public class MethodReferencesExamples {
		
    public static <T> T mergeThings(T a, T b, BiFunction<T, T, T> merger) {
        return merger.apply(a, b);
    }

    public static void main(String[] args) {

        System.out.println(MethodReferencesExamples.
                mergeThings("Hello ", "World!", (String a, String b) -> a.concat(b)));
				
        System.out.println(MethodReferencesExamples.
                mergeThings("Hello ", "World!", String::concat));
    }
}
```

- 처리해야 하는 데이터가 여러개라면 조금 복잡해질 수 있다.
- 문자열 2개를 합치는 방식을 작성하면 다음과 같다. 함축적인 편이라 이해하고 해석하는데 어려울 수 있다.

### 2) 한정적 메서드 참조

- 한정적(bound)이라는 단어를 사용한 이유는 메서드 참조 시 특정 객체의 변수로 제한되기 때문이다.

```java
Calendar.getInstance()::getTime

Calendar cal = Calendar.getInstance();  // 객체 생성
() -> cal.getTime()  // 람다

Calendar cal = Calendar.getInstance();  // 객체 생성
cal::getTime  // 메서드 참조 구문. cal 변수를 참조
```

### 3) 정리

한정적 메서드 참조는 외부에서 정의한 객체의 메서드를 참조할 때 사용하며, 비한정적 메서드 참조는 람다 표현식 내부에서 생성한 객체의 메서드를 참조할 때 사용한다.

### 참고
https://docs.oracle.com/javase/tutorial/java/javaOO/methodreferences.html
