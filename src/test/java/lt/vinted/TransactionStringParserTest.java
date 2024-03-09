package lt.vinted;

import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

class TransactionStringParserTest {


    @Test
    void parserParsesCorrectString() {
        String correctString = "2024-02-09 M LP";
        Transaction transaction = TransactionStringParser.parse(correctString);
        assertEquals(LocalDate.parse("2024-02-09"), (transaction.getDate()));
    }

}