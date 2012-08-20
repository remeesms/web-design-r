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
            out += ' border:1px #444 solid';
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
                this._chartId = 'hichart_' + Math.round(Math.random() * 10000000000);
            }
            return this._chartId;
        },

        /**
         * 初始化chart
         */
        setChartModel : function(chartModel) {
            this._chartModel = chartModel;
        },

        /* draw js chart */
        afterRedrawHTML_ : function(no) {
            this.$$renderChart();
        },
        
        /**
         * 渲染chart
         * @private
         */
        $$renderChart : function() {
            var options, chartModel, chartId, chartType, series, categories, csDatasource, viewParam;
            
            chartModel = this._chartModel;
            if (!this._chartModel || !chartModel.categories || !chartModel.series
                    || !chartModel.datasource) {
                return;
            }
            
            chartType = chartModel.chartType;
            series = chartModel.series;
            categories = chartModel.categories;
            csDatasource = chartModel.csDatasource;
            viewParam = chartModel.viewParam;
            chartId = this.getChartId();
            
            options = {
                chart : {
                    renderTo : chartId,
                    zoomType : 'x',
                    marginRight : 10,
                    width: viewParam.intWidth,
                    height: viewParam.intHeight
                },
                credits : {
                    enabled : false
                },
                xAxis : {
                    gridLineWidth : 1,
                    gridLineColor : '#DBDBDB',
                    showLastLabel : true,
                    tickPosition : 'inside',
                    // tickPositioner : CHART_TICK_POSITIONER,
                    title : null
                },
                yAxis : {
                    gridLineWidth : 1,
                    gridLineColor : '#DBDBDB',
                    lineWidth : 1,
                    labels : {
                        align : 'right',
                        x : -20
                    }
                },
                legend : {
                    enabled : true
                },
                title : {
                    text : null
                }
            };
            
            options.series = this.$$setupSeries(chartModel);

            /* 不同图表类型 */
            if (chartType == 'column' || chartType == 'bar') {
                options.chart.type = 'column';
                this._chart = new Highcharts.Chart(options);
            } else if (chartType == 'line') {
                options.chart.type = 'line';
                this._chart = new Highcharts.Chart(options);
            } else if (chartType == 'pie') {
                options.chart.type = 'pie';
                this._chart = new Highcharts.Chart(options);
            } else if (chartType == 'scatter') {
                options.chart.type = 'scatter';
                this._chart = new Highcharts.Chart(options);
            }
        },
        
        $$setupSeries : function(chartModel) {
             var res = [], ser, i, o, j, item, seriesData, 
                 chartType = chartModel.chartType,
                 series = chartModel.series,
                 categories = chartModel.categories,
                 sDatasource = chartModel.sDatasource,
                 csDatasource = chartModel.csDatasource;
             
             if (chartType == 'scatter') {
                 series = series || [];
                 sDatasource = sDatasource || [];
                 
                 for (i = 0; item = series[i]; i++) {
                     seriesData = sDatasource[item];
                     ser = {data: []};
                     ser.name = item || '';
                     for (j = 0; o = seriesData[j]; j++) {
                         if (o['y'] > 0) // TEMP FIXME
                         ser.data.push([o['x'], o['y']]);
                     }
                     res.push(ser);
                 }
                 
             } else {
             
                 series = series || [];
                 csDatasource = csDatasource || [];
    
                 for (i = 0; item = series[i]; i++) {
                     ser = {data: []};
    
                     ser.name = item || '';
    //                 ser.yAxis = item.axis || 0;
    //                 ser.color = item.color || null;
    //                 ser.format = item.format || null;
    //                 item.id !== undefined && item.id !== null && (ser.id = item.id);
    
                     for (j = 0; o = csDatasource[j]; j++) {
                         ser.data.push([o['c'], o[item]]);
                     }
                     res.push(ser);
                 }
           }
           return res;
        }
        
            
            
    });

    /** ************************************************************************* */


    function $$rrrrenderChart(chartModel) {
        var options = this.$initOptions();

        $$createChart(options);
    }

    function $$createChart(options) {
        var chart;
        if (options.chartType == 'line') {
            chart = new STOCKCHART(options);
        } else if (options.chartType == 'column') {
            chart = new STOCKCHART(options);
        }
        return chart;
    }

    /**
     * 构建图表参数
     * 
     * @private
     */
    function $$initOptions(chartModel) {
        var options, i, item, datasource, field, me = this;

        options = {
            chart : {
                renderTo : target,
                zoomType : 'x',
                marginRight : CHART_MARGIN_RIGHT
            },
            credits : {
                enabled : false
            },
            xAxis : {
                gridLineWidth : 1,
                gridLineColor : '#DBDBDB',
                showLastLabel : true,
                tickPosition : 'inside',
                tickPositioner : CHART_TICK_POSITIONER,
                // events: {
                // setExtremes: SETUP_CHART_SETEXTREMES_HANDLER(this)
                // },
                title : null
            },
            yAxis : {
                gridLineWidth : 1,
                gridLineColor : '#DBDBDB',
                lineWidth : 1,
                labels : {
                    align : 'right',
                    x : -20
                },
                title : null

            },
            legend : {
                enabled : false
            },
            title : {
                text : null
            }
        };

        if (!dom.ieVersion) {
            options.plotOptions = {
                column : {
                    shadow : true,
                    borderWidth : 1
                }
            };
        }

        datasource = $$setupChartData(chartModel.tickType, chartModel.field,
                chartModel.chartData);

        options.series = SETUP_SERIES(this._aSeries, this._oCategory.field,
                datasource);
        options.xAxis = extend(options.xAxis, SETUP_CATEGORY(this._oCategory));
        options.yAxis = extend(options.yAxis, SETUP_AXIS(this._aAxis));
        options.rangeSelector = SETUP_RANGE_SELECTOR(this._oCategory);
        SETUP_NAVIGATOR(options, this._oCategory);

        options.tooltip = SETUP_TOOLTIP(this._oCategory.type, moreInfo,
                holidayInfo, this._oCategory.tipCallback);

        return options;
    }

    /**
     * @private
     */
    function $$setupChartData(type, field, data) {
        var res = [], i, item, o;

        for (i = 0; item = data[i]; i++) {
            o = item; // FIXME
            if (PARSE_DATE_HANDLER[type]) {
                o[field] = PARSE_DATE_HANDLER[type].call(null, o[field]);
            }
            res.push(o);
        }
        return res;
    }

    var PARSE_DATE_HANDLER = {
        'datetime' : function(data) {
            if (typeof data == 'string') {
                if (data.indexOf('-') >= 0) {
                    data = data.split('-');
                    return new Date(parseInt(data[0], 10),
                            parseInt(data[1], 10) - 1, parseInt(data[2], 10))
                            .getTime();
                } else {
                    return parseInt(data, 10);
                }
            }
            return data;
        },
        'month' : function(data) {
            data = data.split('-');
            return new Date(parseInt(data[0], 10), parseInt(data[1], 10) - 1, 1)
                    .getTime();
        },
        'quarter' : function(data) {
            var par = [ 0, 0, 3, 6, 9 ];

            data = data.split('-Q');
            return new Date(parseInt(data[0], 10), par[parseInt(data[1], 10)],
                    1).getTime();
        },
        'year' : function(data) {
            return new Date(parseInt(data, 10), 0, 1).getTime();
        }
    };

})();