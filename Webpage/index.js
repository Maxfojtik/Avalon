var connection = new BackendConnection("Matthew")

var connected = false;

const States = {
	MAIN_MENU: "InMainMenu",
	LOBBY: "InLobby",
	GAME: "InGame"
};

function setState(state) {
	if(!connected)
	{
		connected = true;
		$('#connecting-screen').find("h2").text("Connected.");
		$('#connecting-screen').fadeTo(100, 0, function() { $('#connecting-screen').hide(); $('#error-screen').hide()});
	}
	$('#lobby-screen').fadeTo(100, 0)
	$('#game-screen').fadeTo(100, 0)
	$('#main-menu-screen').fadeTo(100, 0, function() { setStateFinal(state) })
}
function setStateFinal(state) {
	switch (state) {
		case States.MAIN_MENU:
			$('#lobby-screen').hide();
			$('#game-screen').hide();
			$('#main-menu-screen').fadeTo(300, 1)
			// Clear join game text box
			break;
		case States.LOBBY:
			$('#main-menu-screen').hide();
			$('#game-screen').hide();
			$('#lobby-screen').fadeTo(300, 1)
			// Clear lobby
			// Set lobby ID at top
			// Set link at top
			break;
		case States.GAME:
			$('#main-menu-screen').hide();
			$('#lobby-screen').hide();
			$('#game-screen').fadeTo(300, 1)
			break;
	}
}

// Joins a game from the start page. Will need to find the room code
function joinGame() {
	gameId = $("#input-game-code").val().toUpperCase()
	connection.sendJoinGame(sessionId, gameId);
	console.log("Joining game")
}
// Creates a game from the start page. Will need to ask server for a room code then go to that room
function createGame() {

}
// To be called after validating text string in on input for game lobby join text input
function postCheckedLobbyOpen(isOpen) {
	if (isOpen) {
  	$("#join-game-button").removeAttr('disabled')
  }
  else {
  	$("#join-game-button").prop("disabled", "disabled");
  }
}

function addPlayerToLobby() {

}
function changePlayerNameLobby() {

}

$(document).ready(function(){
	// Checks if the room code is valid via string checking and polling server. Enables join button only when valid
	$('#input-game-code').on('input', function() {
	  text = $('#input-game-code').val().toUpperCase(); // Get text and make capital
	  isValid = (/[A-Z]{4}/).test(text) // Is 4 capital letters
	  if (isValid)
	  	connection.checkLobbyOpen(text) // This will initialize a check with backend, resulting in call to postCheckedLobbyOpen(isOpen)
	  else
	  	$("#join-game-button").prop("disabled", "disabled");
	});
	setTimeout(function(){
		if(!connectionError)
		{
			$("#connecting-screen").addClass('connecting')
			$("#connecting-screen").removeClass('screen')
		}
	}, 1000);
});
