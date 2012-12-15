(function() {

    zssex.SimpleTable = zk.$extends(zul.wgt.Div, {
        
//        bind_ : function () {
//            this.desktop = arguments[0];
            // 此處不做任何事，因為這個時間點mold還未被調用。Spreadsheet中的mold是在redrawWidget時才被調用的。
//        },
    
        afterRedrawHTML_: function () {
//            this.firstChild.bind(this.desktop, null);
        }
        
    });
    
})();