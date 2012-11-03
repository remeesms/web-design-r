/**
 * @see zk.Page.prototype.redraw (the same as mode of DIV)
 */
function(out) {
    // abcde
	    out.push('<div', this.domAttrs_(), '>');

	    for (var w = this.firstChild; w; w = w.nextSibling) {
	        w.redraw(out);
	    }
	    
        out.push('</div>');
	        
};


