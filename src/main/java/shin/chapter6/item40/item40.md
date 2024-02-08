# Item 40 - @Override 애너테이션을 일관되게 사용하라

## java.lang.Override 애너테이션

- `@Override` 애너테이션은 **상위 타입의 메서드를 재정의 할 때 사용**한다.
- `@Override` 를 일관되게 사용하면, **여러가지 버그를 예방**할 수 있다.

```java
import java.lang.annotation.*;

/**
 * Indicates that a method declaration is intended to override a
 * method declaration in a supertype. If a method is annotated with
 * this annotation type compilers are required to generate an error
 * message unless at least one of the following conditions hold:
 *
 * The method does override or implement a method declared in a supertype.
 * 
 * The method has a signature that is override-equivalent to that of
 * any public method declared in Object.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.SOURCE)
public @interface Override {
}
```

### `@Override`의 메타 애너테이션

- @Target(ElementType.METHOD): 메서드 선언 시에만 달 수 있음
- @RetentionPolicy(RetentionPolicy.SOURCE) : 소스코드(.java) 구간까지 유지. 컴파일 과정에서 컴파일러에 의해 애노테이션 정보 사라짐

---
### `@Override`를 사용하지 않을 때 마주할 수 있는 버그를 코드로 이해해보자.


- Object.equals와 Object.hashCode 메서드를 재정의한 클래스 예시

```java
public class Bigram {
    private final char first;
    private final char second;

    public Bigram(char first, char second) {
        this.first = first;
        this.second = second;
    }

    public boolean equals(Bigram b) {
        return b.first == first && b.second == second;
    }

    public int hashCode() {
        return 31 * first + second;
    }

    public static void main(String[] args) {
        Set<Bigram> s = new HashSet<>();
        for (int i = 0; i < 10; i++)
            for (char ch = 'a'; ch <= 'z'; ch++)
                s.add(new Bigram(ch, ch));
        System.out.println(s.size());
    }
}
```

Set은 중복을 허용하지 않으므로 s.size()가 26이 출력될 것 같지만, 실제로는 260이 출력된다.

그 이유는 equals 메서드를 재정의(Overriding)한게 아니라 **다중정의(Overloading)** 했기 때문이다.

이러한 오류를 컴파일러가 찾게 하려면,

`@Override` 애노테이션을 추가해 Object.equals 를 재정의 하려는 의도를 명확하게 나타내야한다.

```java
public class Bigram {
    private final char first;
    private final char second;

    public Bigram(char first, char second) {
        this.first = first;
        this.second = second;
    }

    @Override public boolean equals(Bigram b) {
        return b.first == first && b.second == second;
    }

    @Override public int hashCode() {
        return 31 * first + second;
    }

    public static void main(String[] args) {
        Set<Bigram> s = new HashSet<>();
        for (int i = 0; i < 10; i++)
            for (char ch = 'a'; ch <= 'z'; ch++)
                s.add(new Bigram(ch, ch));
        System.out.println(s.size());
    }
}
```

**Compile Output**

```bash
**error: method does not override or implement a method from a supertype**
```

`@Override`를 붙일 경우, 컴파일러는 해당 메서드가 상위 메서드와 다르다는 것을 찾아 오류가 발생되며, 올바르게 수정 할 수 있다.

즉, **상위 클래스의 메서드를 재정의하는 모든 메서드에 `@Override` 다는 것을 권장한다.** 
단, 상위 클래스의 추상 메서드는 굳이 `@Override`를 달지 않아도 된다.

---
### `@Override` 애노테이션 정리

- 클래스, 인터페이스의 메서드를 재정의 할 때 사용한다.
- 시그니처가 올바른지 재차 확인 가능하다.
- 컴파일 타임에 오류 발견 가능하다.
- 추상 클래스나 인터페이스에서는 상위 클래스, 상위 인터페이스의 메서드를 재정의한 모든 메서드에 추가하는 것을 권장한다.
- 추상 메서드는 굳이 애노테이션을 달지 않아도 된다.
