# Item 46 - 스트림에서는 부작용 없는 함수를 사용하라

## **스트림**

- 스트림의 패러다임의 핵심은 계산을 일련의 변환(transformation)으로 재구성하는 것이다.
- 각 변환 단계는 가능한 한 이전 단계의 결과를 받아 처리하는  `순수 함수` 여야 한다.

## **순수함수란?**

- 오직 입력만이 결과에 영향을 주는 함수
- 다른 가변상태를 참조 하지 않고, 함수 스스로도 다른 상태를 변경하지 않는다.
- 예를 들어 f(x) = y 함수가 있다면, input 이 x 라면 output 은 y 이외의 값이 나오면 안된다.

순수 함수이기 위해서는 스트림 연산에 건네는 함수 객체는 모두 부작용(side effect)이 없어야한다.

예제를 보면서, 올바른 스트림 패러다임에 대해 알아보자.

## **스트림 패러다임은 이해하지 못한 채 API만 사용한 예시**

```java
Map<String, Long> freq = new HashMap<>();
try(Stream<String> words = new Scanner(file).tokens()) {
  words.forEach(word -> {
    freq.merge(word.toLowerCase(), 1L, Long::sum);
  });
}
```

위 코드는 스트림 코드를 가장한 반복적 코드로, 스트림 API의 이점을 살리지 못하고 있다.

`forEach` 내부에서 외부 상태(`freq`)를 수정하는 람다를 실행하고 있으므로 순수 함수가 아니다.

또한, 같은 기능의 반복문보다 코드가 길고, 읽기 어려우며, 유지보수에도 좋지 않다.

## **스트림 패러다임을 잘 이해한 예시**

```java
Map<String, Long> freq;
try(Stream<String> words = new Scanner(file).tokens()) {
  freq = words.collect(groupingBy(String::toLowerCase, counting()));
}
```

스트림 패러다임을 이해하지 못한 예시와 다르게 외부 상태를 변경하지 않는다.

스트림 연산으로 반환된 결과를 새로운 객체에 할당하므로 항상 동일한 값을 반환함을 보장한다.

그 뿐만 아니라 코드도 짧고 명확하다.

---

## **수집기(Collector)**

- Collector 사용 시, 스트림의 원소를 손쉽게 컬렉션으로 모을 수 있다.
- `java.util.stream.Collectors`  : 자주 사용하는 API 제공
- `Collectors` 의 멤버를 정적 임포트(static import)해 사용하면, 스트림 가독성이 좋아진다.
- 최종 처리(스트림 종료 작업)

https://docs.oracle.com/javase/10/docs/api/java/util/stream/Collectors.html

자주 사용하는 5가지를 알아보자.

```java
List<String> givenList = Arrays.asList("a", "bb", "ccc", "bb");
```

### **toList, toSet**

스트림 연산을 마친 결과값을 List, Set 형태로 반환한다.

```java
List<String> result = givenList.stream().collect(Collectors.toList()); // a, bb, ccc, dd
Set<String> result = givenList.stream().collect(Collectors.toSet()); // a, bb, ccc
```

### **toCollection**

toCollection을 이용해 원하는 형태로 반환할 수 있다.

아래 예시는 스트림 요소를 LinkedList로 변환하고 있다.

```java
givenList.stream().collect(Collectors.toCollection(LinkedList::new));
```

### **toMap**

스트림 요소를 맵 형태로 변경한다.

**keyMapper**, **valueMapper**를 이용하여 처리한다. toMap(keyMapper, valueMapper)

```java
Map<String, Integer> result = givenList.stream().collect(toMap("key", String::length);
```

- key라는 이름의 `key`에 4가지의 elements가 들어가기 때문에 key 충돌이 발생한다.
- 위의 경우는 `IllegalStateException`가 발생한다. (key 중복)

**key 중복 시, 처리 방법**

```java
// 3개의 인수를 받는 toMap
// toMap(keyMapper, valueMapper, (oldVal, newVal) -> newVal)
Map<String, Integer> result = givenList.stream()
  .collect(toMap("key", String::length, (old, new) -> old));
```

- 기존의 값을 사용하겠다고 명시했다.

### **groupingBy**

특정 속성값으로 그룹핑을 짓는다.

```java
Map<Integer, List<String>> result = givenList.stream()
  .collect(groupingBy(String::length, toList()));

  // 1, a
  // 2, bb bb
  // 3, ccc
```

### **joining**

스트림을 문자열로 변환한다.

파라미터로 연결 부분에 들어갈 내용을 정할 수 있다.

```java
String result1 = givenList.stream().collect(joining()); // abbcccdd

// 인수 1개
String result2 = givenList.stream().collect(joining(", ")); // a, bb, ccc, bb

// 인수 3개
String result3 = givenList.stream()
  .collect(joining(" ", "PRE-", "-POST")); // PRE-a bb ccc dd-POST
```

---

## **정리**

- 스트림 파이프라인 프로그래밍의 핵심은 부작용 없는 함수에 있다.
- 스트림뿐 아니라 스트림 관련 객체에 전달되는 모든 함수가 부작용이 없어야 한다.
- forEach는 출력을 하는 용도로만 사용하자.
- 스트림을 올바르게 사용하기 위해서는 수집기(Collectors)를 잘 알아두자.

## 참고

https://colevelup.tistory.com/14
