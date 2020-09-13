
var cookies = new CookieMonster()
var connection = new BackendConnection("Matthew")

const States = {
	MAIN_MENU: "InMainMenu",
	LOBBY: "InLobby",
	GAME: "InGame"
};

function setState(state) {
	$('#connecting-screen').find("h2").text("Connected.");
	$('#connecting-screen').fadeTo(100, 0, function() { $('#connecting-screen').hide(); $('#error-screen').hide()});
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
			clearLobby();
			populateLobby();
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
// Asks server for a room code. Connection will then call sendToGame
function createGame() {
	connection.sendCreateGame();
}
function sendToGame(gameId) {
	console.log("Sending to lobby")
	setState(States.LOBBY)
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

function clearLobby() {
	$("lobby-player-list").empty();
}
function populateLobby() {
	addPlayerToLobby(cookies.sessionId, cookies.getPlayerName());
	// Set lobby ID at top
	// Set link at top
}
function addPlayerToLobby(sessionId, name) {
	var newPlayerCard = document.createElement('div');
	newPlayerCard.setAttribute('class', 'player-card');
	newPlayerCard.setAttribute('data-session-id', sessionId);
	newPlayerCard.innerHTML = `
		<span>${name}</span>
		<img src="Images/kick.png" alt="Kick" class="icon">
		<img src="Images/crown.png" alt="Leader" class="icon">
	`;
	$('#lobby-player-list').append(newPlayerCard);
}
// Changes the display for the player's name
function changePlayerNameLobby(sessionId, newName) {
	var playerCard = $(`.player-card[data-session-id=${sessionId}`);
	playerCard.first().text(newName)
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

	$('#input-name-lobby').on('input', function() {
		var name = $('#input-name-lobby').val()
		changePlayerNameLobby(cookies.sessionId, name);
	});

	setTimeout(function(){
		if(!connection.connectionError)
		{
			$("#connecting-screen").addClass('connecting')
			$("#connecting-screen").removeClass('screen')
		}
	}, 1000);
});
