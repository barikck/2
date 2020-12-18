import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


/**
 * This class {@code InvoiceApp} provides the functionality to read shopping basket items
 * from a file and print the invoice in readable format.
 * 
 * Rules while invoicing:
 *   1. VAT @ 12.5% on all products except food, toys and medicines.
 *   2. Additional Tax on imported goods @ 2.4%, no exemptions.
 * 
 * Parameter while running the program: 
 *    File name (e.g., /media/barikck/Data/JAVA/Projects/Test/basket1.txt)
 *  
 * 
 * @author  Chandan Barik
 *
 */
public class InvoiceApp {
	static final Set<String> VAT_ITEM_SET = getVATItemSet();
	static final Set<String> IMPORTED_ITEM_SET = getImportedItemSet();
	static final String COLUMN_SEPARATOR = "  |  ";
	static float subTotal = 0f;
	static float vat = 0f;
	static float additionalTax = 0f;

	
	/**
	 * ***************** main() ******************
	 * 
	 * @param args
	 * @throws IOException when any IO operation failure and NoSuchFileException while 
	 *         invalid file name is passed.
	 */
	public static void main(String[] args) throws IOException {
		System.out.println("Enter the file name: ");
		String fileName = new BufferedReader(new InputStreamReader(System.in)).readLine();

		List<Item> items = new ArrayList<>();
		try {
			//Fetching the item lines from the file
			items = readBasket(fileName);
		} catch (NoSuchFileException e) {
			e.printStackTrace();
			System.out.println("Please enter a valid file name.");
		}
    
		//Invoice printing started
		System.out.println(String.format("%-40s%-5s%10s%-5s%10s%-5s%10s", 
				"NAME", COLUMN_SEPARATOR, 
				"QTY", COLUMN_SEPARATOR, 
				"UNIT COST", COLUMN_SEPARATOR,
				"COST"));
		System.out.println("----------------------------------------" + COLUMN_SEPARATOR +
				"----------" + COLUMN_SEPARATOR +
				"----------" + COLUMN_SEPARATOR +
				"----------");
		
		items.forEach(item -> {
			float cost = item.getQuantity() * item.getUnitCost();
			String s1 = String.format("%-40s%-5s%10.2f%-5s%10.2f%-5s%10.2f", 
					                    item.getName(), COLUMN_SEPARATOR, 
					                    item.getQuantity(), COLUMN_SEPARATOR, 
					                    item.getUnitCost(), COLUMN_SEPARATOR,
					                    cost);
			System.out.println(s1);
			subTotal += cost;
			if(VAT_ITEM_SET.contains(item.getName())) vat += cost * 0.125;
			if(IMPORTED_ITEM_SET.contains(item.getName()) && VAT_ITEM_SET.contains(item.getName())) 
				additionalTax += cost * 1.125 * 0.024;
			else if (IMPORTED_ITEM_SET.contains(item.getName()))
				additionalTax += cost * 0.024;
		});
		
		System.out.println("\n\nSubtotal        : " + String.format("%10.2f", subTotal));
		System.out.println("Value Added Tax : " + String.format("%10.2f", vat));
		System.out.println("Additional Tax  : " + String.format("%10.2f", additionalTax));
		System.out.println("Total           : " + String.format("%10.2f", subTotal + vat + additionalTax));
		//Invoice printing ended		
	}
	
	/**
	 * Reading the file lines through Stream API and storing the order details
	 * into a list of Item objects.
	 * 
	 * @param fileName
	 * @return list of Item objects
	 * @throws IOException
	 */
	static List<Item> readBasket(String fileName) throws IOException {
            return Files.lines(Paths.get(fileName))
            		.map(l -> l.split(" "))
            		.map(arr -> new Item()
            				.setName(Arrays.stream(arr)
            						.collect(Collectors.joining(" "))
            						.substring(arr[0].length()+1,  
            								     (Arrays.stream(arr).collect(Collectors.joining(" ")).length()) 
            								      - (arr[arr.length - 1].length()+3))
            						)
            				.setQuantity(Float.parseFloat(arr[0]))
            				.setUnitCost(Float.parseFloat(arr[arr.length - 1])))
            		.collect(Collectors.toList());
               
    }
	
	/**
	 * @return a set of item names of VAT applicable items
	 */
	static Set<String> getVATItemSet() {
        Set<String> VATItemSet = new HashSet<>();
        VATItemSet.add("soap");
        VATItemSet.add("music CD");
        VATItemSet.add("imported bottle of perfume");
        VATItemSet.add("imported handbag");
        VATItemSet.add("imported sunglasses");
        VATItemSet.add("perfume bottle");
        return VATItemSet;
    } 
	
	/**
	 * @return a set of item names of imported items
	 */
	static Set<String> getImportedItemSet() {
        Set<String> importedItemSet = new HashSet<>();
        importedItemSet.add("box of imported chocolates");
        importedItemSet.add("imported bottle of perfume");
        importedItemSet.add("imported handbag");
        importedItemSet.add("imported sunglasses");
        return importedItemSet;
    }
	

}


/**
 * Class {@code Item} holds single item line details.
 * Builder pattern is provided for convenient object creation within {@code Stream} pipeline.
 */
class Item {
	private String name;
	private float quantity;
	private float unitCost;

	public String getName() {
		return name;
	}

	public Item setName(String name) {
		this.name = name;
		return this;
	}

	public float getQuantity() {
		return quantity;
	}

	public Item setQuantity(float quantity) {
		this.quantity = quantity;
		return this;
	}

	public float getUnitCost() {
		return unitCost;
	}

	public Item setUnitCost(float unitCost) {
		this.unitCost = unitCost;
		return this;
	}
}



