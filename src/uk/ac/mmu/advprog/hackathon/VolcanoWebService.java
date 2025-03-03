package uk.ac.mmu.advprog.hackathon;

import static spark.Spark.port;


/**
 * Handles the setting up and starting of the web service
 * You will be adding additional routes to this class, and it might get quite large
 * Feel free to distribute some of the work to additional child classes, like I did with DB
 * @author Abhipsa Panda
 */
public class VolcanoWebService {

	/**
	 * Main program entry point, starts the web service
	 * @param args not used
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		port(8088);	
		/**
		 * Main program entry point, starts the web service
		 * @param args not used
		 */
		/**
		 * Refactored endpoints into separate classes
		 * Benefits improved maintainability the end points can be modified without affecting others.
		 * Reusability:These classes and methods can be reused.
		 * Scalability:Endpoints can be added without affecting the other classes.
		 * 
		 * 
		 */
		new Test();
		new ByCountry();
		new ByYearRange();
		new ByLocationAndEruptedSince();
		
		System.out.println("Web Service Started. Don't forget to kill it when done testing!");
	}
	
}
