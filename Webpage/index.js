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
		$('#connecting-screen').fadeTo(100, 0, function() { $('#connecting-screen').hide() });
	}
	$('#lobby-screen').fadeTo(100, 0)
	$('#game-screen').fadeTo(100, 0)
	$('#main-menu-screen').fadeTo(100, 0, function() { setStateFinal(state) })
}
function setStateFinal(state) {
	switch (state) {
		case States.MAIN_MENU:
			$('#main-menu-screen').fadeTo(300, 1)
			break;
		case States.LOBBY:
			$('#lobby-screen').fadeTo(300, 1)
			break;
		case States.GAME:
			$('#game-screen').fadeTo(300, 1)
			break;
	}
}

// Joins a game from the start page. Will need to find the room code
function joinGame() {
	gameID = $("#input-game-code").val().toUpperCase()
	connection.send(gameID);
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
	setTimeout(function(){
		$("#connecting-screen").addClass('connecting')
		$("#connecting-screen").removeClass('screen')
	}, 5000);
});
