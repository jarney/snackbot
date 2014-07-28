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


$.fn.sbUIGraph = function(options) {
    var defaults = {
        width: 800,
        height: 800,
        xmin: 0,
        xmax: 10,
        xticks: 10,
        ymin: 0,
        ymax: 10,
        yticks: 10,
        pointSets: []
    };

    
    var opts = $.extend( {}, defaults, options );

    var canvas = this.get()[0];
    canvas.width = opts.width;
    canvas.height = opts.height;
    var context = canvas.getContext("2d");

    this.setX = function(aMin, aMax) {
        opts.xmin = aMin;
        opts.xmax = aMax;
    };
    this.setY = function(aMin, aMax) {
        opts.ymin = aMin;
        opts.ymax = aMax;
    };

    this.repaint = function() {
        context.clearRect ( 0, 0, opts.width, opts.height);
        context.beginPath();
        context.moveTo(0, opts.height);
        context.lineTo(opts.width, opts.height);
        
        var tickLength = opts.width / opts.xticks;
        for (var i = 0; i < opts.xticks; i++) {
            context.moveTo(i*tickLength, opts.height);
            context.lineTo(i*tickLength, opts.height-8);
        }
        tickLength = opts.height / opts.yticks;
        for (var i = 0; i < opts.yticks; i++) {
            context.moveTo(0, i*tickLength);
            context.lineTo(8, i*tickLength);
        }

        for (var j = 0; j < opts.pointSets.length; j++) {
            var points = opts.pointSets[j];
            points = points.toArray();
            for (var i = 0; i < points.length; i++) {
                var cx = (points[i].x - opts.xmin) * opts.width / (opts.xmax - opts.xmin);
                var cy = (points[i].y - opts.ymin) * opts.height / (opts.ymax - opts.ymin);
                if (i === 0) {
                    context.moveTo(cx, cy);
                }
                else {
                    context.lineTo(cx, cy);
                }
            }
        }
        context.stroke();
        
    };
    
    this.repaint();
    
    return this;
    
};

