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

$(
    function() {
        $( "#forward" ).button().click(function( event ) {
                var obj = {
                    eventName: "forward"
                };
                var jsonAsString = JSON.stringify(obj);
                doSend(jsonAsString);
            });
        $( "#reverse" ).button().click(function( event ) {
                var obj = {
                    eventName: "reverse"
                };
                var jsonAsString = JSON.stringify(obj);
                doSend(jsonAsString);
            });
        $( "#left" ).button().click(function( event ) {
                var obj = {
                    eventName: "left"
                };
                var jsonAsString = JSON.stringify(obj);
                doSend(jsonAsString);
            });
        $( "#right" ).button().click(function( event ) {
                var obj = {
                    eventName: "right"
                };
                var jsonAsString = JSON.stringify(obj);
                doSend(jsonAsString);
            });
        $("#tabcontrol").button();
        $("#tabtelemetry").button();
        $("#tabcommunications-log").button();
        $("#tabconfiguration").button();
        $("#maintabs").sbUITabs();

        $(window).resize(function(){
            $("#control_canvas").sbUIFixAspectRatio();
            $("#control_canvas").sbUICenter();
        });
        
        $("#control_canvas").sbUIFixAspectRatio();
        $("#control_canvas").sbUICenter();
        
        
        var connection = new SnackbotConnection("localhost", 
            function (msg) {
                $("#log").sbLog("msg: " + msg);
            },
            function (type, evt) {
                $("#log").sbLog(type + "Event: " + evt);
            }
        );
            
        connection.connect();
        connection.send({evt: "something-wierd"});

        function sendValueChange(r, theta) {
            var x = r * Math.cos(theta);
            var y = r * Math.sin(theta);
            
            connection.send({
                eventName: "differentialDrive",
                left: y+x,
                right: y-x
            });
        }
        
        
        $("#control_canvas").sbUICanvasDial({
                r: 0,
                theta: 0,
                width: 800,
                height: 800,
                dragInput: true,
                onValueChange: sendValueChange
            });

    }
);
