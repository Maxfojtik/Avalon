
var wsUri = "ws://localhost:12389";
var connectionError = false;
class BackendConnection {
	constructor(sessionId) {
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
	}

	onError(evt) {
		console.log('ERROR: ' + evt.type);
		connectionError = true;
		$("#connecting-screen").hide();
		$("#error-screen").addClass('connecting');
		$("#error-screen").removeClass('screen');
	}

	send(message) {
		this.websocket.send(message);
		console.log("->: " + message);
	}

	sendJoinGame(name, sessionId, gameId)
	{
		
	}
}