import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Drink {

    public static final double BASE_COLD_BREW    = 5.50;
    public static final double BASE_ESPRESSO     = 4.00;
    public static final double BASE_DRIP         = 3.50;
    public static final double SYRUP_COST        = 0.75;
    public static final double SPECIAL_MILK_COST = 1.00;
    public static final double TOPPING_COST      = 0.50;

    private final String name;
    private final String coffeeType;
    private final List<String> syrups;
    private final String milk;
    private final List<String> toppings;
    private final double price;

    public Drink(String coffeeType, List<String> syrups, String milk, List<String> toppings) {
        this.coffeeType = coffeeType;
        this.syrups     = new ArrayList<>(syrups);
        this.milk       = milk;
        this.toppings   = new ArrayList<>(toppings);
        this.price      = computePrice(coffeeType, syrups, milk, toppings);
        this.name       = buildName(coffeeType, syrups, milk, toppings);
    }

    public static double computePrice(String coffeeType, List<String> syrups, String milk, List<String> toppings) {
        double base;
        switch (coffeeType != null ? coffeeType : "") {
            case "Cold Brew": base = BASE_COLD_BREW; break;
            case "Espresso":  base = BASE_ESPRESSO;  break;
            default:          base = BASE_DRIP;      break;
        }
        base += syrups.size() * SYRUP_COST;
        if (milk != null && !"Regular".equals(milk)) base += SPECIAL_MILK_COST;
        base += toppings.size() * TOPPING_COST;
        return base;
    }

    private static String buildName(String coffeeType, List<String> syrups, String milk, List<String> toppings) {
        StringBuilder sb = new StringBuilder();
        if (!syrups.isEmpty()) sb.append(syrups.get(0)).append(" ");
        if (milk != null && !"Regular".equals(milk)) sb.append(milk).append(" ");
        sb.append(coffeeType);
        if (!toppings.isEmpty()) sb.append(" w/ ").append(toppings.get(0).replace("\n", " "));
        return sb.toString();
    }

    public String getName()           { return name; }
    public String getCoffeeType()     { return coffeeType; }
    public List<String> getSyrups()   { return new ArrayList<>(syrups); }
    public String getMilk()           { return milk; }
    public List<String> getToppings() { return new ArrayList<>(toppings); }
    public double getPrice()          { return price; }

    /** Returns true if the provided selections exactly match this order. */
    public boolean matches(String c, Set<String> s, String m, Set<String> t) {
        return java.util.Objects.equals(coffeeType, c)
            && new HashSet<>(syrups).equals(s != null ? s : new HashSet<>())
            && java.util.Objects.equals(milk, m)
            && new HashSet<>(toppings).equals(t != null ? t : new HashSet<>());
    }

    public void useIngredients() { /* hook for Inventory */ }
}
