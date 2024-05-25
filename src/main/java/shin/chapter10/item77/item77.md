# Item77 - 예외를 무시하지 말라

API 설계자가 메서드 선언에 예외를 명시하는 까닭은,

해당 메서드를 사용할 때 적절한 조치를 취해달라고 말하는 것이다.

## **예외를 무시하는 방법 😒**

```java
// catch 블록을 비워두면 예외가 무시된다. 아주 의심스러운 코드다!
try {
		...
} catch (SomeException e) {
		
}
```
- 해당 메서드 호출을 try 문으로 감싼 후 catch 블록에서 아무 일도 하지 않는다.

> catch 블록을 비워두지 말자.

- catch 블록을 비워두면 예외가 존재할 이유가 없어진다. (예외의 존재 이유 : 문제 상황 대처)
    - 화재 경보를 무시하는 수준이 아니라 그냥 꺼버린 거나 다름없다.
    - 빈 catch 문을 보거든 머릿속의 사이렌🚨이 울려야 한다.

## **예외를 무시해야 하는 경우**

물론, 예외를 무시해야 할 때도 있다.

> 적어도 무시하기로 했다면, 그 이유를 주석으로 남기고 예외 변수의 이름도 ignored로 바꿔라.

### **예시1 : FileInputStream close 메서드**

```java
@Slf4j
public class FileInputStreamExample {

    public static void main(String[] args) {
            FileInputStreamExample fx = new FileInputStreamExample();
    FileInputStream fileInputStream = fx.openFile();
    close(fileInputStream);
    }

    public void openFile() {
        File file = new File("test.txt");
        try (FileInputStream fis = new FileInputStream(file)) {
            // 파일 내용을 읽는 작업, 데이터 처리 작업 등
            return new FileInputStream(file);
        } catch (IOException e) {
            return new openFile();
        }
    }
    
    public static void close(FileInputStream fileInputStream) {
        try {
            fileInputStream.close();
        } catch (IOException ignored) {
            // 예외 처리
            log.error("Error closing the file", e); // 예외가 주기적으로 발생한다면 조사하기 좋게 로그를 남긴다.
        }
    }
}
```
- `FileInputStream`을 닫을 때는 딱히 복구할 게 없다.
    - 입력 전용 스트림이므로 파일의 상태를 변경하지 않는다.
    - 스트림을 닫는다는 건 필요한 정보를 이미 다 읽었다는 뜻이니 남은 작업이 있을리 없다.
- 혹시나 파일이 닫히지 않는 예외가 주기적으로 발생한다면, 조사해보는 것이 좋을 테니 그 사실을 로그로 남기는 것도 좋은 생각이다.

### **예시 2 : 예외 변수 이름 ignored 적용**

```java
Future<Integer> f = exec.submit(planarMap::chromaticNumber);
int numColors = 4; // 기본값, 어떤 지도라도 이 값이면 충분하다.
try {
    numColors = f.get(1L, TimeUnit.SECONDS);
} catch (TimeoutException | ExecutionException ignored) {
    // 기본값을 사용한다. (색상 수를 최소화하면 좋지만, 필수는 아니다.)
}
```

## **💡 핵심 정리**
- 검사 예외든 비검사 예외든 예외를 무시하지 말자.
- 예외를 적절히 처리해서 오류를 피하거나, 최소한 무시하지 않고 바깥으로 전파되게 둬서 디버깅 정보를 남긴 채 프로그램이 중단되게 하라.
