
var connection = new BackendConnection("test")

// Joins a game from the start page. Will need to find the room code
function joinGame() {
	websocket.send("ABCD");
	console.log("Joining game")
}
// Creates a game from the start page. Will need to ask server for a room code then go to that room
function createGame() {

}
// Checks if the room code is valid via string checking and polling server. Enables join button only when valid
$('#input-game-code').change(function() {
  alert('Changed!');
  console.log("Changed");
});
