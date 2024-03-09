package lt.vinted;

import lt.vinted.rules.CheapestSizeRule;
import lt.vinted.rules.DiscountLimitRule;
import lt.vinted.rules.NthFreeRule;
import lt.vinted.rules.Rule;
import java.io.IOException;
import java.util.List;

public class Main {
    public static void main(String[] args) throws IOException {
        // rules are objects that implement an interface
        // new rules can be easily added
        // in the case of rules with common concerns, rule order is important
        // default rule values are per requirements, but you can modify them by supplying arguments
        Rule cheapestSmallRule = new CheapestSizeRule();
        Rule thirdFreeRule = new NthFreeRule();
        Rule discountLimitRule = new DiscountLimitRule();
        List<Rule> rulesSet = List.of(cheapestSmallRule, thirdFreeRule, discountLimitRule);

        var processingModule = new TransactionProcessor(checkArgs(args), rulesSet);
        processingModule.processTransactions();
    }
    private static String checkArgs(String[] args) {
        if (args == null || args.length != 1) {
            throw new RuntimeException("You must provide one input file as an argument");
        }
        return args[0];
    }
}