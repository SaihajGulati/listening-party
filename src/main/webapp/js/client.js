var socket;

// replace this with the username (get it from the cookies?)
var username = 'hi';

// BEFORE YOU USE ANY OF THESE FUNCTIONS, YOU MUST JOIN A ROOM using joinRoom(roomName)
// only exception is createNewRoom(roomName)

/*function connectToServer() {
 	socket = new WebSocket("ws://localhost:8080/Project201/ws");
	socket.onopen = function(event) {
		console.log("Connected");
	}
	
	socket.onmessage = function(event) {
		console.log(event.data)
 		data = JSON.parse(event.data);
		switch(data.type) {
		case "get":
			// returns an array of rooms if it was requested
			let rooms = data.rooms;
			break;
		case "create":
			// returns whether the room creation was successful or not
			let message = data.message;
			break;
		case "text":
			// returns the latest message a user posted
			let text = data.text;
			break;
		case "next":
			// returns next song
			let songId = data.songId;
			break;
		}
	}
	
	socket.onclose = function(event) {
		// replace this with the roomName
		leaveRoom('??');
	}
}*/

function connectToServer() {
	socket = new WebSocket("ws://localhost:8080/Project201/ws");
	socket.onopen = function(event) {
		console.log("Connected");
		getRooms();
	}
	
	socket.onmessage = function(event) {
 		data = JSON.parse(event.data);
		switch(data.type) {
		case "get":
			// returns an array of room names if it was requested
			let rooms = data.rooms;
			
			// html stuff to show reach room and url to join the room
			for (let i=0; i<rooms.length; i++) {
			    /*if (room.users.length < 4) {*/
			      const roomElement = document.createElement('div');
			      roomElement.className = 'room rounded-rectangle';
			
			      const roomName = document.createElement('div');
			      roomName.innerHTML += rooms[i];
			      roomElement.appendChild(roomName);
			
			      //const roomUsers = document.createElement('div');
			      //roomUsers.textContent = `${room.users.length} / 4`;
			      //roomElement.appendChild(roomUsers);
			
			      const joinButton = document.createElement('button');
			      joinButton.textContent = 'Join';
			      joinButton.onclick = () => window.location.href = 'room.html?roomName=' + rooms[i];
			      roomElement.appendChild(joinButton);
			
			      roomsList.appendChild(roomElement);
			    /*}*/
		  }
			
			break;
		}
	}
}

function getSongs(roomName) {
	const params = {
		"type": "getSongs",
		"roomName": roomName
	}
	socket.send(JSON.stringify(params));
}

function createNewRoom() {
	const newRoomName = prompt('Enter a name for the new room:');
	if (newRoomName)
	{
		const params = {
			"type": "create",
			"roomName": newRoomName
		}
		socket.send(JSON.stringify(params));
		window.location.href = 'room.html?roomName=' + newRoomName;
	}
}

function getRooms() {
	const params = {
		"type": "get",
	}
	socket.send(JSON.stringify(params));
}

function getUserName() {
  document.getElementById('userName').textContent = username;
  if (!username.includes('guest')) {
    document.getElementById('changeNameBtn').style.display = 'inline-block';
  } else {
    document.getElementById('changeNameBtn').style.display = 'none';
  }
}

function changeUserName()
	{
		if (localStorage.getItem("username") === "Guest") {
			alert("Only logged-in users can change their display name!")
		}
		else
		{
			const newName = prompt('Enter your new username:');
		 	if (newName) {
		   		username = newName;
		    	document.getElementById('userName').textContent = username;
		  	}
		}
}

function leaveRoom(roomName) {
	const params = {
		"type": "leave",
		"roomName": roomName
	}
	socket.send(JSON.stringify(params));
}

function sendText(roomName, message) {
	const params = {
		"type": "text",
		"message": message,
		"roomName": roomName
	}
	socket.send(JSON.stringify(params));
}
