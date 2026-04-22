public class Ingredient {
    private String name;
    private double price;
    private int quantity;
    private String units;
    
    
    public Ingredient(String name, double price, String units) {
    	this.name = name;
    	this.price = price;
    	this.units = units;
    	this.quantity = 0;
    }
    public Ingredient(String name, double price, String units, int amount) {
    	this.name = name;
    	this.price = price;
    	this.units = units;
    	this.quantity = amount;
    }
    
    public String getName() {
    	return this.name;
    }   
    public double getPrice() {
	   return this.price;
    }
    public int getQuantity() {
	   return this.quantity;
    }
    public void updateQuantity(int amount) {
    	this.quantity += amount;
    	if(this.quantity < 0) {
    		this.quantity = 0;
    	}
    }
    public void clear() {
    	this.quantity = 0;
    }
    public String toString() {
    	return this.name + ": " + this.quantity + " " + this.units;
    }
    
}
