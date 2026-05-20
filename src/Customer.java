import java.util.Random;

public class Customer {

    private static final String[] NAMES = {
        "Alice", "Bob", "Carol", "Dan", "Eve", "Frank",
        "Grace", "Henry", "Iris", "Jack", "Karen", "Leo",
        "Mia", "Noah", "Olivia", "Paul", "Quinn", "Rose"
    };

    private static final String[] DRINKS = {
        "Espresso", "Latte", "Cappuccino",
        "Americano", "Mocha", "Flat White"
    };

    private static final Random RNG = new Random();

    private final String orderedDrink;
    private final long entryTime;
    private double patience;

    public Customer(long entryTime) {
        this.orderedDrink = DRINKS[RNG.nextInt(DRINKS.length)];
        this.entryTime = entryTime;
        this.patience = 60.0 + RNG.nextInt(60); // 60–120 seconds
    }

    public static String randomName() {
        return NAMES[RNG.nextInt(NAMES.length)];
    }

    public static String randomDrink() {
        return DRINKS[RNG.nextInt(DRINKS.length)];
    }

    public String getOrderedDrink() { return orderedDrink; }
    public long getEntryTime()      { return entryTime; }
    public double getPatience()     { return patience; }

    public double calculateSatisfaction() { return 0; }
    public void leaveTip()               { }
    public void review()                 { }
}

