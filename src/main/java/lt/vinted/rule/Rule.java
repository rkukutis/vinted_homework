package lt.vinted.rule;

import lt.vinted.entity.Transaction;

@FunctionalInterface
public interface Rule {
    // Just a simple interface for any rules that may be added
    // makes it easier to use rules in a stream
    public Transaction processTransaction(Transaction transaction);
}
