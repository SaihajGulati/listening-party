$(document).ready(function() {
    fetchTopSongs();

    async function fetchTopSongs() {
        try {
            const response = await fetch("/MusicController?action=list");
            const items = await response.json();
            displaySongs(items);
        } catch (error) {
            console.error("Failed to fetch top songs:", error);
        }
    }

    function displaySongs(data) {
        $.each(data, function(i, song) {
            const title = song.snippet.title;
            const artist = song.snippet.artist;
            const album = song.snippet.album;

            const row = $("<tr>");
            row.append($("<td>").text(title));
            row.append($("<td>").text(artist));
            row.append($("<td>").text(album));

            const addToQueueButton = $("<button>").text("Add to Queue");
            addToQueueButton.click(function() {
                // Call the MusicController servlet to queue the selected song
                $.get("/MusicController?action=queue&songIndex=" + i);
            });
            row.append($("<td>").append(addToQueueButton));

            $("#song-table tbody").append(row);
        });
    }
    
    const queueButton = $("<button>").text("Add to Queue");
    queueButton.click(() => {
      // Call the MusicController servlet to queue the selected song
      $.get(`/MusicController?action=queue&songIndex=${i}`);
    });
    row.append($("<td>").append(queueButton));

});

 
