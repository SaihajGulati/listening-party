import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

@WebServlet("/Create")
public class CreateRoomServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
    	RoomManager roomManager = RoomManager.getInstance();
        
        String roomName = request.getParameter("roomName");
        String user = request.getParameter("User");

        
        roomManager.createRoom(roomName,user);

        response.setStatus(HttpServletResponse.SC_OK);
    }
}
