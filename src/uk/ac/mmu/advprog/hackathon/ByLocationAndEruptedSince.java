package uk.ac.mmu.advprog.hackathon;

import static spark.Spark.get;
/**
 * The end point to filter by location and the erupted since year
 * @author Abhipsa Panda
 */
public class ByLocationAndEruptedSince {
	Utility utility=new Utility();
	/**
	 * It uses the latitude and longitude and the erupted since with the utility method to see if it is not empty
	 * In order to avoid any of the data to be validated  It would send the message which is invalid with try and catch in case string is added
	 * @throws NumberFormatExcpetion In case of any wrong data entered
	 * Parses to double and Int and then return the XML data
	 * Content-Type is XML
	 */
	public  ByLocationAndEruptedSince() throws Exception {
		get("/location",(request,response)->{
			String latitude=request.queryParams("latitude");
			String longitude=request.queryParams("longitude");
			String erupted_Since=request.queryParams("erupted_since");
			utility.validNotEmpty(longitude, "longitude");
			utility.validNotEmpty(latitude, "latitude");
			utility.validNotEmpty(erupted_Since, "Erupted Since");

			try {
				Double.parseDouble(longitude);
			}
			catch(NumberFormatException e){
				response.header("Content-Type", "text/plain");
				return "Invalid longitude";
				
			}
			try {
				Double.parseDouble(latitude);
			}
			catch(NumberFormatException e){
				response.header("Content-Type", "text/plain");
				return "Invalid latitude";
				
			}
			try {
				Integer.parseInt(erupted_Since);
			}
			catch(NumberFormatException e){
				response.header("Content-Type", "text/plain");
				return "Invalid Erupted Since";
				
			}
			try (DB db=new DB()){
				double latituded=Double.valueOf(latitude);
				double longituded=Double.valueOf(longitude);
				int Last_erupted=Integer.valueOf(erupted_Since);
				response.header("Content-Type", "application/xml");
				return db.getVolcanoEruptionAndLocation(latituded, longituded, Last_erupted);
			}
	});

}
}
