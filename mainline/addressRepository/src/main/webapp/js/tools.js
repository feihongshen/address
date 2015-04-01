/**
 * Created with JetBrains WebStorm.
 * User: hedy
 * Date: 14-12-30
 * Time: 下午4:15
 * To change this template use File | Settings | File Templates.
 */

/**
 * 闭包执行
 */
(function(){
    /**
     * 在Date对象中添加format方法
     * @param format  format 字符串
     * @returns {*}
     */
    Date.prototype.format = function(format){
        var o = {
            "M+" : this.getMonth()+1, //month
            "d+" : this.getDate(), //day
            "h+" : this.getHours(), //hour
            "m+" : this.getMinutes(), //minute
            "s+" : this.getSeconds(), //second
            "q+" : Math.floor((this.getMonth()+3)/3), //quarter
            "S" : this.getMilliseconds() //millisecond
        }
        if(/(y+)/.test(format)) {
            format = format.replace(RegExp.$1, (this.getFullYear()+"").substr(4 - RegExp.$1.length));
        }
        for(var k in o) {
            if(new RegExp("("+ k +")").test(format)) {
                format = format.replace(RegExp.$1, RegExp.$1.length==1 ? o[k] : ("00"+ o[k]).substr((""+ o[k]).length));
            }
        }
        return format;
    }
}());

/**
 * 工具方法
 * @type {{funDefAction: string, namespace: Function, getFunDef: Function, loadJs: Function, loadJavaSctipe: Function, processData: Function, doAction: Function, doFormAction: Function, getFormatDate: Function}}
 */
