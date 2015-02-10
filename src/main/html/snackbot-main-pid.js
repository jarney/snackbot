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

    var rightSetpointList = [];
    var leftSetpointList = [];
    var rightActualList = [];
    var leftActualList = [];
    
    var angleActualList = [];
    var angleSetpointList = [];

    var currentLeftSpeed = 0;
    var currentRightSpeed = 0;
    var currentLeftSetpoint = 0;
    var currentRightSetpoint = 0;
    var currentAngle = 0;
    var currentAngleSetpoint = 0;

    var startTime = (new Date()).getTime();


    for (var i = -49; i <= 0; i += 1) {
        
        var pt = {
            x: i * 100,
            y: 0.0
        };
        rightSetpointList.push(pt);
        leftSetpointList.push(pt);
        rightActualList.push(pt);
        leftActualList.push(pt);
        angleActualList.push(pt);
        angleSetpointList.push(pt);
    }


    var chart;
    
   
    Highcharts.setOptions({
        global: {
            useUTC: false
        }
    });

    $('#pidGraph').highcharts({
        chart: {
            type: 'scatter',
            animation: false,
            marginRight: 10,
            events: {
                load: function () {
                    chart = this;
                }
            }
        },
        title: {
            text: 'Setpoint vs speed'
        },
        xAxis: {
            type: 'linear',
            tickPixelInterval: 150
        },
        yAxis: {
            title: {
                text: 'Speed (m/s)'
            },
            plotLines: [{
                value: 0,
                width: 1,
                color: '#808080'
            }],
            min: -1.5,
            max: 1.5
        },
        tooltip: {
            formatter: function () {
                return '<b>' + this.series.name + '</b><br/>' +
                    Highcharts.numberFormat(this.x, 2) + '<br/>' +
                    Highcharts.numberFormat(this.y, 2);
            }
        },
        legend: {
            enabled: false
        },
        exporting: {
            enabled: false
        },
        series: [
            {
            name: 'Left Speed',
            data: leftActualList
            },
            {
            name: 'Right Speed',
            data: rightActualList
            },
            {
            name: 'Left Setpoint',
            data: leftSetpointList
            },
            {
            name: 'Right Setpoint',
            data: rightSetpointList
            },
            {
            name: 'Angle',
            data: angleActualList
            },
            {
            name: 'Angle Setpoint',
            data: angleSetpointList
            }
    ]
    });
    
    snackbot.subscribe("position-update", function(msg) {
        currentLeftSpeed = msg.leftSpeed;
        currentRightSpeed = msg.rightSpeed;
        currentAngle = msg.angle;
        currentAngleSetpoint = msg.angleSetpoint;
        updateGraph();
    });

    $( "#pidSetSetpoint" ).button().click(function( event ) {
        $( "#pidRightSpeed").slider({value: 0});
        $( "#pidLeftSpeed").slider({value: 0});
    });

    $("#pidRightSpeed").slider({
        min: -2.3,
        max: 2.3,
        step: 0.05,
        change: function(evt, ui) {
            currentRightSetpoint = ui.value;
            snackbot.send({
                eventName: "Mover-Set-Speeds",
                leftSpeed: currentLeftSetpoint,
                rightSpeed: currentRightSetpoint
            });
            updateGraph();
        }
    });
    $("#pidLeftSpeed").slider({
        min: -2.3,
        max: 2.3,
        step: 0.05,
        change: function(evt, ui) {
            currentLeftSetpoint = ui.value;
            snackbot.send({
                eventName: "Mover-Set-Speeds",
                leftSpeed: currentLeftSetpoint,
                rightSpeed: currentRightSetpoint
            });
            updateGraph();
        }
    });
    
    function updateGraph() {
        var time = (new Date()).getTime() - startTime;
        chart.series[0].addPoint({x: time, y: currentLeftSpeed}, false, true);
        chart.series[1].addPoint({x: time, y: currentRightSpeed}, false, true);
        chart.series[2].addPoint({x: time, y: currentLeftSetpoint}, false, true);
        chart.series[3].addPoint({x: time, y: currentRightSetpoint}, false, true);
        chart.series[4].addPoint({x: time, y: currentAngle}, false, true);
        chart.series[5].addPoint({x: time, y: currentAngleSetpoint}, false, true);
        
        chart.redraw();
    }

    snackbot.subscribe("differential-drive-configuration", function(msg) {
        var cfg = msg.configuration;
        var leftMaxSpeed = cfg.leftWheelMaxRotationSpeed / 60.0 * cfg.leftWheelDiameter * 3.14159265;
        var rightMaxSpeed = cfg.rightWheelMaxRotationSpeed / 60.0 * cfg.rightWheelDiameter * 3.14159265;
        var totalMaxSpeed = (leftMaxSpeed > rightMaxSpeed) ? leftMaxSpeed : rightMaxSpeed;
        chart.yAxis[0].setExtremes(-totalMaxSpeed, totalMaxSpeed, true, false);
    });

}
