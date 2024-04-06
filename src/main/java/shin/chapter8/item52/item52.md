# Item 52 - 다중정의는 신중히 사용하라

## **다중정의(Overloading)의 경우**

```java
public class CollectionClassifier {
    public static String classify(Set<?> s) {
        return "집합";
    }

    public static String classify(List<?> lst) {
        return "리스트";
    }

    public static String classify(Collection<?> c) {
        return "그 외";
    }

    public static void main(String[] args) {
        Collection<?>[] collections = {
                new HashSet<String>(),
                new ArrayList<BigInteger>(),
                new HashMap<String, String>().values()
        };

        for (Collection<?> c : collections)
            System.out.println(classify(c));
    }
}
```
**output**

```bash
예상 : 집합, 리스트, 그 외
실제 결과 : 그 외, 그 외, 그 외
```

- 예상과 다르게 “그 외”만 3번 출력된다.
- 다중정의된 3개의 `classify` 중 어느 메서드를 호출할지는 **컴파일타임에 정해진다.**
    - 컴파일타임에는 for문 안의 c는 항상 Collection<?> 타입이다.
    - 런타임에는 타입이 매번 달라지지만, 호출할 메서드를 선택하는 데는 영향을 주지 못한다.
    - 그로 인해, `classify(Collection<?> c)`만 계속 호출된다.

### **원래 의도대로 동작하게 하려면? 🛠️**

```java
public static String classify(Collection<?> c) {
    return c instanceof Set ? "집합" :
            c instanceof List ? "리스트" : "그 외";
}
```

- 메서드를 하나로 합친 후 `instanceof`를 활용해 명시적으로 검사를 수행하면 된다.

## **재정의(Overriding)의 경우**

```java
class Wine {
    String name() { return "포도주"; }
}

class SparklingWine extends Wine {
    @Override String name() { return "발포성 포도주"; }
}

class Champagne extends SparklingWine {
    @Override String name() { return "샴페인"; }
}

public class Overriding {
    public static void main(String[] args) {
        List<Wine> wineList = List.of(
                new Wine(), new SparklingWine(), new Champagne());

        for (Wine wine : wineList)
            System.out.println(wine.name());
    }
}
```

**output**

```bash
포도주
발포성 포도주
샴페인
```

- “포도주”, “발포성 포도주”, “샴페인”을 차례로 출력한다.
- 컴파일타임 타입(`Wine`)과 무관하게 ‘가장 하위에서 정의한’ 재정의 메서드가 실행된다.
- 즉, **런타임에 정해진 타입에서 재정의된 메서드가 실행된다.**

## **다중정의가 혼돈을 일으키는 상황은 피하자**

- 매개변수를 넘길 때, 어떤 다중정의 메서드가 호출될지 모른다면 프로그램은 오작동하기 쉽다.
- 헷갈릴 수 있는 코드는 작성하지 말자. (위의 다중정의 예시처럼)
- 안전하고 보수적으로 가려면 **매개변수 수가 같은 다중정의는 만들지 말자.**
- 가변 인수를 매개변수로 사용한다면 다중정의는 사용하면 안 된다.

이 규칙들만 잘 따르면 다중정의가 혼동을 일으키는 상황을 피할 수 있다.

이 외에 다중정의하는 대신 메서드 이름을 다르게 지어주는 길도 존재한다.

### **매개변수 개수가 같은 다중정의를 피할 수 없다면? 🧐**

- 매개변수 중 하나 이상이 “**근본적으로 다르다면”** 헷갈릴 일이 없다.
- 근본적으로 다르다는 것은 **두 타입의 값을 어느 쪽으로든 형변환할 수 없다는 뜻**이다.
- ex) ArrayList의 인자가 1개인 생성자
    - `ArrayList(int initialCapacity)`, `ArrayList(Collection<? extends E> c)`
    - `int`와 `Collection`은 근본적으로 다르므로 헷갈릴 일이 없다.

### **다중정의의 함정 1 : 오토박싱 🕸️**

**오토박싱이란?**

- 자바 5부터 도입되었다.
- 기본 데이터 타입의 값을 해당하는 Wrapper 클래스 객체로 자동 변환하는 것을 말한다.

```java
List<Integer> list = Arrays.asList(-3, -2, -1, 0, 1, 2);
list.remove(1); // 1을 지우라는 것일까? index 1번째 원소를 지우라는 것일까?
```

- 혼란스러운 이유는 `List<E>`인터페이스가 `remove(int index)`와 `remove(Object o)`를 다중정의했기 때문이다.
- 제네릭과 오토박싱이 등장하면서 `int`를 `Integer`로 자동 변환해주므로 `int`와 `Object`가 **근본적으로 다르지 않게 되었다. (서로 형변환 가능)**

### **다중정의의 함정 2 : 람다와 메서드 참조🕸️**

```java
ExecutorService exec = Executors.newCachedThreadPool();
exec.submit(System.out::println); // 컴파일 오류
```

- `ExecutorService`의 `submit` 메서드의 경우, 매개변수로 `Callable`을 받는 메서드와 `Runnable`을 받는 메서드가 다중정의 되어있다.
- 모든 `println`이 `void`를 반환하니 반환값이 있는 `Callable`과 헷갈릴 리가 없다고 추론할 수 있겠지만 다중정의 메소드를 찾는 알고리즘은 이렇게 동작하지 않는다.
- 핵심은 서로 다른 함수형 인터페이스라도 인수 위치가 같으면 혼란이 생긴다.

  → 같은 위치 인수로 받지 말자.

- 즉, 서로 다른 함수형 인터페이스라도 **서로 근본적으로 다르지 않다.**

### **다중정의의 함정 피하기 : 인수 포워드 ✨**

```java
// 인수 포워드
public boolean contentEquals(StringBuffer sb) {
    return contentEquals((CharSequence)sb);
}

public boolean contentEquals(CharSequence cs) {
		...
}
```

- 다중정의로 2개 이상의 타입을 지원할 때, 위와 같이 명시적 캐스팅으로 인수를 포워딩하여 정상동작을 유도할 수 있다.
- 이처럼 어떤 다중정의 메서드가 호출되는지 몰라도 기능이 똑같다면 신경 쓸 필요가 없어진다.

### **String 클래스의 다중정의 오류 🤔**

```java
public static String valueOf(Object obj) {
    return (obj == null) ? "null" : obj.toString();
}

public static String valueOf(char data[]) {
    return new String(data);
}
```

- 같은 객체를 건네더라도 전혀 다른 일을 수행한다.
- 이렇게 할 이유가 없었음에도, 혼란을 불러올 수 있는 잘못 설계된 사례로 남아있다.

## **💡 정리**

- 다중정의를 허용한다고 해서 남발하지 말자.
- 매개변수 수가 같은 다중정의는 웬만하면 피하자.
- 매개변수 수가 같은데도 불구하고 다중정의를 꼭 사용해야 한다면, 오동작을 방지하기 위해 다중정의한 메서드들이 모두 동일하게 동작하도록 처리하자.
- 위 원칙을 지키지 못할 경우 다중정의된 메서드나 생성자를 효과적으로 사용하지 못할 것이고, 의도대로 동작하지 않은 이유를 디버깅하는데 굉장히 오랜 시간이 걸릴 것이다.
