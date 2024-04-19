# Item 57 - 지역변수의 범위를 최소화 하라

> item 15의 '클래스와 멤버의 접근 권한을 최소화하라'와 취지가 비슷하다.

지역변수의 유효 범위를 최소로 줄이면 코드 가독성과 유지보수성이 높아지고 오류 가능성은 낮아진다.

## **지역변수의 범위를 줄이는 방법 🚀**

### **(1) 가장 처음 쓰일 때 선언하기**

- 사용하는 시점보다 훨씬 이전에 미리 선언하면 가독성이 떨어지고, 실제로 사용하는 시점엔 타입과 초깃값이 기억나지 않을 수 있다.
- 변수의 스코프를 고려하지 않고 마구잡이식으로 선언하면 의도한 범위 앞이나 뒤에서 해당 변수를 사용할 때 문제가 발생할 수 있다.

### **(2) 가능한한 지역변수는 선언과 동시에 초기화하기**

- 초기화에 필요한 정보가 충분하지 않다면 정보가 충분해질 때까지 선언을 미뤄야 한다.
- 단 `try-catch`의 경우 `try` 블록 안의 변수를 블록 바깥에서도 쓸 확률이 높으니 예외다.

### **(3) while 문 대신 for 문(또는 for-each 문) 사용하기**

- fof 문(또는 for-each 문)을 사용하면 반복 변수의 범위가 반복문의 범위로 제한된다.
- while 문을 사용하는 경우에는 반복 변수의 범위가 더 넓어지므로 for 문을 사용하는 것이 좋다.


- 아래 while 문 예시는 의도치 않게 반복자 `i`의 `hasNext()` 메소드를 호출하여 잘못 동작한다.
```java
// while을 사용할 경우
Iterator<Element> i = c.iterator();
while(i.hasNext()) {
    doSomething(i.next());
}

Iterator<Element> i2 = c.iterator();
while(i.hasNext()) {  // 문제 발생, 런타임에 코드가 동작하면서 버그를 발견할 수 있다.
    doSomething(i2.next());
}
```
```java
// for을 사용할 경우
for (Iterator<Element> i = c.iterator(); i.hasNext(); ) { 
	Element e = i.next();
    ...
}

// "i를 찾을 수 없다"는 컴파일 에러를 발생시킨다.
for (Iterator<Element> i2 = c.iterator(); i.hasNext(); ) { 
	Element e = i2.next();;
    ...
}
```

### **반복문 꿀팁 🍯**

- 반복자를 사용해야 하는 경우, for-each 문보다 for 문이 낫다.
```java
for (Element e : c) {
    // e로 무언가를 반복한다.
}

for(Iterator<Element> i = c.iterator(); i.hasNext();) {
    Element e = i.next(); //e와 i로 무언가를 반복한다.
}
```

- for 문의 초기화 부분은 한 번만 실행되므로 계산 비용이 큰 표현식을 다룰 때 유용하다.
```java
for (int i = 0; i < expensiveComputation(); i++) {
  // do something...
}

// n에 한 번만 계산되어 계산 비용을 절약
for (int i = 0, n = expensiveComputation(); i < n; i++) {
  // do something...
}
```

### **(4) 메서드를 작게 유지하고 한 가지 기능에 집중하기**

- 한 메소드가 여러 기능을 처리한다면 관련 없는 코드에서도 변수에 접근할 수 있게 된다.
- 따라서 기능별로 쪼개어 한 가지 기능에만 집중하도록 하자.
