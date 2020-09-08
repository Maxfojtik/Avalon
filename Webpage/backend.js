
var wsUri = "ws://74.140.3.27:12389";

class BackendConnection {

	constructor() {
		this.websocket = new WebSocket(wsUri);
		this.websocket.onopen = function(evt) { this.onOpen(evt) };
		this.websocket.onclose = function(evt) { this.onClose(evt) };
		this.websocket.onmessage = function(evt) { this.onMessage(evt) };
		this.websocket.onerror = function(evt) { this.onError(evt) };
	}

	onOpen(evt) {
		console.log("CONNECTED");
		doSend("WebSocket rocks");
	}

	onClose(evt) {
		console.log("DISCONNECTED");
	}

	onMessage(evt) {
		console.log('RESPONSE: ' + evt.data);
	}

	onError(evt) {
		console.log('ERROR: ' + evt.data);
	}

	doSend(message) {
		websocket.send(message);
		console.log("SENT: " + message);
	}

}