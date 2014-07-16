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

function sbTelemetry(snackbot) {
    var mapPainter;

    var positionList = [];
    
    mapPainter = $("#map_canvas").sbUICanvasMap({
        width: 800,
        height: 800
    });

    snackbot.subscribe("position-update", function(msg) {
            var pt = { x: msg.x, y: msg.y };
            positionList.push(pt);
            mapPainter.paint(positionList);
    });
    $( "#move" ).button().click(function( event ) {
            snackbot.send({
                eventName: "move"
            });

        });
    $( "#subscribe" ).button().click(function( event ) {
        });
    $( "#reset" ).button().click(function( event ) {
            snackbot.send({
                eventName: "reset",
                x: 0,
                y: 0,
                theta: 0
            });
            positionList = [];
            var pt = { x: 0, y: 0 };
            positionList.push(pt);
            mapPainter.setDestination({x: 1, y:0});
            mapPainter.paint(positionList);
        });
}