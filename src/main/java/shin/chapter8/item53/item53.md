# Item 53 - 가변인수는 신중히 사용해라

## **가변인수(varargs) 메서드**

- 명시한 타입의 인수를 **0개 이상** 받을 수 있다.
- 가변인수 메서드 호출 시, 가장 먼저 인수의 개수와 길이가 같은 배열을 만들고 인수들을 이 배열에 저장하여 가변인수 메서드에 건네준다.

## **사용법 1 - 필수 인수를 지정하라**

### **잘못 구현한 예시** 🐛

```java
static int min(int... args) {
    if (args.length == 0) 
        throw new IllegalArgumentException("인수가 1개 이상 필요합니다.");
    int min = args[0];
    for (int i = 1; i < args.length; i++)
        if (args[i] < min)
            min = args[i];
    return min;
}
```
- 최솟값을 찾는 메서드인데, 인수를 0개 받을 수 있도록 설계하는 건 좋지 않다.
    - 인수를 0개만 넣어 호출하면 (컴파일타임이 아닌) 런타임에 실패한다.
    - 코드도 지저분하다.
- args 유효성 검사를 명시적으로 해야 한다.

### **제대로 사용하는 방법** **🛠️**
필수 인수를 지정하자!

```java
static int min(int firstArg, int... remainingArgs) {
    int min = firstArg;
    for (int i = 1; i < args.length; i++)
        if (args[i] < min)
            min = args[i];
    return min;
}
```
- 메서드에 전달되는 인수의 개수가 0개가 되는 것을 방지할 수 있다. - *컴파일에러 발생*
- 불필요한 예외 처리를 하지 않아도 된다.
- `for-each` 문이 쓰여 코드가 깔끔하다.

## **사용법 2 - 성능에 민감한 경우, 다중 정의와 함께**

- 가변인수 메서드는 호출될 때마다 배열을 새로 하나 할당하고 초기화 한다.
- 성능에 민감한 상황에서는 걸림돌이 될 수 있다.

이 경우, **다중 정의**를 통해 해결할 수 있다.

```java
public void foo() { }
public void foo(int a1) { }
public void foo(int a1, int a2) { }
public void foo(int a1, int a2, int a3) { }
public void foo(int a1, int a2, int a3, int... rest) { }
```
- 메서드 호출 중 95%가 인수를 3개까지만 쓴다면, 위와 같은 방법으로 최적화가 이루어진다.
- 인수 3개까지는 배열을 새로 할당하고 초기화하는 과정이 없다.
- `EnumSet` 의 정적 팩터리 메서드도 이와 같은 원리로 최적화를 진행하고 있다.

## **💡 정리**

- 인수 개수가 일정하지 않은 메서드를 정의할 때는 가변인수를 사용하라.
- 필수 인수가 있다면, 가변인수와 함께 선언하자.
- 성능 문제가 있을 경우, 다중 정의와 함께 사용할 수 있다.
