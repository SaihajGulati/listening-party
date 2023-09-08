package Project201;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Vector;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

@ServerEndpoint(value = "/ws")
public class ServerSocket {
        
	private static Map<String, Room> rooms = new HashMap<String, Room>();
	
	@OnOpen
	public void open(Session session) {
		System.out.println("Connection made!");
	}
	
	@OnMessage
	public void onMessage(String message, Session session) throws IOException {
		// Handle user actions (queue song, get next song, etc.)
		JsonObject receivedJsonObject = new Gson().fromJson(message, JsonObject.class);
		JsonObject messageJsonObject = new JsonObject();
		String joinType = receivedJsonObject.get("type").getAsString();
		switch(joinType) {
		  case "get":
			messageJsonObject.addProperty("type", "get");
			
		    JsonArray listOfRooms = new JsonArray();
		    for(String room : rooms.keySet()) {
		    	listOfRooms.add(room);
		    }
		    
		    messageJsonObject.add("rooms", listOfRooms);
		    sendMessage(session, messageJsonObject.toString());
		    break;
		  case "create":
			messageJsonObject.addProperty("type", "create");
			
		    String newRoomName = receivedJsonObject.get("roomName").getAsString();
		    if(rooms.containsKey(newRoomName)) {
		    	messageJsonObject.addProperty("message", "Room already exists");
		    } else {
		    	createNewRoom(newRoomName);
		    	messageJsonObject.addProperty("message", "Room successfully created");
		    }
		    sendMessage(session, messageJsonObject.toString());
		    break;
		  case "join":
			  messageJsonObject.addProperty("type", "join");
			  String joiningRoom = receivedJsonObject.get("roomName").getAsString();
			  String joiningUser = receivedJsonObject.get("username").getAsString();
			  rooms.get(joiningRoom).addUser(session, joiningUser);
			  messageJsonObject.addProperty("currentSongId", rooms.get(joiningRoom).currentlyPlayingSong);
			  sendMessage(session, messageJsonObject.toString());
			  break;
		  case "text":
			  String messagingRoom = receivedJsonObject.get("roomName").getAsString();
			  String messagingText = receivedJsonObject.get("message").getAsString();
			  rooms.get(messagingRoom).sendText(session, messagingText);
			  break;
		  case "queue":
			  messageJsonObject.addProperty("type", "queue");			  
			  String queueingRoom = receivedJsonObject.get("roomName").getAsString();
			  String videoId = receivedJsonObject.get("videoId").getAsString();
			  Room room = rooms.get(queueingRoom);
              room.addSong(videoId);
              messageJsonObject.addProperty("message", "successfully added song");	
              sendMessage(session, messageJsonObject.toString());
			  break;
		  case "next":
			  messageJsonObject.addProperty("type", "next");
			  String pushingRoom = receivedJsonObject.get("roomName").getAsString();
			  if (!rooms.get(pushingRoom).songQueueisEmpty()) {
                  String nextSongId = rooms.get(pushingRoom).nextSong();
                  messageJsonObject.addProperty("nextSong", nextSongId);
                  rooms.get(pushingRoom).broadcast(messageJsonObject.toString());
              }
			  break;
		}
	}
	
	@OnClose
	public void close(Session session) {
		// If user joined a session try to find the user and remove them
		for(Room room : rooms.values()) {
			room.removeUser(session);
		}
	}
	
	@OnError
	public void error(Throwable error) {
		System.out.println(error.getMessage());
		System.out.println("Error parsing your request.");
	}
	
	public void createNewRoom(String name) {
		Room newRoom = new Room(name);
		rooms.put(name, newRoom);
	}
	
	public void sendMessage(Session session, String message) {
		try {
			session.getBasicRemote().sendText(message);
		} catch (IOException ioe) {
			System.out.println("ioe: " + ioe.getMessage());
		}
	}
	
	private static class Song {
        private final String videoId;

        public Song(String videoId) {
            this.videoId = videoId;
        }

        public String getVideoId() {
            return videoId;
        }
    }
	
	public class User {
		private final String username;
		public long lastQueueTimestamp;
		public int queuedSongs;
		
		public User(String username) {
			this.username = username;
			this.lastQueueTimestamp = 0;
			this.queuedSongs = 0;
		}
	}
	
	public class Room {
		private Map<Session, User> users;
		private Queue<String> songQueue = new LinkedList<>();
		public String currentlyPlayingSong;
		
		public Room(String name) {
			users = new HashMap<Session, User>();
			currentlyPlayingSong = "";
		}
		
		public void broadcast(String message) {
			// make sure user is in room to be able to send message
			try {
				for(Session user : users.keySet()) {
					user.getBasicRemote().sendText(message);
				}
			} catch (IOException ioe) {
				System.out.println("ioe: " + ioe.getMessage());
			}
		}
		
		public void sendText(Session session, String text) {
			JsonObject messageJsonObject = new JsonObject();
			messageJsonObject.addProperty("type", "text");
			messageJsonObject.addProperty("message", users.get(session).username + ": " + text);
			broadcast(messageJsonObject.toString());
		}
		
		public synchronized void addUser(Session session, String username) {
			if(users.containsKey(session) == false)
			{
				User newUser = new User(username);
				users.put(session, newUser);
			}
		}
		
		public synchronized void removeUser(Session session) {
			if(users.containsKey(session) == true)
			{
				users.remove(session);
			}
		}
		
		public synchronized void addSong(String videoId) {
			songQueue.add(videoId);
		}
		
		
		public boolean songQueueisEmpty() {
			return songQueue.isEmpty();
		}
		
		public synchronized String nextSong() {
			if (!songQueue.isEmpty()) {
                String nextSongId = songQueue.poll();
                currentlyPlayingSong = nextSongId;
                return nextSongId;
            } else {
            	return null;
            }
		}
		
//	    public boolean isUserAllowedToQueue(Session session) {
//	    	User user = users.get(session);
//	        Long lastQueueTimestamp = user.lastQueueTimestamp;
//	        Integer queuedSongs = user.queuedSongs;
//	        
//	        if (lastQueueTimestamp == 0 || queuedSongs == 0) {
//	            return true;
//	        }
//
//	        long currentTime = System.currentTimeMillis();
//	        long timeSinceLastQueue = currentTime - lastQueueTimestamp;
//
//	        if (queuedSongs < 3 || timeSinceLastQueue >= 20 * 60 * 1000) {
//	            return true;
//	        }
//
//	        return false;
//	    }
//
//	    public void updateUserQueueTimestamp(Session session) {
//	    	User user = users.get(session);
//	    	Integer queuedSongs = user.queuedSongs;
//	        
//	        long currentTime = System.currentTimeMillis();
//
//	        if (queuedSongs >= 3) {
//	            Long lastQueueTimestamp = user.lastQueueTimestamp;;
//	            if (lastQueueTimestamp != 0 && currentTime - lastQueueTimestamp >= 20 * 60 * 1000) {
//	                queuedSongs = 0;
//	            }
//	        }
//	        queuedSongs++;
//	        user.queuedSongs = queuedSongs;
//	        user.lastQueueTimestamp = currentTime;
//	    }
	}
}
