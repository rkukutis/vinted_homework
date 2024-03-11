package lt.vinted;

import lt.vinted.entity.ShippingProvider;
import lt.vinted.entity.Transaction;
import lt.vinted.persistence.FakeDatabase;
import lt.vinted.rule.CheapestSizeRule;
import lt.vinted.rule.DiscountLimitRule;
import lt.vinted.rule.NthFreeRule;
import lt.vinted.rule.Rule;
import lt.vinted.utility.InputValidator;
import lt.vinted.utility.TransactionStringParser;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;

public class TransactionProcessorTest {

    private TransactionProcessor processor;
    @Mock
    private FakeDatabase mockDatabase;
    @Mock
    private InputValidator inputValidator;
    @Mock
    private TransactionStringParser transactionStringParser;

    @BeforeEach
    public void  setUp() {
        this.mockDatabase = mock(FakeDatabase.class);
        this.inputValidator = mock(InputValidator.class);
        this.transactionStringParser = mock(TransactionStringParser.class);
        this.processor = new TransactionProcessor(
                "src/test/resources/input-test.txt",
                new ArrayList<>(),
                mockDatabase,
                inputValidator,
                transactionStringParser
        );
    }

    @Test
    public void givenInvalidTransactionStrings_thenIgnoreAll() throws IOException {
        when(inputValidator.validateTransactionString(anyString())).thenReturn("Thing Ignored");
        processor.processTransactions();
        verify(inputValidator, times(21)).validateTransactionString(anyString());
        verify(transactionStringParser, times(0)).parse(anyString());
    }

    // I believe this is more of an integration test
    @Test void givenListOfTransactions_whenRulesSpecified_thenReturnPricesUnchanged() throws IOException {
        FakeDatabase database = new FakeDatabase();
        database.createProvider(new ShippingProvider(
                "La Poste", "LP", 1.5, 4.9, 6.9)
        );
        database.createProvider(new ShippingProvider(
                "Mondial Relay", "MR", 2, 3, 4));

        Rule cheapestSmallRule = new CheapestSizeRule(database);
        Rule thirdFreeRule = new NthFreeRule(database);
        Rule discountLimitRule = new DiscountLimitRule(database);
        List<Rule> rulesSet = List.of(cheapestSmallRule, thirdFreeRule, discountLimitRule);

        var processingModule = new TransactionProcessor("src/test/resources/input-test.txt", rulesSet, database);
        String processingResult = processingModule.processTransactions();
        String[] resultLines = processingResult.split("\n");
        List<String> expectedResultLines = Files.readAllLines(Path.of("src/test/resources/result-test.txt"));

        for (int i = 0; i < expectedResultLines.size(); i++) {
            Assertions.assertEquals(expectedResultLines.get(i), resultLines[i]);
        }
    }

}
