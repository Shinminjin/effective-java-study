package shin.chapter12.item89;

import java.io.ObjectStreamException;
import java.io.Serializable;
import java.util.Arrays;

// transient 가 아닌 참조 필드를 가지는 싱글턴
public class Elvis implements Serializable {
    private static final long serialVersionUID = -8870240565519414478L;

    public static final Elvis INSTANCE = new Elvis();

    private String[] favoriteSongs = {"Hound Dog", "Heartbreak Hotel"};

    private Elvis() {
    }

    public void printFavorites() {
        System.out.println(Arrays.toString(favoriteSongs));
    }

    private Object readResolve() throws ObjectStreamException {
        return INSTANCE;
    }
}
