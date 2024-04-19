# Item 59 - 라이브러리를 익히고 사용하라

## **라이브러리에 미숙할 때 🕶️**

### **무작위 정수 생성 예제**

```java
public class RandomTest {
		// 흔하지만 문제가 심각한 코드!
    static Random rnd = new Random();
    static int random(int n){
        return Math.abs(rnd.nextInt()) % n;
    }

    public static void main(String[] args) {
        int n = 2 * (Integer.MAX_VALUE / 3);
        int low = 0;
        for (int i = 0; i < 1000000; ++i)
            if (random(n) < n/2)
                low++;
        System.out.println(low);
    }
}
```
위 코드는 세 가지 문제를 내포하고 있다.

1. n이 그리 크지 않은 2의 제곱수라면 얼마 지나지 않아 같은 수열이 반복된다.
2. n이 2의 제곱수가 아니라면 몇몇 숫자가 평균적으로 더 자주 반환 된다.
    - random 메서드가 이상적으로 동작한다면 low의 값이 대략 50만으로 나와야 하지만, 66만에 가까운 값을 얻는다. → 2/3가량이 중간값보다 낮은 쪽으로 쏠려있다.
3. 지정한 범위 바깥의 수가 튀어나올 수 있는 조건이 있다.
    - `Math.abs(Integer.MIN_VALUE)`는 정수 오버플로우 현상 때문에 -2,147,483,648이 반환된다.
    - 즉, `rnd.nextInt()`가 `Integer.MIN_VALUE`를 반환하면 `Math.abs(rnd.nextInt()) % n`은 음수가 반환된다.

### **해결 방법 🔨**

- 이미 잘 만들어 놓은 `Random.nextInt(int bound)` 메소드를 사용하면 직접 해결할 필요가 없다.
    - 자바 7부터는 `Random` 대신 `ThreadLocalRandom`을 사용하자.
    - 포크-조인 풀이나 병렬 스트림에서는 `SplittableRandom`을 사용하자.

## **표준 라이브러리를 사용해야 하는 이유**

- 전문가의 지식과 다른 프로그래머들의 경험을 활용할 수 있다.
- 핵심적인 일과 크게 관련 없는 문제를 해결하느라 시간을 허비하지 않아도 된다
- 따로 노력하지 않아도 성능이 지속해서 개선된다
- 패치를 거듭할수록 기능이 점점 많아진다.
- 많은 개발자들에게 낯익은 코드가 되어 협업 하기 좋다.

## **몰라서 못쓰지는 말자**

- 메이저 릴리즈마다 라이브러리에 많은 기능이 추가된다.
- 새로운 기능을 찾아보자.
    - [Consolidated JDK 8 Release Notes](https://www.oracle.com/java/technologies/javase/8all-relnotes.html#JSERN108), [Consolidated JDK 11 Release Notes](https://www.oracle.com/java/technologies/javase/11all-relnotes.html#JSERN11)
    - https://spring.io/blog

## **꼭 알아야 하는 라이브러리**

### **표준 라이브러리**

- `java.lang`
- `java.util`
- `java.io`
- `Collection framework`
- `Stream`
- `java.util.concurrent`

### **고품질 3rd Party 라이브러리**

- **Guava**: https://github.com/google/guava

[The Guava library: What are its most useful and/or hidden features?](https://stackoverflow.com/questions/3759440/the-guava-library-what-are-its-most-useful-and-or-hidden-features)

- **Apache Commons Lang**: https://github.com/apache/commons-lang

[An Introduction to Apache Commons Lang 3 | Baeldung](https://www.baeldung.com/java-commons-lang-3)

[Apache Commons Lang StringUtils - DZone Java](https://dzone.com/articles/apache-commons-lang)

https://www.baeldung.com/apache-commons-collections-vs-guava

## **💡 핵심 정리**
- 바퀴를 다시 발명하지 말자.
- 아주 특별한 기능이 아니라면 누군가 이미 라이브러리 형태로 구현해 놓았을 가능성이 크다.
- 개발하기보다는 라이브러리를 찾아보자.
