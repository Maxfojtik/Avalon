
var cookies = new CookieMonster()
var connection = new BackendConnection()

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
	connection.sendJoinGame(cookies.sessionId, gameId);
	console.log("Joining game")
}
// Asks server for a room code and then server will have us join game
function createGame() {
	connection.sendCreateGame();
}
// Called when connecting to game lobby. Called by backend
function setGameId(gameId) {
	window.history.replaceState(null, null, "?room="+gameId);
	$("#lobby-id").text(gameId)
	$("#lobby-link").text(window.location)
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
	$("#input-name-lobby").val(cookies.getPlayerName())
	// Set lobby ID at top
	// Set link at top
	$("#input-name-lobby").focus()
}
// Changes the display for the player's name
function changePlayerNameLobby(sessionId, newName) {
	var playerCard = $(".player-card[data-session-id="+sessionId+"]");
	playerCard.children().first().text(newName)
}
function removePlayerCardLobby(sessionId) {
	$(".player-card[data-session-id="+sessionId+"]").remove()
}
function makeHost(sessionId) {
	$(".player-card[data-session-id="+sessionId+"]").prependTo("#lobby-player-list")

	// If we're the new host, give us host buttons
	if (sessionId === cookies.sessionId) {
	}
	// If we're not the new host
	else {
		// Hide kick icons
		kickIcons = $(".kick-icon")
		kickIcons.removeClass("kick-icon")
		kickIcons.addClass("kick-icon-hidden")
		// Hide host icons
		hostIcons = $(".host-icon")
		hostIcons.removeClass("host-icon")
		hostIcons.addClass("host-icon-hidden")
		// Re-add host's host icon
		hostIcon = $("#lobby-player-list").children().first().children(".host-icon-hidden")
		hostIcon.removeClass("host-icon-hidden")
		hostIcon.addClass("host-icon")
	}
}

function remakePlayerCards(players) {
	$("#lobby-player-list").empty()
	addPlayersToExistingLobby(players);
}
function addPlayersToExistingLobby(players) { // Players comes in format: [id1, name1, id2, name2...]
	for (var i=0; i<players.length; i+=2) {
		addPlayerToLobby(players[i], players[i+1], i==0)
	}
}
function addPlayerToLobby(sessionId, name, isHost) {
	console.log("name: "+name)
	var newPlayerCard = document.createElement('div');
	newPlayerCard.setAttribute('class', 'player-card');
	newPlayerCard.setAttribute('data-session-id', sessionId);

	var playerName = document.createElement('span');
	newPlayerCard.appendChild(document.createTextNode(name));
	newPlayerCard.appendChild(playerName);

	var kickIcon = document.createElement('img'); 
  kickIcon.src = "Images/kick.png";
  kickIcon.classList.add("icon")
  kickIcon.classList.add("kick-icon-hidden");
	newPlayerCard.appendChild(kickIcon);

  var hostIcon = document.createElement('img'); 
  hostIcon.src = "Images/crown.png";
  hostIcon.classList.add("icon")
  hostIcon.classList.add("host-icon-hidden");
	newPlayerCard.appendChild(hostIcon);

	// newPlayerCard.innerHTML = `
	// 	<span>${name}</span>
	// 	<img src="Images/kick.png" alt="Kick" class="icon kick-icon-hidden">
	// 	<img src="Images/crown.png" alt="Host" class="icon host-icon-hidden">
	// `;
	$('#lobby-player-list').append(newPlayerCard);
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
	$('#txtSearchProdAssign').keypress(function (e) {
		if(e.which == 13)  // the enter key code
			$('#join-game-button').click();
	}); 
	$('#input-game-code').focus()

	$('#input-name-lobby').on('input', function() {
		var name = $('#input-name-lobby').val()
		changePlayerNameLobby(cookies.sessionId, name);
		connection.sendUpdateName(cookies.sessionId, name)
		cookies.setPlayerName(name)
	});

	setTimeout(function(){
		if(!connection.connectionError)
		{
			$("#connecting-screen").addClass('connecting')
			$("#connecting-screen").removeClass('screen')
		}
	}, 1000);
});
