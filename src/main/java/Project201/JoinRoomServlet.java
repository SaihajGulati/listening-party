import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
@WebServlet("/Join")

public class JoinRoomServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
	
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
		
		RoomManager roomManager = RoomManager.getInstance();
        
        String roomName = request.getParameter("roomName");
        String user = request.getParameter("User");
        
        boolean join = roomManager.joinRoom(roomName,user);
       
        if(join) {
        	response.setStatus(HttpServletResponse.SC_OK);
        }else {
        	response.sendError(HttpServletResponse.SC_CONFLICT,"Room full!");
        }


        
    }
}
