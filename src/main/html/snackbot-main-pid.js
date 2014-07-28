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

function sbPIDTuning(snackbot) {

    var setAngle = 0;
    var setPointList = new CBuffer(50);
    var actualPointList = new CBuffer(50);
    var pointSets = [setPointList, actualPointList];
    var pi = 3.14159265;
    var twopi = pi*2;
    
    $("#setpoint").val("0.0");
    
    pidGrapher = $("#pidCanvas").sbUIGraph({pointSets: pointSets});
    pidGrapher.setY(-pi, pi);
    pidGrapher.repaint();
    
    snackbot.subscribe("position-update", function(msg) {
        var myangle = msg.angle;
        while (myangle > twopi) myangle -= twopi;
        while (myangle < -twopi) myangle += twopi;
        actualPointList.push({ x: msg.time, y: myangle});
        setPointList.push({x: msg.time, y: setAngle});

        pidGrapher.setX(msg.time - 50 * 100, msg.time);
        pidGrapher.repaint();
    });

    $( "#pidSetSetpoint" ).button().click(function( event ) {
        var angle = $("#setpoint").val();
        setAngle = angle * 3.14159265 / 180;
        snackbot.send({
            eventName: "move",
            x: 0.0,
            y: 0.0,
            theta: setAngle
        });
    });
    
}
