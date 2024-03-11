package lt.vinted.rule;

import lt.vinted.persistence.FakeDatabase;
import lt.vinted.entity.Transaction;
import java.time.LocalDate;
import java.util.List;

public class DiscountLimitRule implements Rule {
    private double limit;
    private final FakeDatabase database;

    public DiscountLimitRule(FakeDatabase database) {
        this(10, database);
    }
    public DiscountLimitRule(double limit, FakeDatabase database) {
        if (limit < 0) {
            throw new RuntimeException("Discount limit can not be a negative number");
        }
        this.limit = limit;
        this.database = database;
    }

    @Override
    public Transaction processTransaction(Transaction transaction) {
        if (limit == 0) {
            transaction.setPrice(transaction.getProvider().getShipmentPrice(transaction.getSize()));
            transaction.setDiscount(0);
        }
        // if transaction date is 2015-02-07, time period is (2025-01-30 - 2025-0-01)
        LocalDate fromDate = LocalDate.from(
                transaction.getDate().minusDays(transaction.getDate().getDayOfMonth() + 1)
        );
        LocalDate toDate = fromDate.plusMonths(1L).plusDays(1);

        // add up discounts for the current month, update incoming transaction
        // to use remainder if adding another discount goes over the limit
        List<Transaction> transactions = database.getTimePeriodTransactions(fromDate, toDate);

        double monthDiscountSum = transactions.stream()
                .map(Transaction::getDiscount)
                .reduce((Double::sum)).orElse(0d);

        // if the limit has already been reached remove any discount and set price back to base
        if (monthDiscountSum >= limit) {
            transaction.setDiscount(0);
            transaction.setPrice(transaction.getProvider().getShipmentPrice(transaction.getSize()));
            // apply a discount with remaining amount
        } else if (transaction.getDiscount() + monthDiscountSum > limit && monthDiscountSum < limit) {
            double newDiscount = limit - monthDiscountSum;
            transaction.setDiscount(newDiscount);
            transaction.setPrice(transaction.getProvider().getShipmentPrice(transaction.getSize()) - newDiscount);
        }
        return transaction;
    }
}
