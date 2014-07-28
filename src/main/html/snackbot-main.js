/* 
 * The MIT License
 *
 * Copyright 2014 Jon Arney, Ensor Robotics.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */


$.fn.sbLog = function(message) {
    var pre = document.createElement("p");
    pre.style.wordWrap = "break-word";
    pre.innerHTML = message;
    this.prepend(pre);
};

var snackbot = {
    subscribers: {},
    subscribe: function(eventName, callback) {
        if (this.subscribers[eventName] === undefined) {
            this.subscribers[eventName] = [];
        }
        this.subscribers[eventName].push(callback);
    },
    publish: function(eventName, msg) {
        eventList = this.subscribers[eventName];
        if (eventList === undefined) {
            return;
        }
        for (var i = 0; i < eventList.length; i++) {
            eventList[i](msg);
        }
    },
    connection: undefined,
    send: function(msg) {
        if (this.connection === undefined) {
            return;
        }
        this.connection.send(msg);
    }
};


$(
    function() {
        $("#tabcontrol").button();
        $("#tabtelemetry").button();
        $("#tabcommunications-log").button();
        $("#tabconfiguration").button();
        $("#maintabs").sbUITabs();
        
        sbControl(snackbot);
        sbTelemetry(snackbot);
        sbConfigure(snackbot);
        sbPIDTuning(snackbot);
        
        snackbot.connection = new SnackbotConnection(
            function (msg) {
                snackbot.publish(msg.eventName, msg);
            },
            function (type, evt) {
                $("#log").sbLog(type + "Event: " + evt);
                snackbot.publish(type, evt);
            }
        );
        snackbot.subscribe("open", function(msg) {
            snackbot.send({
                eventName: "subscribe"
            });
        });
    
        snackbot.connection.connect();
        
    }
);
