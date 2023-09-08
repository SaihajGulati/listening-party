package Project201;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import com.google.gson.Gson;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

@WebServlet("/RegisterServlet")
public class RegisterServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		PrintWriter pw = response.getWriter();
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		
		User user = new Gson().fromJson(request.getReader(), User.class);
		
		String email = user.getEmail();
		String username = user.getUsername();
		String pass = user.getPassword();
		
		Gson gson = new Gson();
		
		int id = registerUsers(username, pass, email);
		
		if (id == 0)
		{
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			String result = "Email taken";
			pw.write(gson.toJson(result));
			pw.flush();
		}
		else if (id == -1)
		{
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			String result = "Username taken";
			pw.write(gson.toJson(result));
			pw.flush();
		}
		else
		{
			response.setStatus(HttpServletResponse.SC_OK);
			String result = "good sign up";
			pw.write(gson.toJson(result));
			pw.flush();
		}
	}
	
	public static int registerUsers(String username, String password, String email)
	{
		int returnVal = 0;
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");	
		}
		catch (ClassNotFoundException e)
		{
			e.printStackTrace();
		}
		
		Connection conn = null;
		PreparedStatement ps = null;
		Statement st = null;
		ResultSet rs = null;
		
		try 
		{
			conn = DriverManager.getConnection("jdbc:mysql://localhost/ListeningPartyUsers?user=root&password=Cookie150");
			st = conn.createStatement();
			String s = "SELECT * FROM Users WHERE email = ?";
			ps = conn.prepareStatement(s);
			ps.setString(1, email);
			rs = ps.executeQuery();

			//email already exists
			if(rs.next() == true) {
				return 0;
			}			
		}
		catch (SQLException sqle) {
			System.out.println ("SQLException");
		} finally {
			try 
			{
				if (rs != null) {
					rs.close();
				}
				if (st != null) {
					st.close();
				}
				if (conn != null) {
					conn.close();
				}
			} 
			catch (SQLException sqle) 
			{
				System.out.println("sqle: " + sqle.getMessage());
			}
		}
		
		try 
		{
			conn = DriverManager.getConnection("jdbc:mysql://localhost/ListeningPartyUsers?user=root&password=Cookie150");
			st = conn.createStatement();
			String s = "SELECT * FROM Users WHERE username = ?";
			ps = conn.prepareStatement(s);
			ps.setString(1, username);
			rs = ps.executeQuery();
			
			//username already exists
			if(rs.next() == true) {
				return -1;
			}			
		}
		catch (SQLException sqle) {
			System.out.println ("SQLException");
		} finally {
			try 
			{
				if (rs != null) {
					rs.close();
				}
				if (st != null) {
					st.close();
				}
				if (conn != null) {
					conn.close();
				}
			} 
			catch (SQLException sqle) 
			{
				System.out.println("sqle: " + sqle.getMessage());
			}
		}
		
		try 
		{
			conn = DriverManager.getConnection("jdbc:mysql://localhost/ListeningPartyUsers?user=root&password=Cookie150");
			st = conn.createStatement();			
			st.execute("INSERT INTO Users (username, password, email) VALUES ('"+username+"', '"+password+"', '"+email+"')");
			
			ps = conn.prepareStatement("SELECT * FROM Users WHERE username=?");
			ps.setString(1, username);
			rs = ps.executeQuery();

			if(rs.next() == true) {
				returnVal = -2;
			}
			
		}
		catch (SQLException sqle) {
			System.out.println ("SQLException");
		} finally {
			try 
			{
				if (rs != null) {
					rs.close();
				}
				if (st != null) {
					st.close();
				}
				if (conn != null) {
					conn.close();
				}
			} 
			catch (SQLException sqle) 
			{
				System.out.println("sqle: " + sqle.getMessage());
			}
		}
		
		return returnVal;
	}
}