package uk.ac.mmu.advprog.hackathon;

import java.io.StringWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Handles database access from within your web service
 * @author Abhipsa Panda
 */
public class DB implements AutoCloseable {

	//allows us to easily change the database used
	private static final String JDBC_CONNECTION_STRING = "jdbc:sqlite:./data/volcanoes.db";

	
	//allows us to re-use the connection between queries if desired
	private Connection connection = null;
	
	/**
	 * Creates an instance of the DB object and connects to the database
	 */
	public DB() {
		try {
			connection = DriverManager.getConnection(JDBC_CONNECTION_STRING);
		}
		catch (SQLException sqle) {
			error(sqle);
		}
	}
	
	/**
	 * Returns the number of volcanos in the database, by counting rows
	 * @return The number of volcanos in the database, or -1 if empty
	 * * @throws SQLException Syntax Error or any problem with the Query
	 */
	public int getNumberOfVolcanoes() {
		int result = -1;
		try {
			Statement s = connection.createStatement();
			ResultSet results = s.executeQuery("SELECT COUNT(*) AS count FROM volcanoes");
			while(results.next()) { //will only execute once, because SELECT COUNT(*) returns just 1 number
				result = results.getInt(results.findColumn("count"));
			}
		}
		catch (SQLException sqle) {
			error(sqle);
			
		}
		return result;
	}
	
	/**
	 * Returns the number of eruptions in the database, by counting rows
	 * @return The number of eruptions in the database, or -1 if empty
	 * * @throws SQLException Syntax Error or any problem with the Query
	 */
	public int getNumberOfEruptions() {
		int result = -1;
		try {
			Statement s = connection.createStatement();
			ResultSet results = s.executeQuery("SELECT COUNT(*) AS count FROM eruptions");
			while(results.next()) { //will only execute once, because SELECT COUNT(*) returns just 1 number
				result = results.getInt(results.findColumn("count"));
			}
		}
		catch (SQLException sqle) {
			error(sqle);
			
		}
		return result;
	}
	/**
	 * Returns the number of volcano eruptions by country
	 * @return The number of eruptions by country in the database using the ColumnNumber Number
	 * @param The country is taken to be able to pass the query to filter by the country using the SQL
	 * @throws SQLException Syntax Error or any problem with the Query
	 */
	
	public int getNumberOfVolcanoesByCountry(String country) {
		int result=0;
		try {
			String query="SELECT COUNT(*) AS Number\r\n"
					+ "FROM Volcanoes\r\n"
					+ "WHERE Country = ?";
			//PrepareStatement prevents SQL Injection Attacks
			PreparedStatement ps=connection.prepareStatement(query);
			//Set the query and replaces it with parameters
			ps.setString(1, country);
			ResultSet rs=ps.executeQuery();
			while(rs.next()) {
				result=rs.getInt(rs.findColumn("Number"));
			}
		}
		catch(SQLException sqle) {
			error(sqle);
		}
		return result;
	}
	/**
	 * It returns the data of the Eruptions within the range of the years
	 * @return The JSONArray of the formatted details by using the results from the database
	 * @param start The start year of the eruptions
	 * @param end The end year of the eruptions
	 * @return  A {@link JSONArray} It has all the fetchedDetails formatted in JSON from the database.
	 */
	public JSONArray getEruptionInYear(int start,int end) {
		//JSONArray to be able to easily acquire the JSON Objects
		JSONArray results=new JSONArray();
		try {
			
			String query="SELECT *\r\n"
					+ "FROM Eruptions\r\n"
					+ "INNER JOIN Volcanoes ON Eruptions.Volcano_ID = Volcanoes.ID\r\n"
					+ "WHERE CAST(Date AS INTEGER) >= ?\r\n"
					+ "AND CAST(Date AS INTEGER) <= ?\r\n"
					+ "ORDER BY Date ASC";
			PreparedStatement ps=connection.prepareStatement(query);
			ps.setInt(1, start);
			ps.setInt(2, end);
			ResultSet rs=ps.executeQuery();
		
			while(rs.next()) {
				//New JSON Object is created-data
				JSONObject data=new JSONObject();
				data.put("date", rs.getString("Date")!=null ? rs.getString("Date"):"");
				data.put("name", rs.getString("Name")!=null ? rs.getString("Name"):"");
				//to have JSON Object inside another JSON Object
				JSONObject location = new JSONObject();
				location.put("latitude", rs.getDouble("Latitude")!=0.0 ? rs.getDouble("Latitude"):0);
				location.put("longitude",rs.getDouble("Longitude")!=0.0 ? rs.getDouble("Longitude"):0);
				location.put("elevation",rs.getInt("Elevation")!=0 ? rs.getInt("Elevation"):0);
				location.put("country", rs.getString("Country")!=null ? rs.getString("Country"):"");
				data.put("location",location);
				data.put("deaths", rs.getInt("Deaths") !=0 ? rs.getInt("Deaths"):0);
				data.put("missing", rs.getInt("Missing")!=0 ? rs.getInt("Missing"):0);
				data.put("injuries", rs.getInt("Injuries")!=0 ? rs.getInt("Injuries"):0);
				//results is the JSON Array and  the JSON Object is now allocated to it
				results.put(data);
			}
			
		}
		catch(SQLException sqle) {
			error(sqle);
		}
		return results;
	
	}
	/**
	 * It returns the data based on the Location and the LastEruption when occurred.
	 * @return It returns the XML formatted String with the details of the eruptions limiting to 10 of them.
	 *@param latitude It takes the latitude as a double for the the query and the user inputs
	 *@param longitude It takes the longitude as a double for the query and taking the user Inputs
	 *@param last_erupted It takes the year of the last erupted volcano 
	 * @return  A {@link String} First the data is converted to XML and then put into string
	 * @throws SQLException Syntax Error or any problem with the Query
	 * 
	 */
	
