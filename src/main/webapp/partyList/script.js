document.addEventListener('DOMContentLoaded', () => {
  fetchRooms();
  fetchDisplayName();
});

async function fetchRooms() {
  // Replace with the actual API endpoint.
  const response = await fetch('/api/rooms');
  const rooms = await response.json();
  displayRooms(rooms);
}

function displayRooms(rooms) {
  const roomsList = document.getElementById('roomsList');
  roomsList.innerHTML = '';

  rooms.forEach((room) => {
    if (room.users.length < 4) {
      const roomElement = document.createElement('div');
      roomElement.classList.add('rounded-rectangle');
      roomElement.innerHTML = `
        <h2>${room.name}</h2>
        <p>${room.users.length} / 4</p>
        <button onclick="joinRoom(${room.id})">Join</button>
      `;
      roomsList.appendChild(roomElement);
    }
  });
}

async function fetchDisplayName() {
  // Replace with the actual API endpoint.
  const response = await fetch('/api/user');
  const user = await response.json();

  if (user.loggedIn) {
    document.getElementById('displayName').textContent = user.displayName;
    document.getElementById('changeNameBtn').style.display = 'inline-block';
  } else {
    document.getElementById('displayName').textContent = 'Not logged in';
    document.getElementById('changeNameBtn').style.display = 'none';
  }
}

function changeDisplayName() {
  const newName = prompt('Enter your new display name:');
  if (newName) {
    // Replace with the actual API endpoint.
    fetch('/api/user', {
      method: 'PUT',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({ displayName: newName }),
    })
      .then((response) => response.json())
      .then((user) => {
        document.getElementById('displayName').textContent = user.displayName;
      });
  }
}

function joinRoom(roomId) {
  // Replace with the actual API endpoint.
  fetch(`/api/rooms/${roomId}/join`, {
    method: 'POST',
  }).then((response) => {
    if (response.ok) {
      // Redirect to the room page.
      window.location.href = `/room/${roomId}`;
    } else {
      alert('Failed to join the room');
    }
  });
}

function createRoom() {
  // Replace with the actual API endpoint.
  fetch('/api/rooms', {
    method: 'POST',
  })
    .then((response) => response.json())
    .then((room) => {
      // Redirect to the newly created room.
      window.location.href = `/room/${room.id}`;
    });
}
