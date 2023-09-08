import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class RoomManager {
	private static RoomManager instance = null;
	private HashMap<String,Room> roomMap;
	private final Lock lock;
	
	private RoomManager() {
        roomMap = new HashMap<String, Room>();
        lock = new ReentrantLock();
    }
	
	public static synchronized RoomManager getInstance() {
        if (instance == null) {
            instance = new RoomManager();
        }
        return instance;
    }
	
    public boolean createRoom(String roomName, String user) {
    	lock.lock();
    	try {
    		if(roomMap.containsKey(roomName)) {
        		return false;
        	}
            Room room = new Room(roomName);
            room.adduser(user);
            roomMap.put(roomName, room);
            return true;
    	}finally {
    		lock.unlock();
    	}
    }

    public boolean joinRoom(String roomName, String user) {
    	lock.lock();
    	try {
    		Room room = roomMap.get(roomName);
    		return (room.adduser(user));
    	}finally {
    		lock.unlock();
    	}
    }
    
    public List<Room> getRooms(){
    	List<Room> rooms = new ArrayList<Room>(roomMap.values());
    	return rooms;
    }
}