	public String getVolcanoEruptionAndLocation(double latitude,double longitude,int last_erupted) {
		String results = null;
		try {
			//Example provided of the Office was replaced by the latitude,longitude and last_erupted data
			String query="SELECT\r\n"
					+ " MAX(Date) AS Last_Erupted,\r\n"
					+ " Volcano_ID, Name, Country, Latitude, Longitude, Elevation, Type\r\n"
					+ "FROM Eruptions\r\n"
					+ "INNER JOIN Volcanoes ON Eruptions.Volcano_ID = Volcanoes.ID\r\n"
					+ "WHERE CAST(Date AS INTEGER) >= ?\r\n"
					+ "GROUP BY Volcano_ID\r\n"
					+ "ORDER BY\r\n"
					+ " (\r\n"
					+ " ((? - Latitude) * (? - Latitude)) +\r\n"
					+ " (0.595 * ((? - Longitude) * (? - Longitude)))\r\n"
					+ " )\r\n"
					+ " ASC\r\n"
					+ "LIMIT 10";
			PreparedStatement ps=connection.prepareStatement(query);
			ps.setInt(1, last_erupted);
			ps.setDouble(2,latitude);
			ps.setDouble(3, latitude);
			ps.setDouble(4,longitude);
			ps.setDouble(5, longitude);
			ResultSet rs=ps.executeQuery();
			//DocumentBuilder Factory an abstract class which can help create instance like Document Builder to parse XML.
			DocumentBuilderFactory dbf=DocumentBuilderFactory.newInstance();
			DocumentBuilder db=dbf.newDocumentBuilder();
			Document doc=db.newDocument();
			//Creating element by the root and then appending it
			Element root=doc.createElement("Volcanoes");
			doc.appendChild(root);
			while(rs.next()) {
				Element volcano=doc.createElement("Volcano");
				volcano.setAttribute("id",String.valueOf(rs.getInt("Volcano_ID")));//Set attribute to add the id 
				root.appendChild(volcano);
				Element Name=doc.createElement("Name");
				Name.setTextContent(rs.getString("Name"));
				volcano.appendChild(Name);
				Element Type=doc.createElement("Type");
				Type.setTextContent(rs.getString("Type"));
				volcano.appendChild(Type);
				Element Last_Erupted=doc.createElement("Last_Erupted");
				Last_Erupted.setTextContent(rs.getString("Last_Erupted"));
				volcano.appendChild(Last_Erupted);
				Element Location=doc.createElement("Location");
				volcano.appendChild(Location);
				Element Latitude=doc.createElement("Latitude");
				Latitude.setTextContent(String.valueOf(rs.getDouble("Latitude")));
				Location.appendChild(Latitude);
				Element Longitude=doc.createElement("Longitude");
				Longitude.setTextContent(String.valueOf(rs.getDouble("Longitude")));
				Location.appendChild(Longitude);
				Element Elevation=doc.createElement("Elevation");
				Elevation.setTextContent(String.valueOf(rs.getInt("Elevation")));
				Location.appendChild(Elevation);
				Element Country=doc.createElement("Country");
				Country.setTextContent(rs.getString("Country"));
				Location.appendChild(Country);
				//After creating the XML needs to be converted into String to be displayed
				Transformer transformer =TransformerFactory.newInstance().newTransformer();
				//String Writer collects the data
				StringWriter output=new StringWriter();
				//Transforms the XML Document Object Model to a Stream
				transformer.transform(new DOMSource(doc), new StreamResult(output));
				//output is converted into String by using the toString() methos
				results=output.toString();
				
				
			}
			
		}
		catch(ParserConfigurationException | TransformerException | DOMException | SQLException ioe) {
			ioe.printStackTrace();
		}
		return results;
	}
	 
	
	
	
	
	/**
	 * Closes the connection to the database, required by AutoCloseable interface.
	 */
	@Override
	public void close() {
		try {
			if ( !connection.isClosed() ) {
				connection.close();
			}
		}
		catch(SQLException sqle) {
			error(sqle);
		}
	}
	
	/**
	 * Prints out the details of the SQL error that has occurred, and exits the programme
	 * @param sqle Exception representing the error that occurred
	 */
	private void error(SQLException sqle) {
		System.err.println("Problem Accessing Database! " + sqle.getClass().getName());
		sqle.printStackTrace();
		System.exit(1);
	}
	
	


}
