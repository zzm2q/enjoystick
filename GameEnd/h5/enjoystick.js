(function(win, doc) {

    var BASE = 'http://jenjoystick.sinaapp.com/';

    var ADDR_URL = 'ajax/room?callback=?&id=';

    var config = {
        minPlayer : 1,
        maxPlayer : 2,
        debug : true
    };

    var JWebsocketClient = function() {

        var _this = this;
        var websocket = null;

        this.connect = function(url) {
            if(websocket) {
                try { websocket.close(); } catch(e) {}
            }
            url = 'ws://' + url;
            websocket = new WebSocket(url);
            websocket.onopen = function() {
                _this.send = websocket.send;
                _this.close = websocket.close;
                websocket.onmessage = _this.onData;
                websocket.onclose = _this.onDisconnect;
                _this.onConnect && _this.onConnect();
            };
        };


        this.onConnect = null;
        this.onData = null;
        this.onDisconnect = null;
    };




    var EnjoyStick = (function() {

        var stopFetchAddr = false;

        var eventListeners = {
            'gameInit' : [],
            'playerJoin' : [],
            'playerDisconnect' : [],
            'playerExit' : [],
            'playerMessage' : []
        };

        var playerManager = new function() {

            var connectedAddr = {};
            var playerHash = {};

            this.count = 0;

            this.isConnected = function(addr) {
                return connectedAddr[addr];
            };

            this.join = function(player) {
                if(connectedAddr[player.addr]) {
                    return 'connected';
                }
                var hash;
                for(var i=1; i<=config.maxPlayer; i++) {
                    if(playerHash[i]) continue;
                    hash = i;
                    break;
                }
                if(!hash) {
                    throw "full";
                }
                playerHash[hash] = player;
                connectedAddr[player.addr] = true;
                player.index = hash;
                this.count ++;
                return true;
            };

            this.exit = function(player) {
                playerHash[player.index] = undefined;
                connectedAddr[player.addr] = undefined;
                this.count --;
            };
        };


        var Player = function(addr) {

            var _this = this;
            this.addr = addr;
            this.client = new JWebsocketClient();

            this.client.onConnect = function() {
                if(config.debug) {
                    console.log("playerJoin : " + addr);
                }
                playerManager.join(_this);
                api.fireEvent("playerJoin", { player : _this });
            };
            this.client.onDisconnect = function() {
                if(config.debug) {
                    console.log("playerExit : " + addr);
                }
                playerManager.exit(_this);
                api.fireEvent("playerExit", { player : _this });
            };
            this.client.onData = function(e) {
                if(config.debug) {
                    console.log("playerMessage : ", addr);
                    console.log("playerMessage : ", e.data);
                }
                api.fireEvent("playerMessage", { player : _this, msg : JSON.parse(e.data) });
            };
            this.client.connect(addr);
        };

        function fetchAddresses(id, listener) {
            $.getJSON(BASE + ADDR_URL + id, function(result) {
                if(stopFetchAddr) {
                    return;
                }
                if(result && result.data.addresses) {
                    listener(result.data.addresses);
                }
                setTimeout(function() {
                    fetchAddresses(id, listener);
                }, 3000);
            });
        }

        function init() {
            var id = 'id_' + Date.now();
            api.fireEvent('gameInit', { id : id });
            fetchAddresses(id, function(addrs) {
                for(var i=0; i<addrs.length; i++) {
                    if(!playerManager.isConnected(addrs[i])) {
                        new Player(addrs[i]);
                    }
                }
            });
            if(config.debug) {
                console.log("gameInit " + id);
            }
        }

        var api = {
            configure : function(cfg) {
                config = cfg;
            },
            init : function() {
                init();
            },
            fireEvent : function(type, e) {
                var listeners = eventListeners[type];
                if(!listeners) {
                    throw "illegal type '" + type + "'";
                }
                for(var i=0; i<listeners.length; i++) {
                    listeners[i](e);
                }
            },
            addEventListener : function(type, listener) {
                var listeners = eventListeners[type];
                if(!listeners) {
                    throw "illegal type '" + type + "'";
                }
                listeners.push(listener);
            },
            setState : function(state) {
                switch (state) {
                    case 'play' : stopFetchAddr = true; break;
                    case 'connecting' : stopFetchAddr = false; break;
                }
            }
        };

        return api;
    })();

    win.ES = EnjoyStick;

})(window, document);



