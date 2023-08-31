//$(document).ready(function () {

$.fn.extend({
    "stepInit": function (option) {
        //打开下面注释，可动态加载CSS，不用href 导入
        // var style="<style>\nul,li {\n    list-style: none;\n    margin: 0px;\n    padding: 0px;\n}\n\n.bar-box {\n    position: relative;\n    top: 11px;\n    left: 10px;\n}\n\n.bar-content {\n    position: relative;\n    z-index: 9999;\n}\n\n.bar-bg {\n    width: 100%;\n    height: 6px;\n    background: #ddd;\n    position: relative;\n}\n\n.bar-color {\n    width: 0%;\n    height: 6px;\n    background: #00b9a3;\n    position: absolute;\n    left: 0px;\n    top: 0px;\n    /* position: relative;\n    top: -32px; */\n}\n\n.bar-circle-ul {}\n\n.bar-circle-ul li {\n    float: left;\n}\n\n.bar-circle-ul li div {\n    width: 32px;\n    height: 32px;\n    text-align: center;\n    line-height: 32px;\n    display: inline-block;\n    border-radius: 50%;\n    background: #ddd;\n    color: #252525;\n    position: relative;\n}\n\n.bar-circle-ul li div span {\n    position: absolute;\n    top: -33px;\n    color: #ddd;\n    left: -254%;\n    width: 200px;\n}</style>"
        // $('body').append(style);
        var arr = option.data;
        var stepNum = arr.length;
        var stepIndex = option.index; //当前步骤
        //添加最外面的div结构
        var content = "<div class=\"bar-content\">\n<ul class=\"bar-circle-ul\">\n</ul>\n</div>\n\n<div class=\"bar-box\">\n<div class=\"bar-bg\">\n<div class=\"bar-color\">\n</div>\n</div>\n</div>"
        $(this).append(content);
        //js动态处理外面div百分比宽度
        var perc = 100 / stepNum; //100除以当前步骤总和
        $(this).attr({'style': 'margin-right:' + '-' + perc + '%'})
        //生成用于循环li的数据
        var arrObj = []
        for (var i = 0; i < arr.length; i++) {
            var obj = {};
            obj.index = i + 1;
            obj.text = arr[i];
            arrObj.push(obj)
        }
        var liDow = '';
        //根据数据 动态生成li结构
        arrObj.forEach(function (v, k) {
            liDow += "<li><div class='li-div'>" + v.index + "<span class='li-span'>" + v.text + "</span></div></li>";
        })
        $('.bar-circle-ul').append(liDow);
        //js动态处理li 百分比宽度
        $(".bar-circle-ul li").attr({'style': ' width:' + perc + '%'})
        var stepWidth = $(this)[0].clientWidth;
        //动态计算bar-box的宽度
        var barW = Math.floor(stepWidth * (100 - perc) / 100);
        $(".bar-box").attr({'style': ' width:' + barW + 'px'})
        var piceW = Math.floor(barW / (stepNum - 1));
        $(this)[0].piceW = piceW; //存进度宽度到原生dom
        $(this).toStep(stepIndex);
    },
    "toStep": function (stepIndex) {
        //进度动画开始
        var piceW = $(this)[0].piceW;
        //$(".bar-color").attr({'style':' width:'+piceW*stepIndex+'px'}) //这个是没动画的
        $(".bar-color").animate({width: piceW * stepIndex + 'px'}, 100);
        //改变已走步骤 圆点和文字 的颜色
        $('.bar-circle-ul li').each(function (k, v) {
            var liDiv = $(v).find('.li-div');
            var liSpan = $(v).find('.li-span');
            liDiv.css({background: '#ddd', color: '#252525'});  //重置所有的颜色
            liSpan.css({color: '#7c7c7c'});
            if (k <= stepIndex) {
                liDiv.css({background: '#00b9a3', color: '#fff'}); //圆点颜色
                liSpan.css({color: '#252525'}); //字体颜色
            }

        })
    }
})

//})
