zkex.menu.Fisheye=zk.$extends(zul.LabelImageWidget,{getZclass:function(){var a=this._zclass;return a!=null?a:"z-fisheye"},doMouseOver_:function(a){this.$supers("doMouseOver_",arguments);var c=this.$n(),b=this.$n("label");if(this._label!=""){b.style.display="block";b.style.visibility="hidden"}var d=this.parent;if(d){d.active=true;d._fixLab(this)}zk(b).cleanVisibility()},doMouseOut_:function(a){this.$supers("doMouseOut_",arguments);this.$n("label").style.display="none"},bind_:function(){this.$supers(zkex.menu.Fisheye,"bind_",arguments);var c=this.$n(),a=this.$n("img"),b=this.$n("label");zk(c).disableSelection();this._mh=zk(b).sumStyles("tb",jq.margins);this._mw=zk(b).sumStyles("lr",jq.margins)}});