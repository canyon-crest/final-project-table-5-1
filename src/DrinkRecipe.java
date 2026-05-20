import java.util.ArrayList;

public class DrinkRecipe {
    private String name;
    private ArrayList<Ingredient> ingredients;
    private double price;
    private int basePrepTime;

    public DrinkRecipe(String name) {
        this.name = name;
        this.ingredients = new ArrayList<Ingredient>();
        setRecipeDetails(name);
    }

    public DrinkRecipe(String name, ArrayList<Ingredient> ingredients, double price, int basePrepTime) {
        this.name = name;
        this.ingredients = ingredients;
        this.price = price;
        this.basePrepTime = basePrepTime;
    }

    private void setRecipeDetails(String name) {
        addIngredient("beans", 1);
        addIngredient("cups", 1);

        if(name.equals("Espresso")) {
            this.price = 3.00;
            this.basePrepTime = 12;
        }
        else if(name.equals("Americano")) {
            this.price = 3.50;
            this.basePrepTime = 16;
        }
        else if(name.equals("Latte")) {
            addIngredient("milk", 1);
            this.price = 4.50;
            this.basePrepTime = 22;
        }
        else if(name.equals("Cappuccino")) {
            addIngredient("milk", 1);
            this.price = 4.75;
            this.basePrepTime = 24;
        }
        else if(name.equals("Mocha")) {
            addIngredient("milk", 1);
            addIngredient("caramel syrup", 1);
            this.price = 5.25;
            this.basePrepTime = 28;
        }
        else if(name.equals("Flat White")) {
            addIngredient("milk", 1);
            this.price = 4.25;
            this.basePrepTime = 20;
        }
        else {
            this.price = 3.00;
            this.basePrepTime = 18;
        }
    }

    public void addIngredient(String name, int amount) {
        ingredients.add(new Ingredient(name, 0.0, "units", amount));
    }

    public boolean isAvailable(Inventory inventory) {
        for(int i = 0; i < ingredients.size(); i ++) {
            Ingredient ingredient = ingredients.get(i);
            if(!inventory.hasIngredient(ingredient.getName(), ingredient.getQuantity())) {
                return false;
            }
        }
        return true;
    }

    public boolean consume(Inventory inventory) {
        if(!isAvailable(inventory)) {
            return false;
        }
        for(int i = 0; i < ingredients.size(); i ++) {
            Ingredient ingredient = ingredients.get(i);
            inventory.consumeIngredient(ingredient.getName(), ingredient.getQuantity());
        }
        return true;
    }

    public String getName() {
        return name;
    }

    public ArrayList<Ingredient> getIngredients() {
        return new ArrayList<Ingredient>(ingredients);
    }

    public double getPrice() {
        return price;
    }

    public int getBasePrepTime() {
        return basePrepTime;
    }

    public String toString() {
        return name + " - $" + String.format("%.2f", price);
    }
}
