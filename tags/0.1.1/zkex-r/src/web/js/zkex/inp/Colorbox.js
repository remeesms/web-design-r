(function(){function a(){this._picker=new zkex.inp.PickerPop({_wgt:this});this._palette=new zkex.inp.PalettePop({_wgt:this});this.appendChild(this._picker);this.appendChild(this._palette)}var b=zkex.inp.Colorbox=zk.$extends(zk.Widget,{_open:false,$init:function(){this.$supers("$init",arguments);this.afterInit(a);this._currColor=new zkex.inp.Color()},$define:{disabled:function(c){var d=this.$n();if(d){var c=this._disabled;this[c?"domUnlisten_":"domListen_"](d,"onClick","_doBtnClick");jq(d)[c?"addClass":"removeClass"](this.getZclass()+"-disb")}}},setColor:function(d){var c=this._currColor.getHex();if(c!=d){this._currColor.setHex(d);this.onSize()}},getColor:function(){return this._currColor.getHex()},setValue:function(c){this.setColor(c)},getValue:function(){return this.getColor()},getZclass:function(){var c=this._zclass;return c!=null?c:"z-colorbox"},_syncPopupPosition:function(){zk(this.$n("pp")).position(this.$n(),this._getPosition())},_getPosition:function(){var c=this.parent;if(!c){return}if(c.$instanceof(zul.wgt.Toolbar)){return"vertical"==c.getOrient()?"end_before":"after_start"}return"after_start"},_syncShadow:function(c){if(!this._popupShadow){this._popupShadow=new zk.eff.Shadow(this.$n("pp"),{stackup:(zk.useStackup===undefined?zk.ie6_:zk.useStackup)})}this._popupShadow.sync()},_hideShadow:function(c){var d=this._popupShadow;if(d){d.hide()}},bind_:function(){this.$supers("bind_",arguments);if(!this._disabled){this.domListen_(this.$n(),"onClick","_doBtnClick")}var c=this.$n("palette-btn"),d=this.$n("picker-btn");if(c){this._palette.$n().style.display="none";this.domListen_(c,"onClick","openPalette")}if(d){this._picker.$n().style.display="none";this.domListen_(d,"onClick","openPicker")}zWatch.listen({onSize:this})},unbind_:function(){if(!this._disabled){this.domUnlisten_(this.$n(),"onClick","_doBtnClick")}zWatch.unlisten({onSize:this});if(this._popupShadow){this._popupShadow.destroy()}this.domUnlisten_(this.$n("palette-btn"),"onClick","openPalette").domUnlisten_(this.$n("picker-btn"),"onClick","openPicker");this.$supers("unbind_",arguments)},domClass_:function(e){var d=this.$supers("domClass_",arguments),c=this.getZclass();if(!e||!e.zclass){if(this._disabled){d+=" "+c+"-disb"}}return d},_doBtnClick:function(c){var d=this._open;if(this._open=!d){this.openPopup()}else{this.closePopup()}c.stop()},openPopup:function(){this._open=true;var e=this.$n(),c=this.$n("pp"),d=this._picker.$n();this.closePicker();this.openPalette();c.style.width=c.style.height="auto";c.style.position="absolute";c.style.overflow="auto";c.style.display="block";c.style.zIndex="88000";jq(c).zk.makeVParent();this._syncPopupPosition();this._syncShadow()},closePopup:function(){this._open=false;var d=this.$n(),c=this.$n("pp");jq(c).zk.undoVParent();this._hideShadow()},onChange:function(c){this.$n("currcolor").style.backgroundColor=c;this._currColor.setHex(c);this.fire("onChange",{color:c},{toServer:true},150)},onHide:function(){this._open=false;var d=this.$n(),c=this.$n("pp");jq(c).zk.undoVParent();this._hideShadow()},openPalette:function(){var d=this._palette.$n();if(!d){return}var e=this._picker.$n();if(e&&zk(e).isVisible()){this.closePicker()}d.style.display="block";jq(this.$n("pp")).addClass("z-palette-btn");this._syncPopupPosition();this._syncShadow();var c=this._palette;c.setColor(this._currColor.getHex());c.onShow()},closePalette:function(d){var c=this._palette.$n();if(!c||!zk(c).isVisible()){return}c.style.display="none";jq(this.$n("pp")).removeClass("z-palette-btn");if(d){this.closePopup()}},openPicker:function(){var c=this._picker.$n();if(!c){return}this.closePalette();c.style.display="block";jq(this.$n("pp")).addClass("z-picker-btn");this._syncPopupPosition();this._syncShadow();var d=this._picker;d.setColor(this._currColor.getHex());d.onShow()},closePicker:function(){var c=this._picker.$n();if(!c||!zk(c).isVisible()){return}c.style.display="none";jq(this.$n("pp")).removeClass("z-picker-btn")},onSize:function(){var g=this.$n("currcolor");if(g){var h=this.getWidth(),c=this.getHeight(),f=this._currColor;g.style.backgroundColor=f.getHex();if(h){var e=g.style.width;if(e!=h){g.style.width=h}}if(c){var d=g.style.height;if(d!=c){g.style.height=c}}}}})})();