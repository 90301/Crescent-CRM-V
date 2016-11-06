package inventory;

public class InventoryCategory {

	public Integer id;
	String categoryName;
	public InventoryCategory(String categoryName) {
		// TODO Auto-generated constructor stub
		this.categoryName =  categoryName; 
		this.id = 100;
	}
	
	//All inventory categories  can be found with a sql query (unique on category)
	
	@Override
	public String toString() {
		return this.categoryName + " : " + this.id;
	}

}
