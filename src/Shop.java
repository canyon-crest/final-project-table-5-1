public class Shop {
    
	private int currentDay;
	private double money;
	private double rating;
	private double cleanliness;
	private int reviewCount;
	private double totalProfit;
	private String state;
	
	public Shop() {
		startGame();
	}
	
	public void startGame() {
		this.currentDay = 1;
		this.money = 50;
		this.rating = 10;
		this.cleanliness = 10;
		this.reviewCount = 0;
		this.totalProfit = 0;
		this.state = "OPEN";
		startDay();
	}
	public void startDay() {
		this.state = "OPEN";
	}
	
	public void endDay() {
		this.currentDay++;
		this.state = "CLOSED";
	}
	
	public void checkGameOver() {
		if(money < 0) {
			this.state = "GAME_OVER";
		}
		else if(currentDay > 14) {
			this.state = "WIN";
		}
	}
	
	public void updateCleanliness(double amount) {
		this.cleanliness += amount;
		if(this.cleanliness < 0) {
			this.cleanliness = 0;
		}
		if(this.cleanliness > 10) {
			this.cleanliness = 10;
		}
	}
	
	public double getCleanliness() {
		return this.cleanliness;
	}

	public void addSale(double amount) {
		this.money += amount;
		this.totalProfit += amount;
	}

	public void spendMoney(double amount) {
		this.money -= amount;
		checkGameOver();
	}

	public void updateReviews(double satisfaction) {
		this.rating = ((this.rating * reviewCount) + satisfaction) / (reviewCount + 1);
		this.reviewCount++;
	}

	public int getCurrentDay() {
		return currentDay;
	}

	public double getMoney() {
		return money;
	}

	public double getRating() {
		return rating;
	}

	public double getTotalProfit() {
		return totalProfit;
	}

	public String getState() {
		return state;
	}
}
