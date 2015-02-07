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
function sbConfigure(snackbot) {

    
    // The configuration of the robot is given to us by an event
    // immediately after connecting to it.  We need to subscribe to this
    // event and save the result when it is given.
    snackbot.subscribe("differential-drive-configuration", function(msg) {
        var cfg = msg.configuration;
        $("#configureLeftMotorId").val(cfg.leftMotorId);
        $("#configureRightMotorId").val(cfg.rightMotorId);
        $("#configureLeftEncoderTicksPerRevolution").val(cfg.leftEncoderTicksPerRevolution);
        $("#configureRightEncoderTicksPerRevolution").val(cfg.rightEncoderTicksPerRevolution);
        $("#configureLeftWheelDiameter").val(cfg.leftWheelDiameter);
        $("#configureRightWheelDiameter").val(cfg.rightWheelDiameter);
        $("#configureLeftEncoderCalibrationTicks").val(cfg.leftEncoderCalibrationTicks);
        $("#configureRightEncoderCalibrationTicks").val(cfg.rightEncoderCalibrationTicks);
        $("#configureWheelDistance").val(cfg.wheelDistance);
        $("#configureLeftWheelMaxRotationSpeed").val(cfg.leftWheelMaxRotationSpeed);
        $("#configureRightWheelMaxRotationSpeed").val(cfg.rightWheelMaxRotationSpeed);
        $("#configureDistanceTolerance").val(cfg.distanceTolerance);
        $("#configureAngleTolerance").val(cfg.angleTolerance);
        $("#configureDecelerationDistance").val(cfg.decelerationDistance);
        
        $("#configureLeftWheelPIDP").val(cfg.leftPID_P);
        $("#configureLeftWheelPIDI").val(cfg.leftPID_I);
        $("#configureLeftWheelPIDD").val(cfg.leftPID_D);
        
        $("#configureRightWheelPIDP").val(cfg.rightPID_P);
        $("#configureRightWheelPIDI").val(cfg.rightPID_I);
        $("#configureRightWheelPIDD").val(cfg.rightPID_D);
        
        $("#configureDistancePIDP").val(cfg.distancePID_P);
        $("#configureDistancePIDI").val(cfg.distancePID_I);
        $("#configureDistancePIDD").val(cfg.distancePID_D);
        
        $("#configureAnglePIDP").val(cfg.anglePID_P);
        $("#configureAnglePIDI").val(cfg.anglePID_I);
        $("#configureAnglePIDD").val(cfg.anglePID_D);
        
        if (cfg.leftWheelDirection > 0) {
            $("#configureLeftDirectionReverse").prop("checked", false);
        }
        else {
            $("#configureLeftDirectionReverse").prop("checked", true);
        }
        if (cfg.rightWheelDirection > 0) {
            $("#configureRightDirectionReverse").prop("checked", false);
        }
        else {
            $("#configureRightDirectionReverse").prop("checked", true);
        }
        
    });
    
    snackbot.subscribe("updateConfigurationDone", function(msg) {
        $("#configureSaveResult").text(msg.saveResult);
        //$("#configureSave").prop("disabled", false);
    });
    
    var leftDirection = 0;
    var rightDirection = 0;
    
    function trimString(v, sz) {
        v = v.toString();
        
        var ll = v.length;
        if (ll > sz) ll = sz;
        
        return v.substr(0, ll);
    }
    
    snackbot.subscribe("position-update", function(msg) {
        var lspd = "<p>" + trimString(msg.leftSpeed, 5) + "m/s</p>";
        var rspd = "<p>" + trimString(msg.rightSpeed, 5) + "m/s</p>";
        $("#leftSpeed2").html(lspd);
        $("#rightSpeed2").html(rspd);
        if ((leftDirection > 0 && msg.leftSpeed < 0) ||
            (leftDirection < 0 && msg.leftSpeed > 0)) {
            lspd = lspd + "<p>Left motor encoder is reversed</p>";
        }
        if ((rightDirection > 0 && msg.rightSpeed < 0) ||
            (rightDirection < 0 && msg.rightSpeed > 0)) {
            rspd = rspd + "<p>Right motor encoder is reversed</p>";
        }
        $("#leftSpeed").html(lspd);
        $("#rightSpeed").html(rspd);
    });

    var stop = function (evt) {
            snackbot.send({
                eventName: "driveMotor",
                leftMotor: 0.0,
                rightMotor: 0.0
            });
            leftDirection = 0;
            rightDirection = 0;
    };
    
    var updateConfiguration = function() {

        var rightWheelDirection;
        var leftWheelDirection;
        if ($("#configureRightDirectionReverse").is(":checked")) {
            rightWheelDirection = -1.0;
        }
        else {
            rightWheelDirection = 1.0;
        }
        if ($("#configureLeftDirectionReverse").is(":checked")) {
            leftWheelDirection = -1.0;
        }
        else {
            leftWheelDirection = 1.0;
        }
        
        var configuration = {
            leftMotorId: $("#configureLeftMotorId").val(),
            rightMotorId: $("#configureRightMotorId").val(),
            leftEncoderTicksPerRevolution: parseFloat($("#configureLeftEncoderTicksPerRevolution").val()),
            rightEncoderTicksPerRevolution: parseFloat($("#configureRightEncoderTicksPerRevolution").val()),
            leftWheelDiameter: parseFloat($("#configureLeftWheelDiameter").val()),
            rightWheelDiameter: parseFloat($("#configureRightWheelDiameter").val()),
            leftEncoderCalibrationTicks: parseFloat($("#configureLeftEncoderCalibrationTicks").val()),
            rightEncoderCalibrationTicks: parseFloat($("#configureRightEncoderCalibrationTicks").val()),
            wheelDistance: parseFloat($("#configureWheelDistance").val()),
            leftWheelMaxRotationSpeed: parseFloat($("#configureLeftWheelMaxRotationSpeed").val()),
            rightWheelMaxRotationSpeed: parseFloat($("#configureRightWheelMaxRotationSpeed").val()),
            distanceTolerance: parseFloat($("#configureDistanceTolerance").val()),
            angleTolerance: parseFloat($("#configureAngleTolerance").val()),
            decelerationDistance: parseFloat($("#configureDecelerationDistance").val()),
            leftWheelDirection: leftWheelDirection,
            rightWheelDirection: rightWheelDirection,
            
            leftPID_P: parseFloat($("#configureLeftWheelPIDP").val()),
            leftPID_I: parseFloat($("#configureLeftWheelPIDI").val()),
            leftPID_D: parseFloat($("#configureLeftWheelPIDD").val()),
            
            rightPID_P: parseFloat($("#configureRightWheelPIDP").val()),
            rightPID_I: parseFloat($("#configureRightWheelPIDI").val()),
            rightPID_D: parseFloat($("#configureRightWheelPIDD").val()),
            
            distancePID_P: parseFloat($("#configureDistancePIDP").val()),
            distancePID_I: parseFloat($("#configureDistancePIDI").val()),
            distancePID_D: parseFloat($("#configureDistancePIDD").val()),
            
            anglePID_P: parseFloat($("#configureAnglePIDP").val()),
            anglePID_I: parseFloat($("#configureAnglePIDI").val()),
            anglePID_D: parseFloat($("#configureAnglePIDD").val())

        };
        
        snackbot.send({
            eventName: "updateConfiguration",
            data: configuration
        });
        
    };
    

    // When each of the buttons is pressed, we move the motor
    // in the appropriate direction.  When the button is released,
    // we send a stop command.
    $( "#configureLeftFwdTest" ).button().mousedown(function( event ) {
            snackbot.send({
                eventName: "driveMotor",
                leftMotor: 1.0,
                rightMotor: 0.0
            });
            leftDirection = 1;
        });
    $( "#configureLeftRevTest" ).button().mousedown(function( event ) {
            snackbot.send({
                eventName: "driveMotor",
                leftMotor: -1.0,
                rightMotor: 0.0
            });
            leftDirection = -1;
        });
    $( "#configureRightFwdTest" ).button().mousedown(function( event ) {
            snackbot.send({
                eventName: "driveMotor",
                leftMotor: 0.0,
                rightMotor: 1.0
            });
            rightDirection = 1;
        });
    $( "#configureRightRevTest" ).button().mousedown(function( event ) {
            snackbot.send({
                eventName: "driveMotor",
                leftMotor: 0.0,
                rightMotor: -1.0
            });
            rightDirection = -1;
        });
        
    $( "#configureLeftFwdTest" ).button().mouseup(stop);
    $( "#configureLeftRevTest" ).button().mouseup(stop);
    $( "#configureRightFwdTest" ).button().mouseup(stop);
    $( "#configureRightRevTest" ).button().mouseup(stop);

    // Buttons to configure the
    // robot with appropriate values
    $( "#configureLeftRightReversed" ).button().click(function( event ) {
        var lmotor = $("#configureLeftMotorId").val();
        var rmotor = $("#configureRightMotorId").val();
        $("#configureLeftMotorId").val(rmotor);
        $("#configureRightMotorId").val(lmotor);
    });
        
    $( "#configureLeftDirectionReverse" ).button();
    $( "#configureRightDirectionReverse" ).button();
        
    $("#configureSave").button().click(function(event) {
        //$("#configureSave").prop("disabled", true);
        updateConfiguration();
    });


    // When each of the buttons is pressed, we move the motor
    // in the appropriate direction.  When the button is released,
    // we send a stop command.
    $( "#configureLeftFwdTestSpeed" ).button().mousedown(function( event ) {
            snackbot.send({
                eventName: "differentialDrive",
                leftMotor: 0.5,
                rightMotor: 0.0
            });
            leftDirection = 1;
        });
    $( "#configureLeftRevTestSpeed" ).button().mousedown(function( event ) {
            snackbot.send({
                eventName: "differentialDrive",
                leftMotor: -0.5,
                rightMotor: 0.0
            });
            leftDirection = -1;
        });
    $( "#configureRightFwdTestSpeed" ).button().mousedown(function( event ) {
            snackbot.send({
                eventName: "differentialDrive",
                leftMotor: 0,
                rightMotor: 0.5
            });
            rightDirection = 1;
        });
    $( "#configureRightRevTestSpeed" ).button().mousedown(function( event ) {
            snackbot.send({
                eventName: "differentialDrive",
                leftMotor: 0.0,
                rightMotor: -0.5
            });
            rightDirection = -1;
        });
        
    $( "#configureLeftFwdTestSpeed" ).button().mouseup(stop);
    $( "#configureLeftRevTestSpeed" ).button().mouseup(stop);
    $( "#configureRightFwdTestSpeed" ).button().mouseup(stop);
    $( "#configureRightRevTestSpeed" ).button().mouseup(stop);


        
}
