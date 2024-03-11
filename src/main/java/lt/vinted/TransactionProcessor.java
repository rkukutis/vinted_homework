package lt.vinted;
import lt.vinted.entity.Transaction;
import lt.vinted.persistence.FakeDatabase;
import lt.vinted.rule.Rule;
import lt.vinted.utility.InputValidator;
import lt.vinted.utility.TransactionStringParser;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TransactionProcessor {
    private final FakeDatabase database;
    private final String inputFile;
    private final List<Rule> rules;
    private final InputValidator inputValidator;
    private final TransactionStringParser transactionStringParser;

    public TransactionProcessor(String inputFile, FakeDatabase database) {
        this(inputFile, new ArrayList<>(), database);
    }

    public TransactionProcessor(String inputFile, List<Rule> rules, FakeDatabase database) {
        this(inputFile, rules, database, new InputValidator(database), new TransactionStringParser(database));
    }

    public TransactionProcessor(String inputFile, List<Rule> rules, FakeDatabase database,
                                InputValidator validator, TransactionStringParser stringParser) {
        this.inputFile = inputFile;
        this.rules = rules;
        this.database = database;
        this.inputValidator = validator;
        this.transactionStringParser = stringParser;
    }

    public String processTransactions() throws IOException {
        List<String> transactionStrings = Files.readAllLines(Path.of(inputFile));
        StringBuilder stringBuilder = new StringBuilder();
        for (String transactionString : transactionStrings) {
            // append "Ignored" if transaction string is invalid
            String checkedString = inputValidator.validateTransactionString(transactionString);
            if (checkedString.endsWith("Ignored")) {
                processInvalidString(checkedString);
                stringBuilder.append(checkedString).append("\n");
            } else {
                String processedTransaction = processValidString(checkedString);
                stringBuilder.append(processedTransaction).append("\n");
            }
        }
        return stringBuilder.toString();
    }

    private void processInvalidString(String invalidString) {
        System.out.println(invalidString);
    }

    private String processValidString(String transactionString) {
        // create a base transaction, 0 discount
        Transaction transaction = transactionStringParser.parse(transactionString);
        // apply all existing rules
        if (rules != null && !rules.isEmpty()) {
            for (Rule rule : rules) {
                transaction = rule.processTransaction(transaction);
            }
        }
        database.createTransaction(transaction);
        return transaction.toString();
    }

}
