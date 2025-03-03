package uk.ac.mmu.advprog.hackathon;
/**
 * Handles the Validation and a common helper method
 * @author Abhipsa Panda
 */
public class Utility {
	/**
	 * @param value The string which is validated
	 * @param name The name is used to provide return statement when invalid
	 * As some these aspects stay common
	 * It validates if a String is Empty or null
	 */

	public String validNotEmpty(String value,String name) {
		if(value==null||value.isEmpty()) {
			return "Invalid" +name;
		}
		return null;
	}


}
