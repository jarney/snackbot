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

$.fn.sbUICanvasDial = function(options) {
    
    var opts = $.extend( {}, $.fn.sbUICanvasDial.defaults, options );

    var r = opts.r;
    var theta = opts.theta;
    var dragInput = opts.dragInput;
    var width = opts.width;
    var halfWidth = width/2;
    var height = opts.height;
    var halfHeight = height/2;
    var onValueChange = opts.onValueChange;
    var logger = opts.logger;
    
    var dragState = {
        startx: undefined,
        starty: undefined,
        x: undefined,
        y: undefined,
        timer: undefined,
        canvas: undefined,
        context: undefined
    };
    dragState.canvas = this.get()[0];
    dragState.canvas.width = width;
    dragState.canvas.height = height;
            
    dragState.context = dragState.canvas.getContext("2d");
    
    
    function dragRepaint() {
        if (dragState.startx === undefined) {
            return;
        }

        var dx = dragState.x - dragState.startx;
        var dy = dragState.y - dragState.starty;

        var scale = halfWidth;
        dx = dx / scale;
        dy = dy / scale;

        var intensity = Math.sqrt(dx*dx + dy*dy);
        var dir = Math.atan2(dx / intensity , dy / intensity);

        paint(intensity, dir);

    }
    
    function paint(r, theta) {
        if (r > 1) {
            r = 1;
        }
        
        onValueChange(r, theta);

        r = r * width/2;
        var dx1 = r * .2 * Math.sin(theta + Math.PI/8);
        var dy1 = r * .2 * Math.cos(theta + Math.PI/8);

        var dx2 = r * Math.sin(theta);
        var dy2 = r * Math.cos(theta);

        var dx3 = r * .2 * Math.sin(theta - Math.PI/8);
        var dy3 = r * .2 * Math.cos(theta - Math.PI/8);

        dragState.context.clearRect ( 0, 0, width, height);
        dragState.context.beginPath();
        dragState.context.moveTo(halfWidth, halfHeight);
        dragState.context.lineTo(halfWidth + dx1, halfHeight + dy1);
        dragState.context.lineTo(halfWidth + dx2, halfHeight + dy2);
        dragState.context.lineTo(halfWidth + dx3, halfHeight + dy3);
        dragState.context.lineTo(halfWidth, halfHeight);
        dragState.context.closePath();
        dragState.context.moveTo(width, halfHeight);
        dragState.context.arc(halfWidth, halfHeight, halfWidth, 0, Math.PI*2);
        dragState.context.stroke();
    }
    
    function dragStart(evt) {
        dragState.startx = evt.clientX;
        dragState.starty = evt.clientY;
        dragState.x = dragState.startx;
        dragState.y = dragState.starty;
        dragState.timer = window.setInterval(dragRepaint, 100);
        return false;
    }
    function dragMove(evt) {
        dragState.x = evt.clientX;
        dragState.y = evt.clientY;
        return false;
    }
    function dragStop(evt) {
        dragState.startx = undefined;
        dragState.starty = undefined;
        dragState.x = undefined;
        dragState.y = undefined;
        window.clearInterval(dragState.timer);
        paint(0, 0);
        return false;
    }

    function touchStart(evt) {
        evt.preventDefault();
        var targetEvent = evt.originalEvent.touches[0] || evt.originalEvent.changedTouches[0];
        dragStart(targetEvent);
        return false;
    }
    function touchEnd(evt) {
        evt.preventDefault();
        dragStop(evt);
        return false;
    }
    function touchMove(evt) {
        evt.preventDefault();
        var targetEvent = evt.originalEvent.touches[0] || evt.originalEvent.changedTouches[0];
        dragMove(targetEvent);
        return false;
    }
    
    if (dragInput) {
        $("#control_canvas")
                .mousedown(dragStart)
                .mousemove(dragMove)
                .mouseup(dragStop)
                .bind("touchstart", touchStart)
                .bind("touchend", touchEnd)
                .bind("touchmove", touchMove);
 


    }

    paint(r, theta);

    return this;
};

// Plugin defaults – added as a property on our plugin function.
$.fn.sbUICanvasDial.defaults = {
    r: 0,
    theta: 0,
    dragInput: false,
    width: 800,
    height: 800,
    onValueChange: function(r,theta) {},
    logger: function(msg) {}
};

