(function() {
    
    var util = zssex.util = {};
    
    /**
     * 不同方式的四舍五入
     * @param cutMode {Number} 0或默认:四舍五入；2:IEEE 754标准的五舍六入中凑偶
     */
    util.fixNumber = function (num, formatStr, cutMode) {
        var formatDec = /D+/.exec(formatStr);
        var formatDecLen = (formatDec && formatDec.length>0) ? formatDec[0].length : 0;
        if (!cutMode) { // 四舍五入
            var p = Math.pow(10, formatDecLen);
            return ( Math.round (num * p ) ) / p ;
        } else if (cutMode == 2) { // 五舍六入中凑偶
            return Number(num).toFixed(formatDecLen);
        } else { // 原样
            return Number(num);
        }
    }

    /**
     * 将数字按照指定格式进行格式化
     * 
     * @param num
     *            [Number]要格式化的数字
     * @param formatStr
     *            [String]指定的格式<br>
     *            I代表整数部分,可以通过逗号的位置来设定逗号分隔的位数 D代表小数部分，可以通过D的重复次数指定小数部分的显示位数
     * @param usePositiveSign 正数加上正号
     * @param cutMode {Number} 0或默认:四舍五入；1:只是纯截取。2:IEEE 754标准的五舍六入中凑偶
     * @return [String]格式化过的字符串
     * @example util.formatNumber(10000/3, "I,III.DD%"); 返回"3,333.33%"
     */
    util.formatNumber = function (num, formatStr, usePositiveSign, cutMode) {
        var str, numStr, tempAry, intStr, decStr;

        num = util.fixNumber(num, formatStr, cutMode); 
        numStr = num.toString();
        tempAry = numStr.split('.');
        intStr = tempAry[0];
        decStr = (tempAry.length > 1) ? tempAry[1] : "";
             
        str = formatStr.replace(/I+,*I*/g, function() {
            var matchStr = arguments[0];
            var replaceStr;
            var commaIndex = matchStr.lastIndexOf(",");
            if (commaIndex >= 0 && commaIndex != intStr.length - 1) {
                var splitPos = matchStr.length - 1 - commaIndex;
                var parts = [];
                while (intStr.length > splitPos) {
                    parts.push(intStr.substr(intStr.length-splitPos,splitPos));
                    intStr = intStr.substring(0, intStr.length - splitPos);
                }
                parts.push(intStr);
                parts.reverse();
                if (parts[0] == "-") {
                    parts.shift();
                    replaceStr = "-" + parts.join(",");
                } else {
                    replaceStr = parts.join(",");
                }
            } else {
                replaceStr = intStr;
            }
            if (usePositiveSign && replaceStr && replaceStr.indexOf('-') < 0) { 
                replaceStr = '+' + replaceStr;
            }
            return replaceStr;
        });
        str = str.replace(/D+/g, function() {
            var matchStr = arguments[0];
            var replaceStr = decStr;
            if (replaceStr.length > matchStr.length) {
                replaceStr = replaceStr.substr(0, matchStr.length);
            } else {
                while (replaceStr.length < matchStr.length) {
                    replaceStr += "0";
                }
            }
            return replaceStr;
        });
        return str;
    };
    
    /**
     * 將數組解成普通列表。例如：
     * 輸入：[{a:1},{a:2332},{a:55},...]
     * 输出：[1, 2332, 55, ...]
     */
    util.unwrapList = function (list, labelName) {
        var i, l, o, ret = [];
        list = list || [];
        for (i=0, l=list.length; i<l; i++) {
            o = list[i];
            ret.push(o ? o[labelName] : null);
        }
        return ret;
    };
    
})();