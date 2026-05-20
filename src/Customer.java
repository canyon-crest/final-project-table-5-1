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
    private double satisfaction;

    public Customer(long entryTime) {
        this.orderedDrink = DRINKS[RNG.nextInt(DRINKS.length)];
        this.entryTime = entryTime;
        this.patience = 60.0 + RNG.nextInt(60);
        this.satisfaction = 10.0;
    }

    public static String randomName() {
        return NAMES[RNG.nextInt(NAMES.length)];
    }

    public static String randomDrink() {
        return DRINKS[RNG.nextInt(DRINKS.length)];
    }

    public String getOrderedDrink() {
        return orderedDrink;
    }

    public long getEntryTime() {
        return entryTime;
    }

    public double getPatience() {
        return patience;
    }

    public double getSatisfaction() {
        return satisfaction;
    }

    public double calculateSatisfaction() {
        return calculateSatisfaction(entryTime, 10.0);
    }

    public double calculateSatisfaction(long finishTime) {
        return calculateSatisfaction(finishTime, 10.0);
    }

    public double calculateSatisfaction(long finishTime, double cleanliness) {
        double waitTime = Math.max(0, finishTime - entryTime);
        double speedScore = 10.0 - ((waitTime / patience) * 6.0);
        double cleanlinessScore = Math.max(0, Math.min(10, cleanliness));
        satisfaction = (speedScore * 0.7) + (cleanlinessScore * 0.3);

        if(satisfaction < 0) {
            satisfaction = 0;
        }
        if(satisfaction > 10) {
            satisfaction = 10;
        }
        return satisfaction;
    }

    public double leaveTip(double orderPrice) {
        if(satisfaction >= 9) {
            return orderPrice * 0.22;
        }
        else if(satisfaction >= 6) {
            return orderPrice * 0.15;
        }
        else if(satisfaction >= 3) {
            return orderPrice * 0.05;
        }
        return 0;
    }

    public void leaveTip() {
    }

    public void review() {
    }
}
