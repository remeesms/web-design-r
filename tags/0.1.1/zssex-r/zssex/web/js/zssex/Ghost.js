zssex.Ghost = zk.$extends(zk.Widget, {
	replaceWidget : function(b) {
		var c = this.firstChild, a = b.firstChild;
		while (c && a) {
			if (c._cloneAttrs) {
				c._cloneAttrs(a)
			}
			c = c.nextSibling;
			a = a.nextSibling
		}
		this.$supers(zssex.Ghost, "replaceWidget", arguments)
	},
	domClass_ : function(a) {
		return "zssexghost"
	},
	getPosition : zk.$void
});