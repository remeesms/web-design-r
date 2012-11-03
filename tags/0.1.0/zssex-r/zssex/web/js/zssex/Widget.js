(function() {
    function g(j, l, o) {
        var p = j.control, m = j.opts.type = p._dragType, k = o.domTarget, n = jq(j.node), h = jq('<div id="zk_ddghost"></div>').attr("style", n.attr("style")).addClass("zswidget zswidget-drag z-drag-ghost").width(n.width() + "px").height(n.height() + "px").appendTo(p._sheet.sp.comp);
        j._orgcursor = document.body.style.cursor;
        document.body.style.cursor = m == "onWidgetSize" ? "crosshair" : "move";
        if("onWidgetMove" == m) {
            var i = zk(j.node).revisedOffset();
            j.opts.innerOffset = [o.pageX - i[0], o.pageY - i[1]];
            document.body.style.cursor = "move"
        } else {
            document.body.style.cursor = "crosshair"
        }
        jq(p.getDragNode()).addClass("z-dragged");
        p._dragging = true;
        return h[0]
    }

    function b(m, l, i) {
        var k = i - m[0], h = l.offsetLeft + l.clientHeight - 20;
        if(k > h) {
            k = h
        }
        var j = l.offsetLeft + l.clientWidth - k;
        return [k, j]
    }

    function e(m, j, i) {
        var h = i - m[1], l = j.offsetTop + j.clientHeight - 20;
        if(h > l) {
            h = l
        }
        var k = j.offsetTop + j.clientHeight - h;
        return [h, k]
    }

    function d(k, j, h) {
        var i = 20;
        return Math.max(i - 2, h - k[1] - 2)
    }

    function c(k, j, i) {
        var h = 20;
        return Math.max(h - 2, i - k[0] - 2)
    }

    function f(G, A, u) {
        var p = G.control, D = p.getDragNode(), B = G.node, j = p._dragType, z = p._dragDir, t = G.opts;
        if("onWidgetSize" == j && z) {
            var m = G.control._sheet, s = p._focusBorderSize, w = p._focus;
            if("N" == z) {
                var C = zk(m.sp.comp).revisedOffset(), E = zk(D).revisedOffset(), h = e(C, D, u.pageY), v = h[0], l = h[1];
                jq(B).height(l + "px");
                t.x1 = E[0] + s;
                t.x2 = t.x1 + D.clientWidth - (s * 2);
                t.y1 = v + C[1];
                t.y2 = t.y1 + l - s;
                return [B.offsetLeft, v]
            } else {
                if("EN" == z) {
                    var C = zk(m.sp.comp).revisedOffset(), E = zk(D).revisedOffset(), k = B.offsetLeft, h = e(C, D, u.pageY), v = h[0], l = h[1], r = c(zk(B).revisedOffset(), D, u.pageX);
                    jq(B).height(l + "px").width(r + "px");
                    t.x1 = E[0] + s;
                    t.x2 = t.x1 + r - 4;
                    t.y1 = v + C[1];
                    t.y2 = t.y1 + l - s;
                    return [k, v]
                } else {
                    if("E" == z) {
                        var C = zk(B).revisedOffset(), E = zk(D).revisedOffset(), k = B.offsetLeft, v = B.offsetTop, r = c(C, D, u.pageX);
                        jq(B).width(r + "px");
                        t.x1 = E[0] + s;
                        t.y1 = E[1] + s;
                        t.x2 = t.x1 + r - 4;
                        t.y2 = t.y1 + D.clientHeight - (s * 2);
                        return [k, v]
                    } else {
                        if("ES" == z) {
                            var C = zk(B).revisedOffset(), E = zk(D).revisedOffset(), k = B.offsetLeft, v = B.offsetTop, r = c(C, D, u.pageX), l = d(C, D, u.pageY);
                            jq(B).width(r + "px").height(l + "px");
                            t.x1 = E[0] + s;
                            t.y1 = E[1] + s;
                            t.x2 = t.x1 + r - 4;
                            t.y2 = t.y1 + l - 4;
                            return [k, v]
                        } else {
                            if("S" == z) {
                                var C = zk(B).revisedOffset(), E = zk(D).revisedOffset(), k = B.offsetLeft, v = B.offsetTop, l = d(C, D, u.pageY);
                                jq(B).height(l + "px");
                                t.x1 = E[0] + s;
                                t.y1 = E[1] + s;
                                t.x2 = t.x1 + D.clientWidth - (s * 2);
                                t.y2 = t.y1 + l - 4;
                                return [k, v]
                            } else {
                                if("WS" == z) {
                                    var C = zk(m.sp.comp).revisedOffset(), E = zk(D).revisedOffset(), l = d(zk(B).revisedOffset(), D, u.pageY), J = b(C, D, u.pageX), k = J[0], r = J[1];
                                    jq(B).width(r + "px").height(l + "px");
                                    t.x1 = k + C[0];
                                    t.y1 = E[1] + s;
                                    t.x2 = t.x1 + r - 4;
                                    t.y2 = t.y1 + l - 4;
                                    return [k, D.offsetTop]
                                } else {
                                    if("W" == z) {
                                        var C = zk(m.sp.comp).revisedOffset(), E = zk(D).revisedOffset(), J = b(C, D, u.pageX), v = D.offsetTop, k = J[0], r = J[1];
                                        jq(B).width(r + "px");
                                        t.x1 = k + C[0];
                                        t.y1 = E[1] + s;
                                        t.x2 = t.x1 + r - 4;
                                        t.y2 = t.y1 + D.clientHeight - (s * 2);
                                        return [k, v]
                                    } else {
                                        if("WN" == z) {
                                            var C = zk(m.sp.comp).revisedOffset(), E = zk(D).revisedOffset(), J = b(C, D, u.pageX), h = e(C, D, u.pageY), v = h[0], l = h[1], k = J[0], r = J[1];
                                            jq(B).width(r + "px").height(l + "px");
                                            t.x1 = k + C[0];
                                            t.y1 = v + C[1];
                                            t.x2 = t.x1 + r - 4;
                                            t.y2 = t.y1 + l - s;
                                            return [k, v]
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } else {
            var i = G.opts.innerOffset, m = G.control._sheet, F = m.$n("sp"), C = zk(m.sp.comp).revisedOffset(), I = m.leftWidth, H = m.topHeight, q = u.pageX - C[0] - i[0] + F.scrollLeft, o = u.pageY - C[1] - i[1] + F.scrollTop;
            t.x1 = Math.max(u.pageX - i[0], I);
            t.y1 = Math.max(u.pageY - i[1], H);
            t.x2 = t.x1 + D.clientWidth;
            t.y2 = t.y1 + D.clientHeight;
            return [q < I ? I : q, o < H ? H : o]
        }
    }

    function a(s, q) {
        var n = s.control, p = s.opts, k = s.control._sheet, x = k.sheetid, y = k.leftWidth, h = k.topHeight, r = zk(k.dp.comp).revisedOffset(), m = s.node.firstChild, j = k.custColWidth, l = k.custRowHeight, w = zss.SSheetCtrl._calCellPos(k, p.x1, p.y1, false), v = zss.SSheetCtrl._calCellPos(k, p.x2, p.y2, false), i = p.x1 - r[0] - y - j.getStartPixel(w[1]) + 1, o = p.y1 - r[1] - h - l.getStartPixel(w[0]) + 1;
        var t = v[1] == w[1], u = v[0] == w[0];
        if(t) {
            dx2 = i + (p.x2 - p.x1)
        } else {
            dx2 = p.x2 - r[0] - y - j.getStartPixel(v[1]) + 1
        }
        if(u) {
            dy2 = o + (p.y2 - p.y1)
        } else {
            dy2 = p.y2 - r[1] - h - l.getStartPixel(v[0]) + 1
        }
        n._sheet._wgt.fireMoveWidgetEvt(n.getType(), s.opts.type, s.control.getId(), Math.round(i), Math.round(o), Math.round(dx2), Math.round(dy2), w[1], w[0], v[1], v[0]);
        s.control._dragging = false;
        zcss.removeRule("#" + x + " .zswidget-focus", x + "-sheet")
    }


    zssex.Widget = zk.$extends(zul.Widget, {
        _leftPos: null,
        _topPos: null,
        _dragging: false,
        _delaySetDragMove: null,
        _dragType: null,
        _dragDir: null,
        _focus: false,
        _focusBorderSize: 6,

        $define: {
            col: function() {
                this.adjustLocation()
            },

            row: function() {
                this.adjustLocation()
            },

            left: function() {
                this.adjustLocation()
            },

            top: function() {
                this.adjustLocation()
            },

            visible: function(h) {
                var i = this.$n();
                if(i) {
                    jq(i).css("visibility", h ? "visible" : "hidden")
                }
            },

            sizable: null,
            movable: null,
            focusable: null,
            id: null,
            type: null
        },
        
        adjustLocation: function(s) {
            var i = this.$n(), p = this._sheet;
            if( !i || !p) {
                return
            }
            var j = this.getCol(), t = this.getRow(), q = p.custColWidth, u = p.custRowHeight, m = q.getSize(j), h = u.getSize(t), k = this._leftPos = p.leftWidth + q.getStartPixel(j) + (this.getLeft() > m ? m : this.getLeft()), r = this._topPos = p.topHeight + u.getStartPixel(t) + (this.getTop() > h ? h : this.getTop()), o = this._focus, l = this._focusBorderSize;
            jq(i).css({
                left: o ? jq.px(k - l) : jq.px(k),
                top: o ? jq.px(r - l) : jq.px(r)
            });
            if(s) {
                jq(i).css("visibility", "visible")
            }
        },

        _cloneAttrs: function(h) {
            if(h._id == this._id) {
                h._sheet = this._sheet;
                h._leftPos = this._leftPos;
                h._topPos = this._topPos
            }
        },
        
        /**
         * 在DefaultWidgetHandler#onLoadOnDemand中會向Spreadsheet widget發出setAttr:redrawWidget的cmd，
         * Spreadsheet widget的redrawWidget會調用此函數。
         * 在此函數中進行Spreadsheet的子widget的繪製。
         * （包括mold的執行和bind_）
         * （與普通zk component不同，他們的mold的執行在父component的mold執行中調用，
         * 但是Spreadsheet不會調用自component的mold函數）
         */
        redrawWidgetTo: function(h) {
            if( !h) {
                return
            }
            this._sheet = h;
            var i = h.$n("wp");
            jq(i).append(this.redrawHTML_());

            // bind
            for(var child = this.firstChild; child; child = child.nextSibling) {
                child.bind(this.desktop, null);
            }
            
            // afterRedrawHTML_事件
            for(var child = this.firstChild; child; child = child.nextSibling) {
                child.afterRedrawHTML_ && child.afterRedrawHTML_();
            }

            this.clearCache();
            this.adjustLocation(h, true);
            if(this.isSizable() || this.isMovable()) {
                this.setDraggable(true)
            }
        },

        getDragOptions_: function(h) {
            h.ghosting = g;
            h.constraint = f;
            h.endeffect = a;
            return h
        },

        ignoreDrag_: function(h) {
            return !this._dragType
        },

        doBlur_: function(h) {
            jq(this.$n()).removeClass("zswidget-focus").css({
                left: jq.px(this._leftPos),
                top: jq.px(this._topPos)
            });
            this._updateListenFocus(false)
        },

        _onSheetFocus: function(h) {
            var i = h.data;
            if(i) {
                if(i.ctrl != this) {
                    this.doBlur_()
                }
            } else {
                this.doBlur_()
            }
        },

        doMouseMove_: function(A) {
            if( !this._dragging) {
                var r = A.domTarget, i = this.$n(), k = this._sheet.sheetid, s = k + "-sheet", j = "#" + k;
                if((r == i || r == this.$n("cave")) && this.isSizable()) {
                    this._dragType = "onWidgetSize";
                    var t = zk(i).revisedOffset(), C = this._focusBorderSize, v = A.pageX - t[0], u = A.pageY - t[1], z = i.clientWidth, o = i.clientHeight, q = C, p = z - C, B = C, m = o - C;
                    if(v <= q && u <= B) {
                        this._dragDir = "WN";
                        zcss.setRule(j + " .zswidget-focus", "cursor", "nw-resize", true, s)
                    } else {
                        if(v <= q && u <= m) {
                            this._dragDir = "W";
                            zcss.setRule(j + " .zswidget-focus", "cursor", "w-resize", true, s)
                        } else {
                            if(v <= q && u > m) {
                                this._dragDir = "WS";
                                zcss.setRule(j + " .zswidget-focus", "cursor", "sw-resize", true, s)
                            } else {
                                if(v <= p && u <= B) {
                                    this._dragDir = "N";
                                    zcss.setRule(j + " .zswidget-focus", "cursor", "n-resize", true, s)
                                } else {
                                    if(v > p && u <= B) {
                                        this._dragDir = "EN";
                                        zcss.setRule(j + " .zswidget-focus", "cursor", "ne-resize", true, s)
                                    } else {
                                        if(v > p && u <= m) {
                                            this._dragDir = "E";
                                            zcss.setRule(j + " .zswidget-focus", "cursor", "e-resize", true, s)
                                        } else {
                                            if(v <= p && u > m) {
                                                this._dragDir = "S";
                                                zcss.setRule(j + " .zswidget-focus", "cursor", "s-resize", true, s)
                                            } else {
                                                this._dragDir = "ES";
                                                zcss.setRule(j + " .zswidget-focus", "cursor", "se-resize", true, s)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    this._delaySetDragMove = jq.now();
                } else {
                    if(jq.isAncestor(this.$n("real"), r)) {
                        var l = this._delaySetDragMove;
                        if( !l || (l && (jq.now() - l) > 500)) {
                            this._dragType = "onWidgetMove";
                            this._delaySetDragMove = null;
                            zcss.setRule(j + " .zswidget-focus", "cursor", "move", null, s);
                        }
                    }
                }
            }
        },

        doMouseDown_: function(h) {
            if(jq.isAncestor(this.$n("real"), h.domTarget)) {
                this._delaySetDragMove = null;
            }
            if(!this._focus && this.isFocusable()) {
                var j = this._sheet, l = j.state;
                if(l == zss.SSheetCtrl.EDITING) {
                    j.dp.stopEditing();
                } else {
                    if(l == zss.SSheetCtrl.NOFOCUS) {
                        j.dp.gainFocus();
                        return;
                    }
                }
                var k = jq(this.$n()), 
                    i = this._focusBorderSize, 
                    n = this._leftPos - i, 
                    m = this._topPos - i;
                k.addClass("zswidget-focus");
                if(n > 0) {
                    k.css({ left: jq.px(n) })
                }
                if(m > 0) {
                    k.css({ top: jq.px(m) });
                }
                j.fire("onFocused", { ctrl: this });
                this._updateListenFocus(true);
            }
        },

        doKeyDown_: function(h) {
            var i = h.keyCode;
            this._sheet._wgt.fireWidgetCtrlKeyEvt(this._type, this._id, i, ! !h.ctrlKey, ! !h.shiftKey, ! !h.altKey);
        },

        _updateListenFocus: function(h) {
            var i = this._focus;
            if(i != h) {
                this._sheet[i ? "unlisten" : "listen"]({
                    onFocused: this.proxy(this._onSheetFocus)
                });
                this._focus = h;
                this.$n("fo")[h ? "focus" : "blur"]();
            }
        },

        bind_: function() {
           this.$supers("bind_", arguments);
        },
        
        /**
         * 在初始化時，因為spreadsheet沒有初始化內部的widget的mold，所以不能bindChildren
         */
        bindChildren_: function() {
            // do nothing
        },

        unbind_: function() {
            this._updateListenFocus(false);
            this.$supers("unbind_", arguments);
        },

        domClass_: function(h) {
            return "zswidget";
        }

    })
})();
