package lt.vinted.rules;

import lt.vinted.MockDatabase;
import lt.vinted.Transaction;
import java.time.LocalDate;
import java.util.List;

public class DiscountLimitRule implements Rule {
    private double limit;

    public DiscountLimitRule() {
        this(10);
    }
    public DiscountLimitRule(double limit) {
        this.limit = limit;
    }

    @Override
    public Transaction processTransaction(Transaction transaction) {
        // if transaction date is 2015-02-07, time period is (2025-01-30 - 2025-0-01)
        LocalDate fromDate = LocalDate.from(
                transaction.getDate().minusDays(transaction.getDate().getDayOfMonth() + 1)
        );
        LocalDate toDate = fromDate.plusMonths(1L).plusDays(1);

        // add up discounts for the current month, update incoming transaction
        // to use remainder if adding another discount goes over the limit
        List<Transaction> transactions = MockDatabase.getInstance().getTimePeriodTransactions(fromDate, toDate);

        double monthDiscountSum = transactions.stream()
                .map(Transaction::getDiscount)
                .reduce((Double::sum)).orElse(0d);

        if (monthDiscountSum >= limit) {
            transaction.setDiscount(0);
            transaction.setPrice(transaction.getProvider().getShipmentPrice(transaction.getSize()));
        } else if (transaction.getDiscount() + monthDiscountSum > limit && monthDiscountSum < limit) {
            double newDiscount = limit - monthDiscountSum;
            transaction.setDiscount(newDiscount);
            transaction.setPrice(transaction.getProvider().getShipmentPrice(transaction.getSize()) - newDiscount);
        }
        return transaction;
    }
}
