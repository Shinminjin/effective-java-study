package shin.chapter6.item41;

import org.junit.jupiter.api.Test;

import java.io.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class SerializableTest {
    record NotSerializableMelon(String type, int weight) {}

    @Test
    void notSerializableTest() throws IOException {

        NotSerializableMelon melon = new NotSerializableMelon("머스크", 3000);

        FileOutputStream fileOut = new FileOutputStream("melon.txt");

        // Creates an ObjectOutputStream
        ObjectOutputStream objOut = new ObjectOutputStream(fileOut);

        // For Debug
//        objOut.writeObject(melon);

        assertThrows(NotSerializableException.class,
                () -> objOut.writeObject(melon));
    }

    record SerializableMelon(String type, int weight) implements Serializable {}

    @Test
    void serializableTest() throws IOException, ClassNotFoundException {

        SerializableMelon melon = new SerializableMelon("머스크", 3000);

        FileOutputStream fileOut = new FileOutputStream("melon.txt");

        // Creates an ObjectOutputStream
        ObjectOutputStream objOut = new ObjectOutputStream(fileOut);

        // Writes objects to the output stream
        objOut.writeObject(melon);

        // Reads the object
        FileInputStream fileIn = new FileInputStream("melon.txt");
        ObjectInputStream objIn = new ObjectInputStream(fileIn);

        SerializableMelon newMelon = (SerializableMelon) objIn.readObject();

        // then, Check
        assertThat(newMelon.type).isEqualTo("머스크");
        assertThat(newMelon.weight).isEqualTo(3000);
    }
}
