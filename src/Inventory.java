import java.util.ArrayList;

public class Inventory {
    
	private Ingredient beans;
	private ArrayList<Ingredient> syrups;
	private ArrayList<Ingredient> milks;
	//private ArrayList<Ingredient> toppings;
    public static final double BEANS_PRICE = 10.00;
    public static final double MILK_PRICE = 3.00; //half gal
    public static final double SPECIALMILK_PRICE = 4.50;
    public static final double VANILLASYRUP_PRICE = 5.00;
    public static final double SPECIALSYRUP_PRICE = 8.00;
	
	
	public Inventory() {
		this.beans = new Ingredient("beans", BEANS_PRICE, "lbs"); //priced by the pound, 20 shots per pound
		this.syrups = new ArrayList<Ingredient>();
		this.milks = new ArrayList<Ingredient>();
		//this.toppings = new ArrayList<Ingredient>();
	}
	
	public String checkInventory() {
		String message = this.beans.toString() + "\n";
		if(syrups.size() > 0) {
			for(int i = 0; i < syrups.size(); i ++) {
				message += syrups.get(i).toString() + "\n";
			}
		}
		if(milks.size() > 0) {
			for(int i = 0; i < milks.size(); i ++) {
				message += syrups.get(i).toString() + "\n";
			}
		}
//		if(toppings.size() > 0) {
//			for(int i = 0; i < toppings.size(); i ++) {
//				message += toppings.get(i).toString();
//			}
//		}
		return message;
	}
	
	public void updateInventory(String name, String type, int amount) {
		if(type.equals("beans")) {
			beans.updateQuantity(amount);
		}
		else if(type.equals("milk")) {
			if(milks.size() > 0) {
				for(int i = 0; i < milks.size(); i ++) {
					if((milks.get(i)).getName().equals(name)) {
						(milks.get(i)).updateQuantity(amount);
					}
				}
			}
			else {
				if(!name.equals("milk")) {
					milks.add(new Ingredient(name, SPECIALMILK_PRICE, "cartons", amount));
				}
				else {
					milks.add(new Ingredient(name, MILK_PRICE, "cartons", amount));
				}
			}
		}
		else if(type.equals("syrup")) {
			if(syrups.size() > 0) {
				for(int i = 0; i < syrups.size(); i ++) {
					if((syrups.get(i)).getName().equals(name)) {
						(syrups.get(i)).updateQuantity(amount);
					}
				}
			}
			else {
				if(!name.equals("vanilla syrup")) {
					syrups.add(new Ingredient(name, SPECIALSYRUP_PRICE, "bottles", amount));
				}
				else {
					syrups.add(new Ingredient(name, VANILLASYRUP_PRICE, "bottles", amount));
				}
			}
		}
		
	}
	public void clearInventory() {
		beans.clear();
		if(milks.size() > 0) {
			for(int i = 0; i < milks.size(); i ++) {
				(milks.get(i)).clear();
			}
		}
		if(syrups.size() > 0) {
			for(int i = 0; i < syrups.size(); i ++) {
				(syrups.get(i)).clear();
			}
		}
	}
	public static void main(String[] args) {
		Inventory i = new Inventory();
		System.out.println(i.checkInventory());
		i.updateInventory("beans",  "beans", 10);
		i.updateInventory("milk", "milk", 3);
		i.updateInventory("almond milk", "milk", 2);
		i.updateInventory("vanilla syrup", "syrup", 2);
		i.updateInventory("caramel syrup", "syrup", 1);
		System.out.println(i.checkInventory());
		i.clearInventory();
		System.out.println(i.checkInventory());
	}
}

