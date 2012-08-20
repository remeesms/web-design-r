function(b){
	var c=this.uuid;
	b.push("<div",this.domAttrs_(),'><a id="',
			this.uuid,'-fo" href="javascript:" class="zswidget-ifocus"></a><div id="',
			c,'-cave" class="zswidget-cave"><div id="',c,'-real" class="zswidget-real">');
	for(var a=this.firstChild;a;a=a.nextSibling){
		a.redraw(b)
	}
	b.push("</div></div></div>")
};