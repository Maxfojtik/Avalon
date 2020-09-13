
// var wsUri = "ws://localhost:12389"; // Localhost
var wsUri = "ws://74.140.3.27:12389"; // Max's

class BackendConnection {
	constructor(sessionId) {
		this.connectionError = false;
		this.sesid = sessionId;
		this.websocket = new WebSocket(wsUri);
		var self = this;
		this.websocket.onopen = function(evt) { self.onOpen(evt) };
		this.websocket.onclose = function(evt) { self.onClose(evt) };
		this.websocket.onmessage = function(evt) { self.onMessage(evt) };
		this.websocket.onerror = function(evt) { self.onError(evt) };
	}

	onOpen(evt) {
		console.log("CONNECTED");
		this.send("PlayerConnect|"+this.sesid);
	}

	onClose(evt) {
		console.log("DISCONNECTED");
	}

	onMessage(evt) {
		console.log('<-: ' + evt.data);
		var params = evt.data.split("|");
		if(params[0]=="UpdateState")
		{
			setState(params[1]);
		}
		if(params[0]=="LobbyOpen")
		{
			postCheckedLobbyOpen(params[1] == 'true');
		}
		if(params[0]=="GameId")
		{
			setGameId(params[1]);
			this.sendJoinGame(cookies.sessionId, params[1]);
		}
		if(params[0]=="UpdatedName")
		{
			changePlayerNameLobby(params[1], params[2]);
		}
		if(params[0]=="PlayerJoinedGame")
		{
			addPlayerToLobby(params[1], params[2]);
		}
		if(params[0]=="Players")
		{
			addPlayersToExistingLobby(params.splice(1));
		}
	}

	onError(evt) {
		console.log('ERROR: ' + evt.type);
		this.connectionError = true;
		$("#connecting-screen").hide();
		$("#error-screen").addClass('connecting');
		$("#error-screen").removeClass('screen');
	}

	send(message) {
		this.websocket.send(message);
		console.log("->: " + message);
	}

	sendJoinGame(sessionId, gameId)
	{
		this.send("JoinGame|"+sessionId+"|"+gameId);
	}
	
	sendCreateGame()
	{
		this.send("CreateGame");
	}
	
	sendUpdateName(sessionId, newName)
	{
		this.send("UpdateName|"+sessionId+"|"+newName);
	}
	
	setRoleCount(sessionId, gameId, roleName, number)
	{
		this.send("Admin|"+sessionId+"|"+gameId+"|SetRole|"+roleName+"|"+number);
	}
	
	checkLobbyOpen(gameId)
	{
		this.send("LobbyOpen|"+gameId);
	}
}