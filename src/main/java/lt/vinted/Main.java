package lt.vinted;

import lt.vinted.entity.ShippingProvider;
import lt.vinted.persistence.FakeDatabase;
import lt.vinted.rule.CheapestSizeRule;
import lt.vinted.rule.DiscountLimitRule;
import lt.vinted.rule.NthFreeRule;
import lt.vinted.rule.Rule;
import java.io.IOException;
import java.util.List;

public class Main {
    public static void main(String[] args) throws IOException {
        // * rules are objects that implement an interface
        // * new rules can be easily added
        // * in the case of rules with common concerns, rule order is important
        // * default rule values are per requirements, but you can modify them by supplying arguments
        // * I use dependency injection, because this makes the code more testable and flexible
        FakeDatabase database = getFakeDatabase();
        Rule cheapestSmallRule = new CheapestSizeRule(database);
        Rule thirdFreeRule = new NthFreeRule(database);
        Rule discountLimitRule = new DiscountLimitRule(database);
        List<Rule> rulesSet = List.of(cheapestSmallRule, thirdFreeRule, discountLimitRule);

        var processingModule = new TransactionProcessor(checkArgs(args), rulesSet, database);
        String processingResult = processingModule.processTransactions();
        System.out.println(processingResult);
    }
    private static String checkArgs(String[] args) {
        if (args == null || args.length != 1) {
            throw new RuntimeException("You must provide one input file as an argument");
        }
        return args[0];
    }

    // I usually use PostgreSQL in my projects, but a fake database is probably sufficient for this task
    private static FakeDatabase getFakeDatabase() {
        FakeDatabase database = new FakeDatabase();
        database.createProvider(new ShippingProvider(
                "La Poste", "LP", 1.5, 4.9, 6.9)
        );
        database.createProvider(new ShippingProvider(
                "Mondial Relay", "MR", 2, 3, 4));
        return database;
    }
}