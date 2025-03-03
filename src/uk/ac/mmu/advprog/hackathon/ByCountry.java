package uk.ac.mmu.advprog.hackathon;

import static spark.Spark.get;

import java.net.URLDecoder;
/**
 * The end point for the searching all the  volcanic eruptions by country
 * @author Abhipsa Panda
 */

public class ByCountry {
	/**
	 * @param search it takes the search query parameters
	 * @throws Exception In case of any SQL query issues
	 * Search if empty or null then @returns 0
	 * URLDecoder used in case of spaces
	 * If still has none search results then prints Invalid Country
	 * ContentType stays text
	 */
	public ByCountry() throws Exception{
		
		get("/country",(request,response)->{
			String search=request.queryParams("search");
			try(DB db=new DB()){
				if(search.isEmpty()|search==null) {
					response.header("Content-Type", "text/plain");
					return 0;
				}
				else {
				search=URLDecoder.decode(search,"UTF-8");//https://www.geeksforgeeks.org/java-net-urldecoder-class-java/
				if(db.getNumberOfVolcanoesByCountry(search)==0) {
					response.header("Content-Type", "text/plain");
					return "Invalid Country";
				}else {
					response.header("Content-Type", "text/plain");
				return db.getNumberOfVolcanoesByCountry(search);
				}
				}
		
		}
		});
	}

}
