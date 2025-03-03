package uk.ac.mmu.advprog.hackathon;
import static spark.Spark.*;
public class Test {
	/**
	 * Test data calculates the total number of volcanos and eruptions
	 */
	public Test() throws Exception{
		get("/test",(request,response)->{
			try (DB db = new DB()) {
				return 	"Number of volcanoes: " + db.getNumberOfVolcanoes() + 
						"<br>" +
						"Number of eruptions: " + db.getNumberOfEruptions();
			}
		});
		
		}
		
	}


