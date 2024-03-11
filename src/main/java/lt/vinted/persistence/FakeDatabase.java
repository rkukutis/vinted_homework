package lt.vinted.persistence;

import lt.vinted.enumerated.ShipmentSize;
import lt.vinted.entity.ShippingProvider;
import lt.vinted.entity.Transaction;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class FakeDatabase {
    private final List<Transaction> transactions;
    private final List<ShippingProvider> providers;

    public FakeDatabase() {
        this.transactions = new ArrayList<>();
        this.providers = new ArrayList<>();
    }

    // Methods replicate SQL query functionality

    public void createTransaction(Transaction newTransaction) {
        transactions.add(newTransaction);
    }

    public void createProvider(ShippingProvider newProvider) {
        providers.add(newProvider);
    }

    // SELECT * FROM providers WHERE short_name='shortName';
    public Optional<ShippingProvider> getProviderByShortName(String shortName) {
        return providers.stream()
                .filter(provider -> provider.getShortName().equals(shortName))
                .findFirst();
    }

    // SELECT * FROM providers ORDER BY size_shipment_price ASC LIMIT 1;
    public ShippingProvider getCheapestSizeShipmentProvider(ShipmentSize size) {
        ShippingProvider cheapestProvider = providers.getFirst();
        for (ShippingProvider provider : providers) {
            if (provider.getShipmentPrice(size) < cheapestProvider.getShipmentPrice(size)) {
                cheapestProvider = provider;
            }
        }
        return cheapestProvider;
    }
    // SELECT * FROM transactions WHERE size=LARGE AND provider=LP AND date IS BETWEEN from AND to;
    public List<Transaction> getTimePeriodTransactions(LocalDate from,
                                                       LocalDate to,
                                                       ShipmentSize searchedSize,
                                                       ShippingProvider searchedProvider) {
        List<Transaction> validTransactions = new ArrayList<>();
        for (Transaction transaction : transactions) {
            if (transaction.getDate().isAfter(from) &&
                    transaction.getDate().isBefore(to) &&
                    transaction.getSize() == searchedSize &&
                    transaction.getProvider().equals(searchedProvider)
            ){
                validTransactions.add(transaction);
            }
        }
        return validTransactions;
    }
    // SELECT * FROM transactions WHERE date IS BETWEEN from AND to;
    public List<Transaction> getTimePeriodTransactions(LocalDate from, LocalDate to) {
        List<Transaction> validTransactions = new ArrayList<>();
        for (Transaction transaction : transactions) {
            if ( transaction.getDate().isAfter(from) && transaction.getDate().isBefore(to)) {
                validTransactions.add(transaction);
            }
        }
        return validTransactions;
    }
}
