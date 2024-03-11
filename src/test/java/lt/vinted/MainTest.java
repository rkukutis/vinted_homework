package lt.vinted;

import org.junit.jupiter.api.Test;
import java.io.IOException;
import static org.junit.jupiter.api.Assertions.*;

class MainTest {

    @Test
    public void givenInvalidValidFileArgument_thenThrowException() throws IOException {
        String[] invalidArray = {"a1.txt", "a2.txt"};
        assertThrows(RuntimeException.class, () -> Main.main(invalidArray));
        String[] emptyArray = {};
        assertThrows(RuntimeException.class, () -> Main.main(emptyArray));
    }

    @Test
    public void givenInvalidFile_thenThrowIOException() {
        String[] invalidArray = {"NOT_VALID.txt"};
        assertThrows(IOException.class, () -> Main.main(invalidArray));
    }
}