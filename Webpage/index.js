
var connection = new BackendConnection()

// Joins a game from the start page. Will need to find the room code
function joinGame() {
	websocket.send("ABCD");
	console.log("Joining game")
}
// Creates a game from the start page. Will need to ask server for a room code then go to that room
function createGame() {

}
$(document).ready(function(){
	// Checks if the room code is valid via string checking and polling server. Enables join button only when valid
	$('#input-game-code').on('input', function() {
	  text = $('#input-game-code').val().toUpperCase(); // Get text and make capital
	  isValid = (/[A-Z]{4}/).test(text) // Is 4 capital letters
	  // if (isValid)
	  	// isValid = connection.isRoomOpen()
	  if (isValid) {
	  	$("#join-game-button").removeAttr('disabled')
	  }
	  else {
	  	$("#join-game-button").prop("disabled", "disabled");
	  }
	});
});
