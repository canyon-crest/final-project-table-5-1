public class Shop {
    
	private int currentDay;
	private double money;
	private double rating;
	private double cleanliness;
	private int reviewCount;
	
	public Shop() {
		startGame();
	}
	
	public void startGame() {
		this.currentDay = 0;
		this.money = 0;
		this.rating = 10;
		this.cleanliness = 10;
		this.reviewCount = 0;
		startDay();
	}
	public void startDay() {
		
	}
	
	public void endDay() {
		
	}
	
	public void checkGameOver() {
		
	}
	
	public void updateCleanliness(double amount) {
		this.cleanliness += amount;
	}
	
	public double getCleanliness() {
		return this.cleanliness;
	}
}
