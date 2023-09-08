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

@WebServlet("/LoginServlet")
public class LoginServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		PrintWriter pw = response.getWriter();
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		
		User user = new Gson().fromJson(request.getReader(), User.class);
		
		String username = user.getUsername();
		String password = user.getPassword();
		
		Gson gson = new Gson();
		
		int id = signinUsers(username, password);
		
		if (id == 0)
		{
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			String result = "Bad login";
			pw.write(gson.toJson(result));
			pw.flush();
		}
		else
		{
			response.setStatus(HttpServletResponse.SC_OK);
			String result = "Good login";
			pw.write(gson.toJson(result));
			pw.flush();
		}
	}
	
	public static int signinUsers(String username, String password)
	{
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
			String s = "SELECT * FROM Users WHERE username = ? AND password = ?";
			ps = conn.prepareStatement(s);
			ps.setString(1, username);
			ps.setString(2, password);
			rs = ps.executeQuery();

			//username and password match
			if(rs.next() == true) {
				return 1;
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
		
		return 0;
	}
}


