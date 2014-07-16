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

function SnackbotConnection(_onMessage, _stateChange) {
    this.wsUri = "ws://" + window.location.host + "/v1/biote/";

    this.stateChange = _stateChange;
    var stateChange = _stateChange;

    if (stateChange === undefined) {
        stateChange = function(eventType, evt) {};
    }

    this.onMessage = function(evt) {
        var evtObj = JSON.parse(evt.data);
        _onMessage(evtObj);
    };

    this.onOpen = function(evt)
    {
        stateChange("open", evt);
    };
    
    this.onClose = function(evt)
    {
        stateChange("close", evt);
    };
    this.onError = function(evt)
    {
        stateChange("error", evt);
    };
}

SnackbotConnection.prototype.connect = function() {
    this.stateChange("log", "connect: " + this.wsUri);
    this.websocket = new WebSocket(this.wsUri);
    this.websocket.onopen = this.onOpen;
    this.websocket.onclose = this.onClose;
    this.websocket.onmessage = this.onMessage;
    this.websocket.onerror = this.onError;
};
SnackbotConnection.prototype.disconnect = function() {
    this.stateChange("log", "disconnect: " + this.wsUri);
    this.websocket.close();
};

SnackbotConnection.prototype.send = function(message) {
    var jsonAsString = JSON.stringify(message);
    this.stateChange("log", "send: " + this.wsUri + ":" + jsonAsString);
    try {
        this.websocket.send(jsonAsString);
    }
    catch (ex) {
        this.stateChange("log", "send exception" + ex);
    }
};

