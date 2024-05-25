# Item76 - 가능한 한 실패 원자적으로 만들라

## **실패 원자성(Failure Atomicity)**

> 호출된 메소드가 실패하더라도 해당 객체는 메서드 호출 전 상태를 유지해야 한다.

- 위와 같은 특성을 실패 원자성이라고 한다.
- 실패 원자성은 시스템의 안정성, 성능, 신뢰성을 향상시키는데 필수적이다.

## **메서드 실패 원자적으로 만드는 방법**

### **방법 1 : 불변 객체로 설계하기 - *item17***

```java
public class String {
    ...
    public String substring(int beginIndex, int endIndex) {
        int length = length();
        checkBoundsBeginEnd(beginIndex, endIndex, length);
        int subLen = endIndex - beginIndex;
        if (beginIndex == 0 && endIndex == length) {
            return this;
        }
        return isLatin1() ? StringLatin1.newString(value, beginIndex, subLen)
                          : StringUTF16.newString(value, beginIndex, subLen);
    }
    ...
}
```
- 불변 객체의 상태는 생성 이후 절대 변하지 않으므로 자연스럽게 실패 원자적이다.
- 즉, 기존 객체가 불안정한 상태에 빠지는 일은 생기지 않는다.

### **방법 2 : 매개변수 유효성 검사 - *item 49***

작업 수행에 앞서 매개변수의 유효성을 체크한다.

```java
public Object pop() {
    if (size == 0)
        throw new EmptyStackException();
    
    Object result = elements[--size];
    elements[size] = null; // 다 쓴 참조 해제
    return result;
}
```
- 객체 내부 상태가 변하기 전에 잠재적 예외 가능성을 대부분 걸러낸다.

```java
public void add(String key, Object value) {
    if (!(value instanceof Integer))
        throw new ClassCastException(value.toString());
    map.put(key, (Integer) value);
}
```
- 실패할 가능성이 있는 모든 코드를, 객체의 상태를 바꾸는 코드보다 앞에 배치하는 방법
- TreeMap에 원소를 추가하기 전에 정렬을 위한 '**어떤 기준**'에 적합한지 검사하여, 엉뚱한 타입의 원소라면 ClassCastException을 던지게 한다.

### **방법 3 : 임시 구조 사용**

객체의 임시 복사본에서 작업한 이후, 작업이 성공적으로 완료되면 원래 객체와 교체한다.

```java
default void sort(Comparator<? super E> c) {
    Object[] a = this.toArray(); // 배열로 변환 
    Arrays.sort(a, (Comparator) c);
    ListIterator<E> i = this.listIterator();
    for (Object e : a) {
        i.next();
        i.set((E) e);
    } // 성공적으로 완료되면 원래 객체와 교체한다
}
```
- 데이터를 임시 자료구조에 저장해 작업하는게 더 빠르다면 적용하기 좋은 방식이다.
- 정렬 시 배열을 사용하면 참조 지역성이 높아져 반복문에서의 원소 접근이 훨씬 빨라진다.
- 정렬에 실패하더라도 입력 스트림은 변하지 않는 효과도 얻을 수 있다.

### **방법 4 : 복구 코드**

- 작업 도중 발생하는 실패를 가로채는 복구코드를 작성하여 작업 전 상태로 되돌리는 방법
- 주로 디스크 기반의 내구성(durability)을 보장해야 하는 자료구조에 쓰인다.
- 자주 쓰이는 방법은 아니다.

## **실패 원자성 보장이 힘든 경우 🤔**

실패 원자성은 권장되는 덕목이지만 항상 달성할 수 있는 것은 아니다.

또한 실패 원자적으로 만들 수 있다고 하더라도 항상 그리해야 하는 건 아니다.

**예시 1 : `ConcurrentModificationException`**

- 두 스레드가 동기화 없이 같은 같은 객체를 동시에 수정하려고 할 때 발생한다.
- 이 경우,  `ConcurrentModificationException`을 잡아냈다고 해서 그 객체가 여전히 쓸 수 있는 상태라고 가정하면 안된다.

**예시 2 : `Error`, `AssertionError`**

- 시스템 레벨의 문제는 일반적인 방법으로 복구가 불가능하다.
- 따라서 실패 원자성을 보장하려는 시도는 의미가 없다.

> 실패 원자성을 지키지 못한다면, 실패 시 객체 상태를 API 설명에 명시해야 한다.
