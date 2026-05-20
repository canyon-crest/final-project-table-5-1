import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Customer {

    private static final String[] NAMES = {
        "Alice", "Bob", "Carol", "Dan", "Eve", "Frank",
        "Grace", "Henry", "Iris", "Jack", "Karen", "Leo",
        "Mia", "Noah", "Olivia", "Paul", "Quinn", "Rose"
    };
    // Values must match CoffeeMakerPanel's arrays exactly (including \n for toppings)
    private static final String[] COFFEES  = {"Cold Brew", "Espresso", "Drip"};
    private static final String[] SYRUPS   = {"Vanilla", "Caramel", "Hazelnut", "Lavender"};
    private static final String[] MILKS    = {"Regular", "Oat", "Almond", "Soy"};
    private static final String[] TOPPINGS = {"Whipped\nCream", "Cinnamon", "Sea Salt", "Cold\nFoam"};

    private static final Random RNG = new Random();

    private final String name;
    private final Drink  order;
    private final long   entryTime;
    private double       patience;

    public Customer(long entryTime) {
        this.name      = NAMES[RNG.nextInt(NAMES.length)];
        this.entryTime = entryTime;
        this.patience  = 60.0 + RNG.nextInt(60); // 60–120 seconds

        String coffee = COFFEES[RNG.nextInt(COFFEES.length)];
        List<String> syrups = new ArrayList<>();
        if (RNG.nextBoolean()) syrups.add(SYRUPS[RNG.nextInt(SYRUPS.length)]);
        String milk = MILKS[RNG.nextInt(MILKS.length)];
        List<String> toppings = new ArrayList<>();
        if (RNG.nextInt(3) == 0) toppings.add(TOPPINGS[RNG.nextInt(TOPPINGS.length)]);
        this.order = new Drink(coffee, syrups, milk, toppings);
    }

    public String getName()      { return name; }
    public Drink  getOrder()     { return order; }
    public long   getEntryTime() { return entryTime; }
    public double getPatience()  { return patience; }

    public void reducePatience(double seconds) { patience = Math.max(0, patience - seconds); }

    public double calculateSatisfaction() { return 0; }
    public void   leaveTip()             { }
    public void   review()               { }
}


