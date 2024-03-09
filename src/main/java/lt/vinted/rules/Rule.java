package lt.vinted.rules;

import lt.vinted.Transaction;

@FunctionalInterface
public interface Rule {
    // if you have a list of transactions, you can process them in a stream by mapping over each one with a rule
    public Transaction processTransaction(Transaction transaction);
}
