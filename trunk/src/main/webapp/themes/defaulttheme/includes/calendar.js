       // how reliable is this test?
       isIE = (document.all ? true : false);
     	 isDOM = (document.getElementById ? true : false);

       // Initialize arrays.
       var months = new Array("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul",
                              "Aug", "Sep", "Oct", "Nov", "Dec");
       var daysInMonth = new Array(31, 28, 31, 30, 31, 30, 31, 31,
                                   30, 31, 30, 31);
     	 var displayMonth = new Date().getMonth();
     	 var displayYear = new Date().getFullYear();
    	 var displayDivName;
    	 var displayElement;


      // get the true offset of anything on NS4, IE4/5 & NS6, even if it's in a table!
      function getAbsX(elt) {
          return (elt.x) ? elt.x : getAbsPos(elt,"Left");
      }

      function getAbsY(elt) {
          return (elt.y) ? elt.y : getAbsPos(elt,"Top");
      }

      function getAbsPos(elt,which) {
          iPos = 0;
          while (elt !== null) {
            iPos += elt["offset" + which];
            elt = elt.offsetParent;
          }
          return iPos;
      }

      function getDivStyle(divname) {
         var style;
         if (isDOM) {
            style = document.getElementById(divname).style;
         } else {
            style = isIE ? document.all[divname].style : document.layers[divname];
         }
         return style;
      }

      function hideElement(divname) {
         getDivStyle(divname).visibility = 'hidden';
      }

      // annoying detail: IE and NS6 store elt.top and elt.left as strings.
      function moveBy(elt,deltaX,deltaY) {
         elt.left = parseInt(elt.left) + deltaX;
         elt.top = parseInt(elt.top) + deltaY;
      }

      function toggleVisible(divname) {
         divstyle = getDivStyle(divname);
         if (divstyle.visibility == 'visible' || divstyle.visibility == 'show') {
            divstyle.visibility = 'hidden';
         } else {
            fixPosition(divname);
            divstyle.visibility = 'visible';
         }
      }

      function setPosition(elt,positionername,isPlacedUnder) {
         var positioner;
         if (isIE) {
            positioner = document.all[positionername];
         } else {
            if (isDOM) {
                positioner = document.getElementById(positionername);
            } else {
                // not IE, not DOM (probably NS4)
                // if the positioner is inside a netscape4 layer this will *not* find it.
                // I should write a finder function which will recurse through all layers
                // until it finds the named image...
                positioner = document.images[positionername];
            }
         }
         elt.left = getAbsX(positioner);
         elt.top = getAbsY(positioner) + (isPlacedUnder ? positioner.height : 0);
      }

       function getDays(month, year) {
            // Test for leap year when February is selected.
            if (1 == month) {
               return ((0 == year % 4) && (0 != (year % 100))) || (0 == year % 400) ? 29 : 28;
            } else {
               return daysInMonth[month];
            }
       }

       function getToday() {
          // Generate today's date.
          this.now = new Date();
          this.year = this.now.getFullYear();
          this.month = this.now.getMonth();
          this.day = this.now.getDate();
       }

       // Start with a calendar for today.
       today = new getToday();

       function newCalendar(eltName, attachedElement) {
      	    if (attachedElement) {
      	       if (displayDivName && displayDivName != eltName) {
                   hideElement(displayDivName);
               }
      	       displayElement = attachedElement;
      	    }
      	    displayDivName = eltName;
            today = new getToday();
            var parseYear = parseInt(displayYear + '');
            var newCal = new Date(parseYear,displayMonth,1);
            var day = -1;
            var startDayOfWeek = newCal.getDay();
            if ((today.year == newCal.getFullYear()) && (today.month == newCal.getMonth())) {
               day = today.day;
            }
            var intDaysInMonth = getDays(newCal.getMonth(), newCal.getFullYear());
            var daysGrid = makeDaysGrid(startDayOfWeek,day,intDaysInMonth,newCal,eltName);
      	    if (isIE) {
                var elt = document.all[eltName];
                elt.innerHTML = daysGrid;
            } else if (isDOM) {
                var elt = document.getElementById(eltName);
                elt.innerHTML = daysGrid;
      	    } else {
                var elt = document.layers[eltName].document;
                elt.open();
                elt.write(daysGrid);
                elt.close();
	          }
       }

       function incMonth(delta,eltName) {
      	   displayMonth += delta;
      	   if (displayMonth >= 12) {
      	     displayMonth = 0;
      	     incYear(1,eltName);
      	   } else if (displayMonth <= -1) {
      	     displayMonth = 11;
      	     incYear(-1,eltName);
      	   } else {
      	     newCalendar(eltName);
      	   }
    	 }

    	 function incYear(delta,eltName) {
          displayYear = parseInt(displayYear + '') + delta;
          newCalendar(eltName);
    	 }

    	 function makeDaysGrid(startDay,day,intDaysInMonth,newCal,eltName) {
    	    var daysGrid;
    	    var month = newCal.getMonth();
    	    var year = newCal.getFullYear();
    	    var isThisYear = (year == new Date().getFullYear());
    	    var isThisMonth = (day > -1)
    	    daysGrid = '<table border=1 cellspacing=0 cellpadding=2><tr><td bgcolor=#ffffff nowrap>';
    	    daysGrid += '<font face="courier new, courier" size=2>';
    	    daysGrid += '<a href="javascript:hideElement(\'' + eltName + '\')">x</a>';
    	    daysGrid += '&nbsp;&nbsp;';
    	    daysGrid += '<a href="javascript:incMonth(-1,\'' + eltName + '\')">&laquo; </a>';

    	    daysGrid += '<b>';
    	    if (isThisMonth) {
              daysGrid += '<font color=red>' + months[month] + '</font>';
          } else {
              daysGrid += months[month];
          }
    	    daysGrid += '</b>';

    	    daysGrid += '<a href="javascript:incMonth(1,\'' + eltName + '\')"> &raquo;</a>';
    	    daysGrid += '&nbsp;&nbsp;&nbsp;';
    	    daysGrid += '<a href="javascript:incYear(-1,\'' + eltName + '\')">&laquo; </a>';

    	    daysGrid += '<b>';
    	    if (isThisYear) {
              daysGrid += '<font color=red>' + year + '</font>';
          } else {
              daysGrid += '' + year;
          }
    	    daysGrid += '</b>';

    	    daysGrid += '<a href="javascript:incYear(1,\'' + eltName + '\')"> &raquo;</a><br>';
    	    daysGrid += '&nbsp;Su Mo Tu We Th Fr Sa&nbsp;<br>&nbsp;';
    	    var dayOfMonthOfFirstSunday = (7 - startDay + 1);
    	    for (var intWeek = 0; intWeek < 6; intWeek++) {
    	        var dayOfMonth;
    	        for(var intDay = 0; intDay < 7; intDay++) {
    	            dayOfMonth = (intWeek * 7) + intDay + dayOfMonthOfFirstSunday - 7;
          		    if (dayOfMonth <= 0) {
      	              daysGrid += "&nbsp;&nbsp; ";
          		    } else if (dayOfMonth <= intDaysInMonth) {
            		      var color = "blue";
            		      if (day > 0 && day == dayOfMonth) {
                          color="red";
                      }
            		      daysGrid += '<a href="javascript:setDay(';
            		      daysGrid += dayOfMonth + ',\'' + eltName + '\')" '
            		      daysGrid += 'style="color:' + color + '">';
            		      var dayString = dayOfMonth + "</a> ";
		                  if (dayString.length == 6) {
                          dayString = '0' + dayString;
                      }
            		      daysGrid += dayString;
          		    }
              }
      	      if (dayOfMonth < intDaysInMonth) {
                 daysGrid += "<br>&nbsp;";
              }
    	    }
	        return daysGrid + "</td></tr></table>";
	    }

      function setDay(day, eltName) {
      	  displayElement.value = day + "/" + (displayMonth + 1 < 10 ? "0" : "") + (displayMonth + 1) + "/" + displayYear;
      	  hideElement(eltName);
      }

      function toggleDatePicker(eltName,formElt) {
          var x = formElt.indexOf('.');
          var formName = formElt.substring(0,x);
          var formEltName = formElt.substring(x+1);
          newCalendar(eltName,document.forms[formName].elements[formEltName]);
          toggleVisible(eltName);
      }

      function fixPosition(divname) {
          divstyle = getDivStyle(divname);
          positionerImgName = divname + 'Pos';
          // hint: try setting isPlacedUnder to false
          isPlacedUnder = false;
          if (isPlacedUnder) {
              setPosition(divstyle,positionerImgName,true);
          } else {
              setPosition(divstyle,positionerImgName)
          }
      }
