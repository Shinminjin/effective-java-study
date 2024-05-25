# Item74 - 메서드가 던지는 모든 예외를 문서화하라

메서드가 던지는 예외는 그 메서드를 올바로 사용하는데 아주 중요한 정보다.

따라서 각 메서드가 던지는 예외 하나하나를 문서화하는데 충분한 시간을 쏟아야한다. - *item 56*

## **검사 예외는 따로따로 선언하고, 반드시 문서화하자.**

> 검사 예외는 항상 따로따로 선언하고, 각 예외가 발생하는 상황을 자바독의  `@throws` 태그를 사용하여 정확히 문서화하자.

**공통 상위 클래스(Exception, Throwable)로 뭉뚱그려 선언하는 일은 삼가하라.**

- 메서드 사용자에게 대처할 힌트를 주지 못한다.
- 같은 맥락에서 발생할 여지가 있는 다른 예외까지 삼켜버려 API 사용성을 크게 떨어뜨린다.

### **잘못된 사용 방법 🙅‍♀️**

```java
// 잘못 선언한 예
public void testMethod() throws Exception {
    // ...
}

public void testMethod() throws Throwable {
    // ...
}
```

### **올바른 사용 방법 🙆‍♀️**

```java
/**
 * @throws IllegalStateException
 */
public void testMethod(String parameter) throws IllegalStateException {
    // ...
}
```

## **비검사 예외도 문서화하자**

- 해당 메서드를 사용하는 개발자가 그 예외를 인지하고, 적절하게 코드를 작성하게 된다.
- 사실상 해당 메서드를 성공적으로 수행하기 위한 전제조건이 된다.
- 공개 API인 public 메서드는 문서화하는 것이 더욱 중요하다.
- 비검사 예외를 인터페이스 메서드에 문서화하는 것은 특히 중요하다.
    - 해당 조건이 인터페이스의 일반 규약에 속하게 되어 그 인터페이스를 구현한 모든 구현체가 일관된 방식으로 동작하게 돕는다.

## **검사예외 vs 비검사예외**

> 메소드가 던질 수 있는 예외를 각각 `@throws` 태그로 문서화하되, 비검사 예외는 메소드 선언의 `throws` 목록에 넣지 말자.

```java
/**
 * @throws SQLException SQL 이 잘못된 경우
 * @throws ClassNotFoundException 지정한 경로에 클래스파일이 존재하지 않는경우.
 * @throws NullPointerException 지정한 요소에 null 이 들어오는 경우
 */
public void foo() throws SQLException, ClassNotFoundException {
    // ...
}
```

- 검사냐 비검사냐에 따라 API 사용자가 해야 할 일이 달라지므로 확실히 구분하라.
- 비검사 예외는 호출자에게 처리를 강제하지 않으므로 `@throws` 태그로만 문서화할 수 있다.
- 자바독 생성시, throws 절에 선언된 예외와 `@throws` 태그에만 명시된 예외는 별도로 표시된다.
    - 따라서 검사 예외와 비검사 예외를 명확하게 구분할 수 있다.

## **한 클래스의 많은 메서드가 같은 이유로 같은 예외를 던진다면**

> 한 클래스의 많은 메서드가 같은 이유로 같은 예외를 던진다면 그 예외를 각각의 메서드가 아닌 클래스 설명에 추가하는 방법을 고려하자.

```java
/**
 * @throws NullPointerException - 이 클래스의 모든 메서드는 인수로 null이 넘어오면 NullPointerExcetpion을 던진다.
 */
class Sample throws NullPointerException {

   public void methodA(String param) {
       if (param == null) {
            throw new NullPointerException();
        }
        // ...
   }

   public void methodB(String param) {
       if (param == null) {
            throw new NullPointerException();
        }
        // ...
   }

   public void methodC(String param) {
       if (param == null) {
            throw new NullPointerException();
        }
        // ...
   }
   // ...
}
```
