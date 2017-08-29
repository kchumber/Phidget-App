package com.democo.locationrecord;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class UploadLocation
 */
@WebServlet("/UploadLocation")

public class UploadLocation extends HttpServlet {
	  private static final long serialVersionUID = 1L;
	  
	// This version of DisplayLocation uses a database connection
	// For efficiency, the connection is created in the init() method
	// making it available to all instances of the servlet running, as opening the connection
	// can take time, as well as closing it. This keeps an open connection.
	// Note: Investigate use of Connection Pooling to make it safer

	  private String email = "kallum_chumber@hotmail.com";
	  private String lat = "0";
	  private String lon = "0";
	  private String alt = "0";
	  Connection conn = null;
	  Statement stmt;

	  public void init(ServletConfig config) throws ServletException {
	  // init method is run once at the start of the servlet loading
	  // This will load the driver and establish a connection
	    super.init(config);
		String user = "chumberk";
	    String password = "vrexWall7";
	    String url = "jdbc:mysql://mudfoot.doc.stu.mmu.ac.uk:3306/"+user;

		// Load the database driver
		try {  Class.forName("com.mysql.jdbc.Driver").newInstance();
	        } catch (Exception e) {
	            System.out.println(e);
	        }

			// get a connection with the user/pass
	        try {
	            conn = DriverManager.getConnection(url, user, password);
	            // System.out.println("DEBUG: Connection to database successful.");
	            stmt = conn.createStatement();
	        } catch (SQLException se) {
	            System.out.println(se);
	        }
	  } // init()

	  public void destroy() {
	        try { conn.close(); } catch (SQLException se) {
	            System.out.println(se);
	        }
	  } // destroy()
    /**
     * @see HttpServlet#HttpServlet()
     */
    public UploadLocation() {
        super();
        // TODO Auto-generated constructor stub
    }

public void doPost(HttpServletRequest request, HttpServletResponse response)
    		throws ServletException, IOException {
	doGet(request, response);
}

public void doGet(HttpServletRequest request,
           HttpServletResponse response)
throws ServletException, IOException {

	response.setStatus(HttpServletResponse.SC_OK);
	
	// Determine the parameters passed to the servlet
	String info = request.getParameter("info");	
	
	if (info == null){ 
		// no request for info, so must be sending data
		// get data if available for each position parameter
		email = (request.getParameter("email") != null)?
				request.getParameter("email") : email;
		lat = (request.getParameter("lat") != null)?
				request.getParameter("lat") : lat;
		lon = (request.getParameter("lon") != null)?
				request.getParameter("lon") : lon;
		alt = (request.getParameter("alt") != null)?
				request.getParameter("alt") : alt;
				
		// DEBUG Data - print current data to server console
		System.out.println("DEBUG: Received position data. Now: Lat:"+lat+
							", Lon:"+lon+", alt:"+alt+", email:"+email);
		
		int numInserts = 0; // how many inserts were made
        try {
			// Create the INSERT statement from the parameters
			// set time inserted to be the current time on database server
			String updateSQL = 
		     	"insert into LocationTrace(UserID,Latitude,Longitude,Altitude,TimeInserted) "+
		      	"values('"+email+"',"+lat+", "+lon+","+alt+",now());";
                System.out.println("DEBUG: Update: " + updateSQL);
                numInserts = stmt.executeUpdate(updateSQL);
        } catch (SQLException se) {
            System.out.println(se);
        }
        
        // send result to client as name=value pair
		PrintWriter out = response.getWriter();
		out.print("inserted="+numInserts);
        System.out.println("DEBUG: Inserted: " + numInserts);
		out.close();
    }
	else {  
		// info parameter is present, so is request for last position data
		// Return current data (JSON format)
		response.setContentType("text/plain");
		String json = "{\"info\": {" +
		"\"email\": \"" + email + "\", " +
		"\"lat\": " + lat + ", " +
		"\"lon\": " + lon + " , " +
		"\"alt\": " + alt + "} }";
		PrintWriter out = response.getWriter();
		// System.out.println("UploadLocation JSON: "+json);
		out.print(json);
		out.close();
	}
}
}
