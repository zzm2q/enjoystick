var Tetris = function(id) {

    var _this = this;
    var root = $("#" + id);

    var state = 'none';
    var currentBlock = null;
    var sx = 5; sy = 0;
    var x=sx, y=sy;
    var maxX, maxY;
    var speed = 1, duration = 1000;
    var matrix;
    var setupInterval;
    var newBlockQueue = [];
    var eventListeners = {
        "inited" : [],
        "running" : [],
        "paused" : [],
        "over" : [],
        "newBlock" : [],
        "clearLine" : []
    };


    var keyHandle = {
        '38' : function() {
            _this.transform();
        },
        '40' : function() {
            _this.move('down');
        },
        '37' : function() {
            _this.move('left');
        },
        '39' : function() {
            _this.move('right');
        },
        '32' : function() {
            _this.splashDown();
        },
        '下' : function() {
            _this.move('down');
        },
        '左' : function() {
            _this.move('left');
        },
        '右' : function() {
            _this.move('right');
        },
        'B' : function() {
            _this.transform();
        },
        'A' : function() {
            _this.splashDown();
        }
    };

    /*
     00 01 02 03
     10 11 12 13
     20 21 22 23
     30 31 32 33
     */
    var typeHash = {
        'T' : [[0,0,0,1,0,2,1,1], [0,2,1,2,2,2,1,1], [0,1,1,0,1,1,1,2], [0,0,1,0,2,0,1,1]],
        'Z' : [[0,1,1,0,1,1,2,0], [1,0,1,1,2,1,2,2]],
        'S' : [[0,1,1,1,1,2,2,2], [1,2,1,1,2,0,2,1]],
        'I' : [[0,1,1,1,2,1,3,1], [2,0,2,1,2,2,2,3]],
        'O' : [[0,1,0,2,1,1,1,2]],
        'L' : [[0,1,1,1,2,1,2,2], [2,0,1,0,1,1,1,2], [0,0,0,1,1,1,2,1], [0,2,1,2,1,1,1,0]],
        'J' : [[0,1,1,1,2,1,2,0], [0,0,1,0,1,1,1,2], [0,2,0,1,1,1,2,1], [1,0,1,1,1,2,2,2]]
    };

    var types = (function() {
        var arr = [];
        for(var t in typeHash) {
            $.each(typeHash[t], function(_, pos) {
                arr.push({
                    type : t,
                    pos : pos
                });
            });
        }
        return arr;
    })();

    this.changeState = function(stat) {
        if(!eventListeners[stat]) {
            throw "illegal state : " + stat;
        }
        var from = state;
        state = stat;
        var listeners = eventListeners[state];
        for(var i=0; i<listeners.length; i++) {
            listeners[i]({ from: from, now : state});
        }
    };


    this.init = function(row, col) {
        var html = '<table width="100%" height="100%">';

        root.empty();

        for(var i=0; i<row; i++) {
            html += '<tr>';
            for(var j=0; j<col; j++) {
                html += '<td></td>';
            }
            html += '</tr>';
        }

        root.append(html);

        maxX = col;
        maxY = row;

        this.initMatrix();

        this.changeState("inited");
    };

    this.initMatrix = function() {
        matrix = [];
        root.find('tr').each(function(_, tr) {
            var tds = [];
            $(tr).find('td').each(function(_, td) {
                tds.push($(td));
            });
            matrix.push(tds);
        });
    };

    this.start = function() {
        setupInterval && clearInterval(setupInterval);
        this.newBlock();
        this.next();
        this.changeState("running");
        function setup() {
            if(_this.getState() === 'running') {
                _this.move('down');
            }
            setupInterval = setTimeout(setup, duration/speed);
        }
        setup();
    };

    this.newBlock = function() {
        newBlockQueue.push(this.randomBlock());
        for(var i=0; i<eventListeners.newBlock.length; i++) {
            eventListeners.newBlock[i](newBlockQueue);
        }
    };


    this.next = function() {
        var i, pos, gameover = false;
        currentBlock = newBlockQueue.shift();
        this.newBlock();
        pos = currentBlock.pos;
        x = sx;
        y = sy;
        for(i=0; i<pos.length; i+=2) {
            var by = pos[i]+y, bx = pos[i+1]+x;
            if(by <= 0) continue;
            var hasBlock = matrix[by-1][bx-1] && matrix[by-1][bx-1].data('block');
            if(hasBlock) {
                gameover = true;
                break;
            }
        }
        if(gameover) {
            this.changeState("over");
        } else {
            this.refresh(x, y);
        }
    };

    this.randomBlock = function() {
        var idx = parseInt(Math.random() * 1000) % types.length;
        return types[idx];
    };

    this.pause = function() {
        if(this.getState() === "running") {
            this.changeState("paused");
        }
    };

    this.resume = function() {
        if(this.getState() === 'paused') {
            this.changeState("running");
        }
    };

    this.transform = function() {
        if(!currentBlock && this.getState() != 'running') return;
        var len = typeHash[currentBlock.type].length;
        var idx = $.inArray(currentBlock.pos, typeHash[currentBlock.type]);
        if(idx+1 < len) {
            idx ++;
        } else {
            idx = 0;
        }
        if(this.isPlacable(x, y, typeHash[currentBlock.type][idx])) {
            this.clear(x, y, currentBlock.pos);
            currentBlock.pos = typeHash[currentBlock.type][idx];
            this.refresh(x, y);
        }
    };

    this.move = function(dir) {
        if(this.getState() == 'running' && _this.isMovable(dir)) {
            switch(dir) {
                case 'down'  :
                    this.refresh(x, y++);
                    break;
                case 'left'  :
                    this.refresh(x --, y);
                    break;
                case 'right' :
                    this.refresh(x ++, y);
                    break;
                default : throw 'illegal dir';
            }
            return true;
        } else if(dir === 'down') {
            this.block2wall();
            this.next();
        }
        return false;
    };

    this.refresh = function(originX, originY) {
        var i, pos = currentBlock.pos;
        this.clear(originX, originY, pos);
        for(i=0; i<pos.length; i+=2) {
            var ny = pos[i]+y, nx = pos[i+1]+x;
            try {
                matrix[ny-1][nx-1] && matrix[ny-1][nx-1].addClass("current");
            } catch(e) {
            }
        }
    };

    this.clear = function(x, y, pos) {
        for(var i=0; i<pos.length; i+=2) {
            var by = pos[i]+y, bx = pos[i+1]+x;
            try {
                matrix[by-1][bx-1] && matrix[by-1][bx-1].removeClass("current").removeClass("wall");
            }catch(e){
            }
        }
    };

    this.isMovable = function(dir) {
        var xx = x, yy = y, pos = currentBlock.pos;
        switch(dir) {
            case 'down'  :
                yy = y + 1;
                break;
            case 'left'  :
                xx = x - 1;
                break;
            case 'right' :
                xx = xx + 1;
                break;
            default : throw 'illegal dir : ' + dir;
        }
        return this.isPlacable(xx, yy, pos);
    };

    this.isPlacable = function(x, y, pos) {
        for(var i=0; i<pos.length; i+=2) {
            var by = pos[i]+y, bx = pos[i+1]+x;
            if(by > maxY || bx > maxX || bx <= 0) {
                return false;
            }
            if(by > 0 && matrix[by-1][bx-1] && matrix[by-1][bx-1].data('block')) {
                return false;
            }
        }
        return true;
    };

    this.block2wall = function() {
        var i, pos = currentBlock.pos;
        for(i=0; i<pos.length; i+=2) {
            var ny = pos[i]+y, nx = pos[i+1]+x;
            try {
                matrix[ny-1][nx-1].addClass('wall').removeClass("current");
                matrix[ny-1][nx-1].data('block', 1);
            } catch(e) {}
        }
        this.clearLine();
    };

    this.clearLine = function() {
        var line = 0;
        root.find('tr').each(function(_, tr) {
            var count = 0;
            $(tr).find('td').each(function(_, td) {
                if($(td).data('block')) {
                    count ++;
                }
            });
            if(count == maxX) {
                $(tr).remove();
                var html = '<tr>';
                for(var i=0; i<maxX; i++) {
                    html += '<td></td>';
                }
                html += '</tr>';
                root.find('table').prepend(html);
                _this.initMatrix();
                line ++;
            }
        });
        for(var i=0; i<eventListeners.clearLine.length; i++) {
            eventListeners.clearLine[i]({
                line : line
            });
        }
    };

    this.getState = function() {
        return state;
    };

    this.splashDown = function() {
        while(this.move('down')) {}
    };

    this.executeCmd = function(cmd) {
        if(this.getState() === "running") {
            if(cmd.type === "action") {
                var handle = keyHandle[cmd.action];
                handle && handle();
            }
            return true;
        }
        return false;
    };

    this.addEventListener = function(type, listener) {
        if(!eventListeners[type]) {
            throw "illegal type : " + type;
        }
        eventListeners[type].push(listener);
    };
};

