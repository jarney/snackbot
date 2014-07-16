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

$.fn.sbUICanvasMap = function(options) {
    
    var opts = $.extend( {}, $.fn.sbUICanvasMap.defaults, options );

    var width = opts.width;
    var halfWidth = width/2;
    var height = opts.height;
    var halfHeight = height/2;
    var canvas = this.get()[0];
    var destination = {x:1,y:0};
    
    canvas.width = width;
    canvas.height = height;
            
    var context = canvas.getContext("2d");
    
    function paint(list) {
 
        context.clearRect ( 0, 0, width, height);
        context.beginPath();
        
        var p0 = list[0];
        var scale = width/5;
        context.moveTo(p0.x * scale + halfWidth, p0.y * scale + halfHeight);
        
        var i;
        for (i = 1; i < list.length; i++) {
            var pn = list[i];
            context.lineTo(pn.x * scale + halfWidth, pn.y * scale + halfHeight);
        }
        context.lineTo(destination.x * scale + halfWidth, destination.y * scale + halfHeight);
        context.stroke();
    }
    
    function setDestination(pt) {
        destination = pt;
    }
    
    this.setDestination = setDestination;
    this.paint = paint;
    

    return this;
};

// Plugin defaults – added as a property on our plugin function.
$.fn.sbUICanvasMap.defaults = {
    width: 800,
    height: 800
};

