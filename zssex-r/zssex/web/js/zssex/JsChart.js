(function() {

    var util = zssex.util;
    
    zssex.JsChart = zk.$extends(zul.wgt.Div, {

        DEFAULT_FORMAT : 'I,III',
        DEFAULT_AXIS_FORMAT : 'I,III',
        DEFAULT_PERCENT_FORMAT : 'I,III.DD%',
        
        _chartId : '',
        _chart : null,
        _chartModel : null,

        domClass_ : function (no) {
            var out = this.$supers('domClass_', arguments);
            return out;
        },

        domStyle_ : function (no) {
            var out = this.$supers('domStyle_', arguments);
//            out += ' border:1px #444 solid'; // 调试辅助用的边框
            return out; 
        },

        domAttrs_ : function (no) {
            var attr = this.$supers('domAttrs_', arguments);
            return attr;
        },

        bind_ : function () {
            this.$supers('bind_', arguments);
        },

        unbind_ : function () {
            this.$supers('unbind_', arguments);
        },

        getChartId : function () {
            if (!this._chartId) {
                this._chartId = 'highchart_' + Math.round(Math.random() * 10000000000);
            }
            return this._chartId;
        },

        setChartModel : function (chartModel) {
            this._chartModel = chartModel;
        },
        
        getChartModel: function () {
            return this._chartModel;
        },

        afterRedrawHTML_ : function (no) {
            this.$$renderChart();
        },
        
        $$dateParsers : {
            'datetime': function (data) {
                if (typeof data == 'string') {
                    if (data.indexOf('/') >= 0) {
                        data = data.split('/');
                        return new Date(parseInt(data[0], 10), parseInt(data[1], 10) - 1, parseInt(data[2], 10)).getTime();
                    }
                    else {
                        return parseInt(data, 10);
                    }
                }
                return data;
            },
            'month': function (data) {
                data = data.split('/');
                return new Date(parseInt(data[0], 10), parseInt(data[1], 10) - 1, 1).getTime();
            },
            'quarter': function (data) {
                var par = [0, 0, 3, 6, 9];
                 data = data.split('/Q'); 
                return new Date(parseInt(data[0], 10), par[parseInt(data[1], 10)], 1).getTime();
            },
            'year': function (data) {
                return new Date(parseInt(data, 10), 0, 1).getTime();
            }
        },
        
        $$dateFormatters : {
            'datetime': function (timestamp) {
                return formatDate(new Date(timestamp), 'yyyy/MM/dd');
            },
            'month': function (timestamp) {
                return formatDate(new Date(timestamp), 'yyyy/MM');
            },
            'quarter': function (timestamp) {
                var date = new Date(timestamp),
                    quarter = ['Q1', 'Q2', 'Q3', 'Q4'];
                return date.getFullYear() + '/' + quarter[Math.floor(date.getMonth() / 3)];
            },
            'year': function (timestamp) {
                return new Date(timestamp).getFullYear();
            }
        },
        
        $$getAxisValueFormatter : function (format) { 
            return function () {
                return util.formatNumber(this.value, format);
            }
        },
        
        $$getAxisTickFormatter : function () {
            var tickType = this.getChartModel().clientModel.tickType,
                formatter = this.$$dateFormatters[tickType];
            return formatter ? function () {
                return formatter.call(this, this.value);
            } : null;
        },
        
        /**
         * 生成基础图表设置
         */
        $$setupBaseOptions : function () {
            var chartModel = this.getChartModel(),
                chartType = chartModel.chartType,
                viewParam = chartModel.viewParam,
                chartId = this.getChartId();
            
            return {
                chart : {
                	animation : false, 
                    renderTo : chartId,
                    zoomType : 'x',
                    marginRight : 10,
                    width: viewParam.intWidth,
                    height: viewParam.intHeight
                },
                credits : { enabled : false },
                title : { text : viewParam.chartTitle || null }
            };
        },
        
        /**
         * 设置图例
         */
        $$setupLegend : function (options) {
            var legend = {
                    enabled: true,
                    align: 'center'
                };
            options.legend = legend;
            return options;
        },
        
        /**
         * 设置点信息
         */
        $$setupPlot : function (options) {
          var chartModel = this.getChartModel(),
              chartType = chartModel.chartType,
              plotOptions = {
        	  series: {
                  animation: false
		             }
          		};
          
          switch (chartType) {
              case 'pie':
                  plotOptions.pie = {
                      allowPointSelect: true,
                      cursor: 'pointer',
                      dataLabels: {
                          enabled: true,
                          formatter: (function(format) {
                              return function () {
                                  return ['<b>', this.point.name, '</b>: ', util.formatNumber(this.percentage, format)].join('');
                              }
                          })(this.DEFAULT_PERCENT_FORMAT)
                      }
                  };
          }
          options.plotOptions = plotOptions;
          return options;
        },

        /**
         * 设置提示浮层
         */
        $$setupTooltip : function (options) {
            var chartModel = this.getChartModel(),
                chartType = chartModel.chartType,
                seriesFormat = chartModel.clientModel.seriesFormat,
                tooltip = {
                    useHTML: true, 
                    borderColor: '#50BFC6'/*'#56ABD8'*/
                };  
            
            switch (chartType) {
                case 'pie':
                    tooltip.shared = false;
                    tooltip.formatter = (function (seriesFormat) {
                        return function () {
                            var html = [];
                            html.push('<b>' + this.point.name + '</b>: ');
                            html.push(util.formatNumber(this.point.y, seriesFormat));
                            return html.join('');
                        }
                    })(seriesFormat);
                    break;
                default:
                    tooltip.shared = true;
                    tooltip.formatter = (function (seriesFormat) {
                        return function () {
                            var html = ['<div class="ui-charts-tooltip">'], item, i,
                            color, o, titleStr, points = [];
                            
                            titleStr = this.x;
                            if (this.points) { //跟踪线tip
                                html.push('<h3>' + titleStr + '</h3>');
                                html.push('<table cellpadding="0" cellspacing="0">');
                                for (i = 0; item = this.points[i]; i++) {
                                    o = item.series;
                                    html.push('<tr><th style="color:#333">' + o.name + '</th><td>');
                                    //暂存格式化后的文本
                                    if (item.__formatStr) {
                                        html.push(item.__formatStr);
                                    } else {
                                        html.push(item.__formatStr = util.formatNumber(item.y, seriesFormat));
                                    }
                                    html.push('</td></tr>');
                                }
                                html.push('</table>');
                            }    
                            
                            html.push('</div>');
                            return html.join('');
                        }
                    })(seriesFormat);
                    break;
            }
            
            
            options.tooltip = tooltip;
            return options;
        },
        
        /**
         * 设置x轴
         */
        $$setupXAxis : function (options) {
            var chartModel = this.getChartModel(),
                chartType = chartModel.chartType,
                tickType = chartModel.clientModel.tickType,
                categories = chartModel.clientModel.categories || [],
                axis = {
                    gridLineWidth : 1,
                    gridLineColor : '#DBDBDB',
                    showLastLabel : true,
                    tickPosition : 'inside',
                    title : null,
                    labels : {
                        overflow: null,
                        style: {
                            fontFamily: 'Arial,Serif,Times', 
                            fontSize: '11px', 
                            color: '#282828'
                        }
                    }
                };

            if (tickType == 'category') {
                axis.categories = util.unwrapList(categories, 'label');
            }
            // xAxis.lineColor = category.color;
            
            switch (chartType) {
            case 'scatter' : 
                axis.labels.formatter = this.$$getAxisValueFormatter(this.DEFAULT_AXIS_FORMAT);
                break;
            case 'line':
            case 'column':
            case 'bar':
            case 'pie':
                axis.labels.rotation = 315;
                axis.labels.x = -3; // TODO 暂简单化处理，后续应该计算字长而判断位置
                axis.labels.y = 30; 
                axis.labels.formatter = this.$$getAxisTickFormatter();
                break;
            }

            options.xAxis = axis;
            return options;
        },

        /**
         * 设置y轴
         */
        $$setupYAxis : function (options) {
            var chartModel = this.getChartModel(),
                chartType = chartModel.chartType,
                axis = {
                    gridLineWidth : 1,
                    gridLineColor : '#DBDBDB',
                    lineWidth : 1,
                    labels : { 
                        align : 'right', 
                        x : -10,
                        style : {
                            fontFamily: 'Arial,Serif,Times', 
                            fontSize: '11px', 
                            color: '#282828'
                        }
                    },
                    tickPosition: 'inside', 
                    lineWidth: 1,
                    title: null
                };
            
            axis.labels.formatter = this.$$getAxisValueFormatter(this.DEFAULT_AXIS_FORMAT);

            options.yAxis = axis;
            return options;
        },
        
        /**
         * 设置series
         */
        $$setupSeries : function (options) {
             var ser, i, o, j, item, seriesData, 
                 chartModel = this.getChartModel(),
                 chartType = chartModel.chartType,
                 tickType = chartModel.tickType,
                 series = chartModel.clientModel.series,
                 datasource = chartModel.clientModel.datasource,
                 tickParser = this.$$dateParsers[tickType] || null;
             
             options.series = options.series || [];
             
             switch (chartType) {
                 case 'scatter' : 
                     series = series || [];
                     datasource = datasource || {};
                     for (i = 0; item = series[i]; i++) {
                         seriesData = datasource[item.label];
                         ser = {data: []};
                         ser.name = item.label || '';
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
                         ser.name = item.label || '';
                         ser.format = item.seriesFormat || null;
                         //                 ser.yAxis = item.axis || 0;
                         //                 ser.color = item.color || null;
                         for (j = 0; o = datasource[j]; j++) {
                             ser.data.push([(tickParser ? tickParser.call(this, o['c']) : o['c']), o[item.label]]);
                         }
                         options.series.push(ser);
                     }
             }
             
             return options;
        },    
        
        /**
         * 创建图实例
         */
        $$createChart : function (options) {
            var chartType = this.getChartModel().chartType;
            
            switch (chartType) {
                case 'line':
                    options.chart.type = 'line';
                    this._chart = new Highcharts.Chart(options);
                    break;
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
        $$renderChart : function () {
            var options, chartModel;
            
            chartModel = this.getChartModel();
            if (!chartModel || !chartModel.clientModel) { // check
                return;
            }
            
            options = this.$$setupBaseOptions();
            this.$$setupSeries(options);
            this.$$setupLegend(options);
            this.$$setupXAxis(options);
            this.$$setupYAxis(options);
            this.$$setupPlot(options);
            this.$$setupTooltip(options);
            this.$$createChart(options);
        }
            
    });

})();