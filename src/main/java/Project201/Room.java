import java.util.List;

public class Room {
	private String roomName;
    private List<String> users;
    private int num;
    public static int maxnum = 5;

    // Constructor
    public Room(String roomName) {
        this.roomName = roomName;
        num = 0;
    }
    
    public boolean adduser(String user) {
    	if(num >= maxnum) {
    		return false;
    	}
    	else {
    		users.add(user);
    		num++;
    		return true;
    	}
    }
    
    public String getRoomName() {
    	return roomName;
    }
    
    public int getNum() {
    	return num;
    }
}

