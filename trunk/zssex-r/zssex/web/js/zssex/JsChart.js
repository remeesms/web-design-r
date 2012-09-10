(function() {

    zssex.JsChart = zk.$extends(zul.wgt.Div, {

        _chartId : '',
        _chart : null,
        _chartModel : null,

        domClass_ : function(no) {
            var out = this.$supers('domClass_', arguments);
            return out;
        },

        domStyle_ : function(no) {
            var out = this.$supers('domStyle_', arguments);
//            out += ' border:1px #444 solid'; // 调试辅助用的边框
            return out; 
        },

        domAttrs_ : function(no) {
            var attr = this.$supers('domAttrs_', arguments);
            return attr;
        },

        bind_ : function() {
            this.$supers('bind_', arguments);
        },

        unbind_ : function() {
            this.$supers('unbind_', arguments);
        },

        getChartId : function() {
            if (!this._chartId) {
                this._chartId = 'highchart_' + Math.round(Math.random() * 10000000000);
            }
            return this._chartId;
        },

        setChartModel : function(chartModel) {
            this._chartModel = chartModel;
        },
        
        getChartModel: function() {
            return this._chartModel;
        },

        /* draw js chart */
        afterRedrawHTML_ : function(no) {
            this.$$renderChart();
        },
        
        $$setupBaseOptions : function() {
            var chartModel = this.getChartModel(),
                chartType = chartModel.chartType,
                viewParam = chartModel.viewParam,
                chartId = this.getChartId();
            
            return {
                chart : {
                    renderTo : chartId,
                    zoomType : 'x',
                    marginRight : 10,
                    width: viewParam.intWidth,
                    height: viewParam.intHeight
                },
                credits : { enabled : false },
                xAxis : {
                    gridLineWidth : 1,
                    gridLineColor : '#DBDBDB',
                    showLastLabel : true,
                    tickPosition : 'inside',
                    title : null
                },
                yAxis : {
                    gridLineWidth : 1,
                    gridLineColor : '#DBDBDB',
                    lineWidth : 1,
                    labels : { align : 'right', x : -20 }
                },
                title : { text : viewParam.chartTitle || null }
            };            
        },
        
        $$setupLegend : function(options) {
            var series = chartModel.clientModel.series;
            if series && series.length > 0) {
                options.legend = {enable: true}; 
            } else {
                options.legend = {enable: false};
            }
        },
        
        
        /**
         * 设置series
         */
        $$setupSeries : function(options) {
             var ser, i, o, j, item, seriesData, 
                 chartModel = this.getChartModel(),
                 chartType = chartModel.chartType,
                 series = chartModel.clientModel.series,
                 categories = chartModel.clientModel.categories,
                 datasource = chartModel.clientModel.datasource;
             
             options.series = options.series || [];
             
             switch (chartType) {
                 case 'scatter' : 
                     series = series || [];
                     datasource = datasource || {};
                     
                     for (i = 0; item = series[i]; i++) {
                         seriesData = datasource[item];
                         ser = {data: []};
                         ser.name = item || '';
                         for (j = 0; o = seriesData[j]; j++) {
                             ser.data.push([o['x'], o['y']]);
                         }
                         options.series.push(ser);
                     }
                     break;
                     
                 case 'line':
                 case 'column':
                 case 'bar':
                 case 'pie':
                     series = series || [];
                     datasource = datasource || [];
                     
                     for (i = 0; item = series[i]; i++) {
                         ser = {data: []};
                         
                         ser.name = item || '';
                         //                 ser.yAxis = item.axis || 0;
                         //                 ser.color = item.color || null;
                         //                 ser.format = item.format || null;
                         //                 item.id !== undefined && item.id !== null && (ser.id = item.id);
                         
                         for (j = 0; o = datasource[j]; j++) {
                             ser.data.push([o['c'], o[item]]);
                         }
                         options.series.push(ser);
                     }
             }
             
             return options;
        },    
        
        $$createChart : function(options) {
            var chartType = this.getChartModel().chartType;
            
            switch (chartType) {
                case 'line':
                case 'column': 
                case 'bar': 
                    options.chart.type = 'column';
                    this._chart = new Highcharts.Chart(options);
                    break;
                case 'pie': 
                    options.chart.type = 'pie';
                    this._chart = new Highcharts.Chart(options);
                    break;
                case 'scatter': 
                    options.chart.type = 'scatter';
                    this._chart = new Highcharts.Chart(options);
                    break;
            }
            
            return options;
        },
        
        
        /**
         * 渲染chart
         * @private
         */
        $$renderChart : function() {
            var options, chartModel;
            
            chartModel = this.getChartModel();
            if (!chartModel || !chartModel.clientModel) { // check
                return;
            }
            
            options = this.$$setupBaseOptions();
            this.$$setupSeries(options);
            this.$$setupLegend(options);
            this.$$createChart(options);
        }
            
    });

})();