var Tools = {
    "namespace": function(path) { //指定命名空间
        var arr = path.split(".");
        var ns = "";
        for(var i = 0; i < arr.length; i++){
            if( i> 0) ns += ".";
            ns += arr[i];
            eval("if(typeof(" + ns + ") == 'undefined') " + ns + " = new Object();");
        }
    }
    /**
     * 加载自定义js
     * @param pathUrl js文件路径
     * @param viewer
     */
    , "loadJs" : function (pathUrl, viewer) { //将js加载到指定句柄中
		jQuery.ajax({
			url : pathUrl,
			type : "GET",
			dataType : "text",
			async : false,
			data : {},
			success : function(data) {
				try {
					var servExt = new Function(data);
					servExt.apply(viewer);
				} catch (e) {
                    Tip.alertError("js加载错误！" + e);
				}
			},
			error : function() {
				;
			}
		});
	}
    /**
     * 将js文件引用到页面上
     * @param pathUrl文件路径
     */
	, "loadJavaScript" : function (pathUrl) { //将js加载到 <script></script>中
		var jsFileUrl = pathUrl;
		if (!window.Scripts) {
			window.Scripts = [];
		}
		// 检测该脚本有没有被加载过
		var isLoaded = false;
		var head = jQuery(jQuery("head")[0]);
		if (!head) {
			head = jQuery("<head></head>")[0];
		}
		jQuery.each(head.find("script"), function(index, sc) {// 先在页面上找
			if (sc.src && sc.src.indexOf(pathUrl) != -1) {
				isLoaded = true;
				return;
			}
		});
		if (!isLoaded) {// 然后在Scripts数组里继续找
			jQuery.each(window.Scripts, function(index, url) {
				if (pathUrl == url) {
					isLoaded = true;
					return;
				}
			});
		}
		if (isLoaded) {
			return;
		}
		jQuery.ajax({
			url : jsFileUrl,
			type : "GET",
			dataType : "text",
			async : false,
			data : {},
			success : function(data) {
				try {
					head.append("<script type='text/javascript'>" + data + "<\/script>");
					// 放入Scripts数组里
					window.Scripts.push(pathUrl);
				} catch (e) {
                    Tip.alertError("加载javascript异常，" + e);
				}
			},
			error : function() {
				;
			}
		});
	}
    /**
     * 异步返回数据
     * @param ajaxUrl请求url
     * @param queryParams 参数对象
     * @param async 是否异步请求，默认同步（false）
     * @param callbackFunc 回调函数
     * @returns {Object} 返回执行结果集合
     */
	, "processData" : function(ajaxUrl, queryParams, async ,callbackFunc) { //异步调用
	    var resultData = new Object();
	    var params = jQuery.extend({}, queryParams, {expando:jQuery.expando});
	    var tempasync = false;
	    if (async) {
	    	tempasync = async;
	    }
	    jQuery.ajax({
	        type:"post",
	        url:encodeURI(ajaxUrl),
	        dataType:"json",
	        data:params,
	        cache:false,
	        async:tempasync,
	        timeout:60000,
	        success:function(data) {
	            resultData = {};
	            resultData = data;
	            if (typeof data === "string") {//判断返回数据类型
					try {
						resultData = jQuery.parseJSON(data);
					} catch (e) {
                        Tip.alertError("数据异常，" + e);
					}                
	            }
	            //TODO show error msg
	            if(callbackFunc) {
	            	callbackFunc.call(this, resultData);
	            }
	        },
	        error:function(err) {
	            resultData = {};
	            resultData.exception = err;
	            resultData.msg = err.responseText || "error";
//	            if(loginJuge(resultData) == true) {
//	            	return false;
//	            } else {
	            	throw new Error(resultData.msg);
//	            }
	        }
	    });
	    return resultData;
	}
    /**
     * action 异步调用请求
     * @param action 请求action
     * @param data  参数
     * @param async  是否异步，默认同步
     * @param func  回调函数
     * @returns {Object} 返回结果集
     */
	, "doAction": function(action , data, async, func) { //异步调用 
        var ajaxUrl = action;
        var datas = data || {}
        return this.processData(ajaxUrl, datas, async, func);
    }
    /**
     * form提交，用于文件流下载
     * @param action  请求action
     * @param data    参数
     * @param target   目标窗口对象
     */
    , "doFormAction" : function(action, data, target) { //form调用数据，用于上传和下载
    	var formId = "__submitform";
    	var form = document.getElementById(formId);
    	if (form == null) {
    		form = document.createElement("form");
    		form.id = formId;
    		form.style.display = "none";
    		document.body.appendChild(form);
    		form.method = 'post';
    		//使用utf-8
    		form.acceptCharset = "UTF-8";
    		if (!+[1,]) {
    			//让IE支持acceptCharset
    			var el = document.createElement("input");
    			el.setAttribute("name", "_charset_");
    			el.setAttribute("value", "?");
    			form.appendChild(el);
    		}
    	} else {
    		form.innerHTML = "";
    	}
    	form.action = action;
    	if (target == undefined) {
    		target = "_blank";
    	}
    	form.target = target;
    	var el = document.createElement("input");
    	el.setAttribute("id", formId + "data");
    	el.setAttribute("name", "data");
    	el.setAttribute("type", "hidden");
    	form.appendChild(el);
    	document.getElementById(formId + "data").value = jQuery.toJSON(data);
    	form.submit();
    }
    /**
     * 获取格式化日期
     * @param format  yyyy、MM、dd、hh、mm、ss的任意分隔格式日期
     * @returns {*} 格式化之后的当前系统日期字符串
     */
    , "getFormatDate" : function(format, date) {
    	if (date) {
    		return date.format(format);
    	}
        return new Date().format(format);
    }
};

/**
 * 弹出提示框对象
 * @type {{alertError: Function}}
 */
var Tip = {
    /**
     * 成功提示
     * @param msg  提示信息
     */
    "msgOk" : function(msg) {
        layer.msg(msg || "Error", 1, 1);
    }
    /**
     * 弹出错误信息
     * @param msg  提示信息
     */
    , "msgError" : function(msg) {
        layer.msg(msg || "Error", 1, 3);
    }
    /**
     * 弹出成功提示
     * @param msg  提示信息
     */
    , "alertOk" : function(msg) {
        layer.alert(msg  || "OK", 1);
    }
    /**
     * 弹出错误提示
     * @param msg  提示信息
     */
    , "alertError" : function(msg) {
        layer.alert(msg || "Error", 3);
    }
};