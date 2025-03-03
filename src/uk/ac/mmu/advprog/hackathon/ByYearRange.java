package uk.ac.mmu.advprog.hackathon;
import static spark.Spark.get;
/**
 * The end point for searching by year range
 * @author Abhipsa Panda
 */

public class ByYearRange {
	Utility utility=new Utility();
	/**
	 *Two query parameter from and to date
	 *Dates are converted  into Integer
	 *Then validated the from should be less than to year
	 *Context-type is text plain in case of errors
	 *@returns JSON File to the user
	 *@throws NumberFormatException In case the year is a different type of data entered it would show invalid date range
	 */

public ByYearRange() throws Exception{
	get("/year",(request,response)->{
		String from=request.queryParams("from");
		String to=request.queryParams("to");
		utility.validNotEmpty(from, "Date");
		utility.validNotEmpty(to, "Date");

		try (DB db=new DB()){

			int yearFrom=Integer.valueOf(from);
			int yearTo=Integer.valueOf(to);
			if(yearFrom>yearTo) {
				response.header("Content-Type", "text/plain");
				return "Invalid Date Range";
			}
			response.header("Content-Type", "application/JSON");
		return db.getEruptionInYear(yearFrom, yearTo);
			
		}
		catch(NumberFormatException e){
			response.header("Content-Type", "text/plain");
			return "Invalid Date Range";
			
		}
	
	});
	
}
}
