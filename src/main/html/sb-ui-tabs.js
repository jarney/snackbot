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

$.fn.sbUITabs = function() {

    var showTab = function() {

        var selectedId = getHash( this.getAttribute('href') );

        // Highlight the selected tab, and dim all others.
        // Also show the selected content div, and hide all others.
        for ( var id in contentDivs ) {
            if ( id === selectedId ) {
                $("#"+tabLinks[id].id).addClass("tabSelected");
                $("#"+tabLinks[id].id).removeClass("tabNotSelected");
                contentDivs[id].className = 'tabItem';
            } else {
                $("#"+tabLinks[id].id).addClass("tabNotSelected");
                $("#"+tabLinks[id].id).removeClass("tabSelected");
                contentDivs[id].className = 'tabItem tabItemHide';
            }
        }

      // Stop the browser following the link
      return false;
    };

    function getFirstChildWithTagName( element, tagName ) {
      for ( var i = 0; i < element.childNodes.length; i++ ) {
        if ( element.childNodes[i].nodeName === tagName ) return element.childNodes[i];
      }
    }

    function getHash( url ) {
      var hashPos = url.lastIndexOf ( '#' );
      return url.substring( hashPos + 1 );
    }

    var tabLinks = new Array();
    var contentDivs = new Array();

    // Grab the tab links and content divs from the page
    //var tabListItems = document.getElementById('tabs').childNodes;
    var tabListItems = this.find("div a").get();
    
    for ( var i = 0; i < tabListItems.length; i++ ) {
        var tabLink = tabListItems[i];
        var id = getHash( tabLink.getAttribute('href') );
        tabLinks[id] = tabLink;
        contentDivs[id] = document.getElementById( id );
    }

    // Assign onclick events to the tab links, and
    // highlight the first tab
    var i = 0;

    for ( var id in tabLinks ) {
        tabLinks[id].id = "tab" + id;
        $("#"+tabLinks[id].id).button().
                click(showTab).
                focus(function() {this.blur();});
        
        if ( i === 0 ) {
            $("#"+tabLinks[id].id).addClass("tabSelected");
            $("#"+tabLinks[id].id).removeClass("tabNotSelected");
            contentDivs[id].className = 'tabItem';
        }
        else {
            $("#"+tabLinks[id].id).addClass("tabNotSelected");
            $("#"+tabLinks[id].id).removeClass("tabSelected");
            contentDivs[id].className = 'tabItem tabItemHide';
        }
        i++;
    }

};