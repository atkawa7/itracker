


document.observe("dom:loaded", function() {
    $$('.deleteButton').each(function(elem) {
        elem.observe("click", function(event) { if (!confirm(elem.title + "?")) { event.stop() }; });
    });
    $$('a.HTTP_POST[href], .HTTP_POST a[href]').each(function(elem) {
        elem.observe("click", function(event) {
            var hr = elem.href
            var x = hr.indexOf("?")
            if (x>-1) {
                event.stop()
                var f = new Element("form")
                f.setAttribute("action", hr.substr(0,x))
                f.setAttribute("method", "post")
                $$("body").first().insert(f)
                var v = hr.substr(x+1).parseQuery()
                $H(v).each(function(x){
                    var h = new Element('input')
                    h.setAttribute('type', 'hidden')
                    h.setAttribute('name', x.key)
                    h.setAttribute('value',x.value)
                    f.insert(h)
                })
                f.submit()
            }
        });
    });
});

/**
  * Toggle checked flag of checkboxes with name field name, inside same form as clicked-element.
  *
  */
	function toggleChecked(clicked, fieldname) {
		for (var i = 0; i < clicked.form.elements[fieldname].length; i++) {
			clicked.form.elements[fieldname][i].checked = clicked.checked;
		}
	}
	
	function toggleProjectPermissionsChecked(clicked) {
		form = clicked.form;
		for (var i = 0; i < form.elements.length; i++) {
			if (form.elements[i].name.indexOf(clicked.name) > -1) {
				form.elements[i].checked = clicked.checked;
			}
		}
	}

	

    function toggleCalendar(cal) {

    	cal.toggleCalendar();
    }
    
