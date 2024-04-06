# Item 54 - null이 아닌 빈 컬렉션이나 배열을 반환하라

## **컬렉션이나 배열이 비었을 때 null을 반환하면? 🐛**

```java
  private final List<Cheese> cheesesInStock = ...;

  /**
   * @return 매장 안의 모든 치즈 목록 반환한다.
   *       단, 재고가 하나도 없다면 null 을 반환한다.
   */
  public List<Cheese> getCheeses() {
    return cheesesInStock.isEmpty() ? null : new ArrayList<>(cheesesInStock);
  }
```

- null 상황을 처리하는 로직을 추가로 작성해야 한다.

### **클라이언트 측, 방어코드**

```java
List<Cheese> cheeses = shop.getCheeses();

if (cheeses != null && cheeses.contains(Cheese.STILTON)){ ... } // null 예외 처리
```

- 클라이언트에서 이러한 방어 코드를 빼먹으면 NPE 오류가 발생할 수 있다.
- 이런 추가 로직은 서비스를 복잡하게 만들며, 실수를 일으킨다.

### **위 방식을 쓰는 사람들의 의견**

> 빈 컨테이너(컬렉션, 배열)를 할당하는데도 비용이 드니 null을 반환하는 쪽이 낫다(…?)

**과연 그럴까**❓

### **빈 컬렉션이나 배열을 만드는 것이 문제점이 아닌 이유**

- 성능 저하의 주범이라고 확인되지 않는 이상 이 정도의 성능 차이는 신경 쓸 수준이 못된다.
- 빈 컬렉션과 배열을 굳이 새로 할당하지 않고도 반환할 수 있다.

## **그러므로 null이 아닌 빈 컬렉션이나 배열을 반환하자! 🛠️**

### **컬렉션의 경우**

*빈 컬렉션을 반환하는 방법*
```java
public List<Cheese> getCheeses() {
    return new ArrayList<>(cheesesInStock);
}
```

*빈 컬렉션을 매번 생성하는 것이 성능상 문제가 된다면,*
```java
public List<Cheese> getCheeses() {
    return cheesesInStock.isEmpty() ? Collections.emptyList()
		    : new ArrayList<>(cheesesInStock);
}
```
- `Collections.emptyList()` 을 이용하여 매번 똑같은 빈 불변 컬렉션을 반환하면 된다.
- `Collectionse.emptyList()`, `Collectionse.emptyMap()`, `Collectionse.emptySet()` …

### 배열의 경우

*길이가 0일 수도 있는 배열을 반환하는 방법*
```java
public Cheese[] getCheeses() {
    return cheesesInStock.toArray(new Cheese[0]);
}
```

*빈 배열을 매번 생성하는 것이 성능상 문제가 된다면,*

```java
private static final Cheese [] EMPTY_CHEESE_ARRAY = new Cheese[0];

public Cheese[] getCheeses() {
    return cheesesInStock.toArray(EMPTY_CHEESE_ARRAY);
}
```

- 길이 0인 배열을 미리 선언해두고, 그 배열을 반환하면 된다.
- 단순히 성능 개선할 목적이라면 toArray에 넘기는 배열을 미리 할당하는 건 추천하지 않는다. 오히려 성능이 떨어진다는 연구 결과도 있다.

## **💡 정리**
- null이 아닌, 빈 배열이나 컬렉션을 반환하자.
- null을 반환하는 API는 성능이 좋은 것도 아니고, 오히려 서비스 로직을 복잡하게 만든다.
