package lt.vinted;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MockDatabase {

    private static MockDatabase database_instance = null;

    private final List<Transaction> transactions;
    private final List<ShippingProvider> providers;

    private MockDatabase() {
        this.transactions = new ArrayList<>();
        this.providers = new ArrayList<>();
        // These providers already exist when we receive new transactions
        ShippingProvider laPoste = new ShippingProvider(
                "La Poste", "LP", 1.5,4.9,6.9);
        ShippingProvider mondialRelay = new ShippingProvider(
                "Mondial Relay", "MR", 2,3,4);
        providers.addAll(List.of(laPoste, mondialRelay));
    }

    // I need the database object in multiple other classes, so I'm making it a singleton
    public static MockDatabase getInstance() {
        if (database_instance == null) {
            database_instance = new MockDatabase();
        }
        return database_instance;
    }

    public boolean createTransaction(Transaction newTransaction) {
        if (transactions.contains(newTransaction)) {
            throw new RuntimeException("Transaction already exists");
        }
        transactions.add(newTransaction);
        return true;
    }
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
