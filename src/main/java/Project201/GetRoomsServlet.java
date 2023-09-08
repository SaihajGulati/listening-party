import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

import com.google.gson.Gson;

@WebServlet("/getRooms")
//return current status of rooms
public class GetRoomsServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	//temporal class for data return
	public class RoomData{
		private String name;
        private int numPeople;

        public RoomData(String N, int NP) {
            name = N;
            numPeople = NP;
        }

        public String getName() {
            return name;
        }

        public int getNumPeople() {
            return numPeople;
        }
	}
    
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
    		throws ServletException, IOException {

    	RoomManager roomManager = RoomManager.getInstance();
        List<Room> rooms = roomManager.getRooms();
        List<RoomData> roomDataList = new ArrayList<>();
        //roomDataList for response
        for (Room room : rooms) {
            roomDataList.add(new RoomData(room.getRoomName(), room.getNum()));
        }

        Gson gson = new Gson();
        String json = gson.toJson(roomDataList);

        response.setContentType("application/json");

        response.getWriter().write(json);
    }
}