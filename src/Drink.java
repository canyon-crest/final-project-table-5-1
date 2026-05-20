public class Drink {
    private DrinkRecipe recipe;
    private boolean made;

    public Drink(DrinkRecipe recipe) {
        this.recipe = recipe;
        this.made = false;
    }

    public Drink(DrinkRecipe recipe, String name, double price, double prepTime) {
        this.recipe = new DrinkRecipe(name);
        this.made = false;
    }

    public boolean makeDrink(Inventory inventory) {
        made = recipe.consume(inventory);
        return made;
    }

    public boolean makeDrink() {
        made = true;
        return made;
    }

    public boolean wasMade() {
        return made;
    }

    public String getName() {
        return recipe.getName();
    }

    public double getPrice() {
        return recipe.getPrice();
    }

    public int getPrepTime() {
        return recipe.getBasePrepTime();
    }

    public DrinkRecipe getRecipe() {
        return recipe;
    }
}
