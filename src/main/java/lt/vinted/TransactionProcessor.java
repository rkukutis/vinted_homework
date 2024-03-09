package lt.vinted;
import lt.vinted.rules.Rule;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class TransactionProcessor {
    private final String inputFile;
    private final List<Rule> rules;

    public TransactionProcessor(String inputFile, List<Rule> rules) {
        this.inputFile = inputFile;
        this.rules = rules;
    }
    public TransactionProcessor(String inputFile) {
        this(inputFile, new ArrayList<>());
    }

    public void processTransactions() throws IOException {
        List<String> transactionStrings = Files.readAllLines(Path.of(inputFile));
        // if transaction string is invalid, append "Ignored"
        for (String transactionString : transactionStrings) {
            String validatedString = InputValidator.validateTransactionString(transactionString);
            addDiscount(validatedString);
        }
    }

    private void addDiscount(String transactionString) {
        if (transactionString.endsWith("Ignored")) {
            System.out.println(transactionString);
            return;
        }
        // start with a base price, 0 discount
        Transaction transaction = TransactionStringParser.parse(transactionString);
        // apply all existing rules
        if (rules != null && !rules.isEmpty()) {
            for (Rule rule : rules) {
                transaction = rule.processTransaction(transaction);
            }
        }
        System.out.println(transaction);
        MockDatabase.getInstance().createTransaction(transaction);
    }

}
