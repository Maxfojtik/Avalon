
class CookieMonster {

	constructor() {
		
		if (localStorage.sessionId && localStorage.sessionId != "undefined") {
			this.sessionId = localStorage.sessionId;
		}
		else {
			this.sessionId = generateSessionID();
			localStorage.sessionId = this.sessionId;
		}

	}

	setPlayerName(name) {
		localStorage.playerName = name;
	}
	getPlayerName() { return localStorage.playerName; }

}
