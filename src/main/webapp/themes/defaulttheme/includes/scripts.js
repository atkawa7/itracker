
/**
  * Toggle checked flag of checkboxes with name fieldname, inside same form as clicked-element.
  *
  */
	function toggleChecked(clicked, fieldname) {
		for (i = 0; i < clicked.form.elements[fieldname].length; i++) {
			clicked.form.elements[fieldname][i].checked = clicked.checked;
		}
	}
	
	function toggleProjectPermissionsChecked(clicked) {
		form = clicked.form;
		for (i = 0; i < form.elements.length; i++) {
			if (form.elements[i].name.indexOf(clicked.name) > -1) {
				form.elements[i].checked = clicked.checked;
			}
		}
	}