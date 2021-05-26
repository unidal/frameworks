$(document).delegate('.edit', 'click', function(e) {
	var anchor = this, el = $(anchor), id = 'config-' + el.attr('data-status');
	var div = document.getElementById(id);
	var tr = div.parentNode.parentNode;
	
	if (tr.style.display=='none') {
		tr.style.display='';
	} else {
		tr.style.display='none';
	}
});
