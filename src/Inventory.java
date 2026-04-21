import java.util.ArrayList;

public class Inventory {
    
	private Ingredient beans;
	private ArrayList<Syrup> syrups;
	private ArrayList<Milk> milks;
	private ArrayList<Ingredient> toppings;
	
	public Inventory() {
		this.beans = new Ingredient("beans", 10.00, "lb"); //priced by the pound, 20 shots per pound
		this.syrups = new ArrayList<Syrup>();
		this.milks = new ArrayList<Milk>();
		this.toppings = new ArrayList<Ingredient>();
	}
	
	public String checkInventory() {
		String message = this.beans.toString();
		if(syrups.size() > 0) {
			for(int i = 0; i < syrups.size(); i ++) {
				message += syrups.get(i).toString();
			}
		}
		if(milks.size() > 0) {
			for(int i = 0; i < milks.size(); i ++) {
				message += syrups.get(i).toString();
			}
		}
		if(toppings.size() > 0) {
			for(int i = 0; i < toppings.size(); i ++) {
				message += toppings.get(i).toString();
			}
		}
		return message;
	}
	
	public void updateInventory(String name, String type, int amount) {
		if(type.equals("beans")) {
			beans.updateQuantity(amount);
		}
		
	}
}
