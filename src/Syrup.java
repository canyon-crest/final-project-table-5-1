public class Syrup extends Ingredient {
	private String flavor;
	public Syrup(String name, double price, String flavor, String units) {
		super(name, price, units);
		this.flavor = flavor;
	}
	
	public String getFlavor() {
		return this.flavor;
	}
}