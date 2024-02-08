# Item 41 - 정의하려는 것이 타입이라면 마커 인터페이스를 사용하라

## 마커 인터페이스

- 아무 메서드도 선언하지 않은 인터페이스이다.
- 자바의 대표적인 마커 인터페이스로는 `Serializable`, `Cloneable`, `Remote` 등이 있다.
- 대부분의 경우, 마커 인터페이스를 단순한 타입 체크를 하기 위해 사용한다.

### Serializable 인터페이스를 활용한 예시를 통해 마커 인터페이스를 이해해보자.

```java
package java.io;

public interface Serializable {
}
```

`Serializable` 인터페이스를 구현한 클래스는 `ObjectOutputStream` 을 통해 직렬화 할 수 있다.

```java
import org.junit.jupiter.api.Test;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

public class SerializableTest {

    record NotSerializableMelon(String type, int weight) {}

    @Test
    void serializableTest() throws IOException {

        NotSerializableMelon melon = new NotSerializableMelon("머스크", 3000);

        FileOutputStream fileOut = new FileOutputStream("melon.txt");

        // Creates an ObjectOutputStream
        ObjectOutputStream objOut = new ObjectOutputStream(fileOut);

        objOut.writeObject(melon);
    }
}
```

```bash
java.io.NotSerializableException: shin.chapter6.item41.SerializableTest$NotSerializableMelon

	at java.base/java.io.ObjectOutputStream.writeObject0(ObjectOutputStream.java:1197)
	at java.base/java.io.ObjectOutputStream.writeObject(ObjectOutputStream.java:354)
```

Serializable을 구현하지 않은 객체를 직렬화하려고 하면 다음과 같이 오류가 발생한다.
오류가 발생한 ObjectOutputStream.writeObject0의 1197번째 라인을 보면 Serializable을 구현하지 않은 경우, 
NotSerializableException 예외가 발생한다.

```java
private void writeObject0(Object obj, boolean unshared) throws IOException {
    ...
    if (obj instanceof String) {
        writeString((String) obj, unshared);
    } else if (cl.isArray()) {
        writeArray(obj, desc, unshared);
    } else if (obj instanceof Enum) {
        writeEnum((Enum<?>) obj, desc, unshared);
    } else if (obj instanceof Serializable) {
        writeOrdinaryObject(obj, desc, unshared);
    } else {
        if (extendedDebugInfo) {
            throw new NotSerializableException(cl.getName() + "\n" + debugInfoStack.toString());
        } else {
            throw new NotSerializableException(cl.getName());
        }
    }
}
```
Serializable이 구현되었는지 타입 확인 정도만 하고 있는 것을 확인할 수 있다

---
## 마커 인터페이스 vs 마커 애노테이션

### 마커 인터페이스 장점

1. 마커 인터페이스는 이를 구현한 클래스의 인스턴스들을 구분하는 타입으로 쓸 수 있다.
    - 마커 애너테이션은 타입으로 사용할 수 없다.
    - 타입으로 사용할 수 있기 때문에 런타임에야 발견할만한 오류를 컴파일 타임에 발견할 수도 있다.
    - 예시로 든 ObjectOutputStream.writeObject 메서드는 Object 객체를 받도록 설계되어 있어, 직렬화 할 수 없는 객체를 넘겨도 런타임시에야 문제를 확인한다. 
      이는 마커 인터페이스의 장점을 살리지 못한 케이스다.


2. 적용 대상을 좀 더 정밀하게 지정할 수 있다.
    - 마커 애노테이션의 경우, 적용대상(@Target)을 ElementType.Type 으로 선언할 경우, 모든 타입(클래스, 인터페이스, 열거타입, 애너테이션)에 달 수 있다. 
      
      → 부착할 수 있는 타입을 더 세밀하게 제한하지는 못한다.
    
    - 마커 인터페이스의 경우, 마킹하고 싶은 특정 클래스에서만 인터페이스를 구현하도록 하여  적용 대상을 더 정밀하게 지정할 수 있다.


3. 다형성(Polymorphism)의 개념을 사용할 수 있다.

   예를 들어 Coffee 클래스를 상속받고 있는 클래스가 여러개 있다.

    ```java
    class Coffee {
        // ..some fields
    }
    
    class Americano extends Coffee {}
    class CaffeLatte extends Coffee {}
    class Cappuccino extends Coffee {}
    class Espressso extends Coffee {}
    ```

   만약, Coffee를 상속 받는 클래스가 모두 Serializable 되길 원한다면

   Coffee 클래스가 Serializable 인터페이스를 구현하도록 한다.

    ```java
    class Coffee implements Serializable {
    		// ..some fields
    }
    ```

   마킹된 타입이 자동으로 그 인터페이스의 하위타입임을 보장한다.

   마커 애너테이션으로 작성했다면, 아래 코드처럼 일일이 하위 클래스에 마커 애노테이션을  추가해줘야 한다.

    ```java
    @Serializable
    class Americano extends Coffee{}
    
    @Serializable
    class CaffeLatte extends Coffee {}
    
    @Serializable
    class Cappuccino extends Coffee {}
    
    @Serializable
    class Espressso extends Coffee {}
    ```


### 마커 애노테이션의 장점

- 거대한 애너테이션 시스템의 지원을 받는다.
    - 애너테이션을 적극적으로 사용하는 프레임워크에서는 마커 애너테이션을 쓰는 것이 일관성을 지키는데 유리하다.
---
## 내용정리

- 마커 애너테이션  사용
    - 클래스, 인터페이스 외 프로그램 요소(모듈, 패키지, 필드, 지역변수 등)에 마킹해야 하는 경우
    - 애노테이션을 적극적으로 사용하는 프레임워크
- 마커 인터페이스 사용
    - 마킹된 객체를 매개변수로 받는 메서드를 작성할 때

      (마커 인터페이스를 해당 메서드의 매개변수 타입으로 사용하면 컴파일타임에 오류 발생)

    - 새로 추가하는 메서드 없이 단지 타입 정의가 목적인 경우

## 참고

https://www.scaler.com/topics/marker-interface-in-java/
