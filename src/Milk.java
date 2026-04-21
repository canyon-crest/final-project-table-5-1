public class Milk extends Ingredient {
	private String type;
	public Milk(String name, double price, String type, String units) {
		super(name, price, units);
		this.type = type;
	}
	
	public String getType() {
		return this.type;
	}
}