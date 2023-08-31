/*通用参数-------------------------------------------------------------------------------------------------------------------------------*/
const hostDoc = 'http://test.yunjiaosoft.com/contractmodel' //前端页面存放路径
//layui相关
const layform = layui.form
const laytable = layui.table
const laypage = layui.laypage
const element = layui.element
const laydate = layui.laydate
$('.laydate').each(function () {//laydate初始化
    $(this).prop('readonly', true)
    laydate.render({
        elem: this, //指定元素
        max: 0,
        trigger: 'click'
    })
})
//用户相关
let loginData = JSON.parse(sessionStorage.getItem('loginData'))
let loginSlaveList = JSON.parse(sessionStorage.getItem('loginSlaveList'))
const token = sessionStorage.getItem('token')  //用户token
const loginId = loginData.id  //用户id
const loginRealName = loginData.realName//用户真实姓名
// const archivesIds = loginData.archivesId //档案id
const loginUserName = loginData.name  //用户登录名
/*const loginUserRole = sessionStorage.getItem('loginRole')  //用户角色
const loginAreaId = loginData.areaId || ''  //营销区域id
const loginAreaName = loginData.areaName  //营销区域名称
const loginAncestors = loginData.ancestors  //营销区域祖籍id
const loginJobPostion = loginData.jobPostion  //所属岗位
const loginCompanyId = loginData.companyId  //所属公司
const loginCompanyNo = loginData.companyNo  //所属公司*/
const gaodeKey = '3ef62d6b5b00471f846e2748b957d815'   //高德地图key

//数据相关
var datasl = {}      //查询条件
let thistable = []   //当前表格数据
var thistr = {}      //当前行数据(用于查看详情时获取必要信息)
let thisform = {}    //当前表单数据(用于新增和修改时判断用户是否有改动)
var menuId = $(self.frameElement).attr('data-id')    //菜单id
var buttonId = ''  //按钮id
var areaTypeCode = ''  //营销区域数据权限
var toDecimal = 2 * 1   //保留小数

var $chooseDom = ''  //触发弹出选择框的元素
var is_getRole = false  // 角色管理——数据权限-营销区域勾选回显
/*通用方法-------------------------------------------------------------------------------------------------------------------------------*/
//设置网页title
$(self.frameElement).attr('data-title') && $('title').text($(self.frameElement).attr('data-title'))
//ajax拦截器(若返回码为1008则退至登录页面)
$.ajaxSetup({
    // header: {token: token},
    dataFilter: top.ajax_dataFilter,
})
/*ajax全局设置请求头*/
$.ajaxSettings = $.extend($.ajaxSettings, {
    headers: {
        token: token,
    }
});
// ajax设置loading效果
$(document).ajaxStart(function () {
    layer.load(2)
    $('body').addClass('ajax_doing') //避免layer加载效果与数据表格加载效果同时出现
}).ajaxStop(function () {
    layer.closeAll('loading')
    setTimeout(() => {
        $('body').removeClass('ajax_doing')
    }, 500)
})

//获取地址栏url参数
function get_parm(name, url) {
    url = url ? url.substr(url.indexOf('?')) : location.search
    let reg = new RegExp('(^|&)' + name + '=([^&]*)(&|$)', 'i')
    let r = url.substr(1).match(reg);
    return r ? decodeURIComponent(r[2]) : null
}

//判断两个js对象是否相等
function equals_object(a, b) {
    let aNames = Object.getOwnPropertyNames(a)
    let bNames = Object.getOwnPropertyNames(b)
    if (aNames.length != bNames.length) { //判断属性数量是否相同
        return false
    }
    for (let i = 0; i < aNames.length; i++) { //遍历循环所有属性
        let name = aNames[i]
        let aVal = a[name]
        let bVal = b[name]
        if ((typeof (aVal) == 'object') && (typeof (bVal) == 'object')) {//如果当前属性值仍是对象, 则递归比较
            if (!this.equals_object(aVal, bVal)) {
                return false
            }
        } else if (aVal != bVal) {
            return false
        }
    }
    return true
}

//对象转换为键值对连接符字符串
function serialize(obj) {
    var str = [];
    for (var p in obj)
        if (obj.hasOwnProperty(p)) {
            // str.push(encodeURIComponent(p) + "=" + encodeURIComponent(obj[p]));
            str.push(decodeURIComponent(p) + "=" + decodeURIComponent(obj[p]));
        }
    return str.join("&")
}

/** * 动态加载CSS * @param {string} url 样式地址 */
dynamicLoadCss("_plugin/font-awesome/css/font-awesome.min.css")

function dynamicLoadCss(url) {
    var head = document.getElementsByTagName('head')[0];
    var link = document.createElement('link');
    link.type = 'text/css';
    link.rel = 'stylesheet';
    link.href = url;
    head.appendChild(link);
}

// 如果左侧存在菜单，则按钮显示；反之不显示
function changeDa() {
    let changeUrl = []
    parent.$('.index_menu ul li').each(function () {
        let dataUrl = $(this).children('label').attr('data-url')
        changeUrl.push(dataUrl)
    })
    if (changeUrl.indexOf('kehudabg.html') > -1) {  //如果存在
        $('.changeDa').removeClass('none')
    } else {
        $('.changeDa').addClass('none')
    }
}


/*根据权限获取当前页面的按钮-------------------------------------------------------------------------------------------------------------------------------*/
/*$.ajaxSettings.async = false
$.post(
	'../sys/menu/listByRole.do',
	{
		token: token,
		roleIds: sessionStorage.getItem('loginRole'),
		menuId: $(self.frameElement).attr('data-id') //获取当前菜单项id
	},
	res => {
		// let loginButtons = res.data.map(item => item.code)//当前页面的按钮配置信息
		let loginButtons = res.data//当前页面的按钮配置信息
		$('div.bodyGrid header button[class^="btn_"]').each(function () {//遍历btnBarHome区域的按钮
			let thisBtn = $(this).attr('class')
			thisBtn.split(' ').forEach(item => {
				if (item.substr(0, 4) == 'btn_') {
					thisBtn = item
					return false
				}
			})

			// console.log(loginButtons)
			if (!loginButtons.map(item => item.code).includes(thisBtn)) {//如果配置中不包含当前按钮,则移除

				if ($(this).is('.dropdown-toggle')) {  //如果是独立下拉按钮
					if ($(this).parent('dropdown').is(':only-child')) { //如果是当前组唯一个体
						$(this).parents('.btn-group').remove() //则直接移除当前组
					} else {
						$(this).parent('dropdown').remove()   //否则移除当前下拉体
					}
				} else if (($(this).next('dropdown')[0] && $(this).next('dropdown').children('button').text() == '')) {   //如果当前是分段式下拉按钮
					$(this).next('dropdown').remove()  //则先删除后面的下拉体
				}

				if ($(this).is(':only-child')) { //做完上面操作后如果本组只剩当前按钮
					$(this).parents('.btn-group').remove() //则直接移除本组
				}
				$(this).remove()

			} else {//如果配置中包含当前按钮,给相应按钮buttonId
				loginButtons.forEach((btnId, i) => {
					if ($(this).hasClass(btnId.code)) {
						$(this).attr('data-buttonid', btnId.buttonId)
						$(this).attr('data-remark', btnId.remark)
					}
				})
			}
		})
		init_grid_button()
	}
)
$.ajaxSettings.async = true*/
/*按钮不设限-------------------------------------------------------------------------------------------------------------------------------*/
init_grid_button()

//按钮初始化
function init_grid_button() {
    $('button').prop('type', 'button')
    $('.dropdown-toggle').addClass(`btn btn-outline-secondary`) //下拉按钮
        .attr('data-toggle', 'dropdown')

    $('.btn_insert1').addClass(`btn btn-info`)          //新增1
    $('.btn_insert').addClass(`btn btn-info`)          //新增
        .click(function () {
            $('#formInsert .form-control:not(.disabled)').prop('disabled', false)   //表单控件解除禁用
            $('.btn_submit_new').attr('hidden', false).parent('.btn-group').attr('hidden', false)   //显示"保存新增"
            $('#formInsert')[0].reset()  //重置表单
            thisform = layform.val('formInsert')   //当前表单数据(暂存, 点击返回时判断是否有改动)
            $('body').attr('thisdo', 'insert')
        })

    $('.btn_update').addClass(`btn btn-info`)          //修改
    $('.btn_delete').addClass(`btn btn-info`)          //删除
    $('.btn_copy').addClass(`btn btn-info`)            //复制

    $('.btn_select').addClass(`btn btn-outline-info`)  //查询
        .click(function () {
            layer.open({
                type: 1,
                resize: false,
                title: '查询条件',
                area: [`${$(this).attr('w') || 390}px`, `${$(this).attr('h') || 335}px`],
                content: $('#formSelect'),
            })
        })
    $('.btn_select_do').addClass(`btn btn-info`)       //查询_执行

    $('.btn_verify').addClass(`btn btn-outline-secondary`)   //审批
    $('.btn_cancelVerify').addClass(`btn btn-outline-secondary`)   //取消审批
    $('.btn_verify_ok').addClass(`btn btn-info`)             //审批_通过
    $('.btn_verify_no').addClass(`btn btn-outline-secondary`)//审批_不通过
    $('.btn_sign_ok').addClass(`btn btn-info`)             //签字_通过
    $('.btn_sign_no').addClass(`btn btn-outline-secondary`)//签字_不通过

    $('.btn_filter').addClass(`btn btn-outline-secondary`)   //过滤
    $('.btn_freeze').addClass(`btn btn-outline-secondary`)   //冻结, 停用
    // $('.btn_active').addClass(`btn btn-outline-secondary`)   //解冻, 启用
    $('.btn_change').addClass(`btn btn-outline-secondary`)   //变更
    $('.btn_publish').addClass(`btn btn-outline-secondary`)  //发布
    $('.btn_publish').addClass(`btn btn-outline-secondary`)  //发布
    $('.btn_resetPassword').addClass(`btn btn-outline-secondary`)  //重置密码
    $('.btn_dataCof').addClass(`btn btn-outline-secondary`)  //数据权限
    $('.btn_assignUsers').addClass(`btn btn-outline-secondary`)  //分配用户
    $('.btn_createUser').addClass(`btn btn-outline-secondary`)  //生成用户
    $('.btn_review').addClass(`btn btn-outline-secondary`)  //提交申请
    $('.btn_send').addClass(`btn btn-outline-info`)  //发送
    $('.btn_preview').addClass(`btn btn-outline-info`)  //预览
    $('.btn_download').addClass(`btn btn-outline-info`)  //下载
    $('.btn_reviewHistory').addClass(`btn btn-outline-secondary`)  //审批历史
    $('.btn_reviewSchedule').addClass(`btn btn-outline-secondary`)  //进度查看
    $('.btn_htConf').addClass(`btn btn-outline-secondary`)  //合同签章
    $('.btn_htcancel').addClass(`btn btn-outline-secondary`)  //合同作废
    $('.btn_cancelhtConf').addClass(`btn btn-outline-secondary`)  //
    $('.btn_group').addClass(`btn btn-info`)          //分组（计量单位）
    $('.btn_unit').addClass(`btn btn-info`)          //单位（计量单位）
    $('.btn_sign').addClass(`btn btn-outline-secondary`)  //签收
    $('.btn_evaluate').addClass(`btn btn-outline-secondary`)  //评价
    $('.btn_addArea').addClass(`btn btn-outline-secondary`)          //分配
    $('.btn_delArea').addClass(`btn btn-outline-secondary`)          //移除
    $('.btn_xiangqing1').addClass(`btn btn-info`)          //详情
    $('.btn_createGoods').addClass(`btn btn-info`)  //生成竞品
    $('.btn_qcc').addClass(`btn btn-info`)  //企查查认证
    $('.btn_import1').addClass(`btn btn-outline-secondary`) // 导入1
    $('.btn_createBill').addClass(`btn btn-info`)  //生成收款单
    $('.btn_createCustomer').addClass(`btn btn-info`)  //生成客户

    $('.btn_upload').addClass(`btn btn-info`)   //部署流程定义
        .click(function () {
            layer.open({
                type: 1,
                resize: false,
                title: '导入',
                area: [`${$(this).attr('w') || 390}px`, `${$(this).attr('h') || 235}px`],
                content: $('#formUpload'),
            })
        })

    $('.btn_search').addClass(`btn btn-outline-info`)        //模糊查询
        .attr('title', $('.btn_search').text())
        .html(`<img src="_img/main/btn_search.png">`)
        .mouseenter(function () {
            $(this).next('.input-group').find('input').focus()
        })
    $('.btn_search_do').addClass(`btn btn-outline-info`)     //模糊查询_执行
        .attr('title', $('.btn_search_do').text())
        .html(`<img src="_img/main/btn_search.png">`)
        .click(function () {
            // let keywords = $(this).parents('.btnBar').find('[data-id="keywords"]').val()
            let keywords = $(this).parents('.btnBar').find('[data-id="keywords"]').attr('data-name')
            let keywordsVal = $(this).parents('.btnBar').find('[data-id="keywords"]').val()
            // datasl = {keywords}     //查询条件只放模糊关键字
            datasl[keywords] = keywordsVal    //查询条件只放模糊关键字
            if ($('#formSelect')[0]) {
                $('#formSelect')[0].reset()  //重置查询表单
            }
            $('#formSelect [data-id="keywords"]').val(keywords)
            $('.ztree .curSelectedNode').removeClass('curSelectedNode')//取消树选中项
            getTable()
        })

    $('.btn_import').addClass(`btn btn-outline-secondary`)   //导入
        .attr('title', $('.btn_import').text())
        .html(`<img src="_img/main/btn_import.png">`)
        .click(function () {
            layer.open({
                type: 1,
                resize: false,
                title: '导入',
                area: [`${$(this).attr('w') || 390}px`, `${$(this).attr('h') || 235}px`],
                content: $('#formImport'),
            })
        })

    if ($('.btn_export')[0]) {
        $.getScript('_plugin/table2excel/table2excel.min.js')
        $('.btn_export').addClass(`btn btn-outline-secondary`)   //导出
            .attr('title', $('.btn_export').text())
        // .html(`<img src="_img/main/btn_export.png">`)
        $('.btn_export:not([do-ignore])').click(function () {
            if (!$('[lay-id="dataTable"] .layui-table-main tr')[0]) {
                layer.msg('无需导出')
                return false
            }
            init_printAndExportBox()
            let filename = $(this).attr('filename') || `${$('title').text()}导出`
            $('link[href$="layui.css"],link[href$="bootstrap.min.css"]').prop('disabled', true)
            new Table2Excel('#printAndExportBox table', {widthRatio: .16}).export(filename)  //widthRatio输出宽度比率(类似于font-size)
            $('link[href$="layui.css"],link[href$="bootstrap.min.css"]').prop('disabled', false)
            $('#printAndExportBox').remove()
        })
    }
    if ($('.btn_print')[0]) {
        $.getScript('_plugin/print/jQuery.print.min.js')
        $('.btn_print').addClass(`btn btn-outline-secondary`) //打印
            .attr('title', $('.btn_print').text())
        // .html(`<img src="_img/main/btn_print.png">`)
        $('.btn_print:not([do-ignore])').click(function () {
            if (!$('[lay-id="dataTable"] .layui-table-main tr')[0]) {
                layer.msg('无需打印')
                return false
            }
            if ($('body').attr('thisdo') == 'detail') {  //如果在详情页
                init_printAndExportBox('form')
            } else {
                init_printAndExportBox()
            }
            $('#printAndExportBox').print()
            $('#printAndExportBox').remove() //移除临时区域
        })
    }

    $('.btn_more').addClass(`btn btn-outline-secondary`)  //更多
        .attr('title', $('.btn_more').text())
        .html(`<img src="_img/main/btn_more.png">`)

    $('.btn_refresh').addClass(`btn btn-outline-secondary`)            //刷新
        .click(function () {
            location.reload()
        })

    $('.btn_cancel').addClass(`btn btn-outline-secondary`)//取消
        .click(function () {
            if (window.name) {  //iframe弹框
                parent.layer.close(parent.layer.getFrameIndex(window.name))
            } else if ($(this).parents('.layui-layer-page')[0]) { //内容弹框
                layer.closeAll('page')
            }
        })
    $('.btn_submit').addClass(`btn btn-info`)             //保存、确定
    $('.btn_submit_new').addClass(`btn btn-info`)         //保存新增
    $('.btn_return').addClass(`btn btn-outline-secondary`)//返回
        .prepend('<img src="_img/main/btn_return.png"> ')
}


/*ajax请求完成后的统一处理-------------------------------------------------------------------------------------------------------------------------------*/

//加载表格后的处理
function done_table(res) {
    thistable = res.data  //暂存表格数据
    $('[lay-id="dataTable"] .layui-table-main .status_fz').each(function () {  //冻结行美化
        let i = $(this).parents('tr').attr('data-index')
        $(`[lay-id="dataTable"] .layui-table-main tr[data-index="${i}"]`).addClass('freeze')
        $(`[lay-id="dataTable"] .layui-table-fixed-l tr[data-index="${i}"]`).addClass('freeze')
    })
}

//(新增、修改功能)保存后的处理
function done_submit(res, btn) {
    layer.msg(res.msg)
    if (res.code != 1111) {
        return false
    }
    if ($('body').attr('thisdo') == 'insert') { //如果是新增
        getTable() //重载表格
        if (btn == 'btn_submit') { //保存
            $('body').attr('thisdo', '')
        } else if (btn == 'btn_submit_new') {  //保存新增
            $('#formInsert')[0].reset()
            layform.val('formInsert', thisform)
        }
    } else if ($('body').attr('thisdo') == 'update') { //如果是修改
        laytable.reload('dataTable') //刷新表格当前页
        $('body').attr('thisdo', '')
    }
}

//导入后的处理
function done_import(res) {
    if (res.code != 1111) {
        layer.alert(res.msg + '<br>' + res.errorInfo)
        return false
    }
    getTable() //重载表格
    $('body').attr('thisdo', '')  //返回表格页

    layer.closeAll()     //关闭所有弹窗
    layer.alert(res.msg + '<br>' + '成功：' + res.successCount + '条；失败：' + res.failCount + '条')
}

//其他常规操作后的处理
function done(res) {
    if (res.code != 1111) {
        layer.msg(res.msg)
        return false
    }
    laytable.reload('dataTable') //刷新表格当前页
    $('body').attr('thisdo', '')  //返回表格页

    layer.closeAll()     //关闭所有弹窗
    layer.msg(res.msg)
}


/*查询相关-------------------------------------------------------------------------------------------------------------------------------*/

//进入页面自动加载表格
/*$(function () {
	if (typeof (getTable) == 'function') {
		getTable()
	}
})*/

$(function () {
    if ($('#dataTable:not([ignore])')[0]) {
        getTable()
    }
})
//监听查询_执行
layform.on('submit(btn_select_do)', function (data) {
    datasl = data.field
    $('.btnBar [data-id="keywords"]').val('')   //清空搜索条
    $('.ztree .curSelectedNode').removeClass('curSelectedNode')//取消树选中项
    layer.closeAll('page')   //关闭查询弹窗
    getTable()
})

//搜索条监听回车
$(document).on('keydown', '[data-id="keywords"]', function (e) {
    if (e.keyCode == 13) {
        if ($(e.target).parents('.input-group')[0]) {
            $(this).parents('.input-group').find('.btn_search_do').click()
        } else {
            $(this).parents('.btnBar').find('.btn_select_do').click()
        }
    }
})


/*新增、修改相关-------------------------------------------------------------------------------------------------------------------------------*/

//前往修改
function to_update() {
    $('#formInsert .form-control:not(.disabled)').prop('disabled', false)   //表单控件解除禁用
    $('#formInsert .form-control.disabled_update').prop('disabled', true)   //表单元素设置禁止修改项
    $('.btn_submit_new').attr('hidden', true).parent('.btn-group').attr('hidden', true)   //隐藏"保存新增"
    if (!$('body').attr('thisdo')) {
        $('#formInsert')[0].reset()  //重置表单
        getDetail()
    }
    $('body').attr('thisdo', 'update')
}

//监听返回按钮
$(document).on('click', '.btn_return', function (e) {
    if ($('#formInsert')[0].tagName == 'FORMBOX') {
        return false
    }
    let thisdo = $('body').attr('thisdo')
    if (['insert', 'update'].includes(thisdo) && !equals_object(layform.val('formInsert'), thisform)) {
        //如果是新增修改页 且 表单内容有变动
        layer.confirm('是否保存已编辑数据？', {btn: ['是', '否']},
            function (index) {
                // $('.btn_submit').click()
                $('#formInsert .btn_submit').click()
                layer.close(index)
            },
            function () {
                $('body').attr('thisdo', '')
            })
    } else {
        $('body').attr('thisdo', '')
    }
})
//监听保存按钮
layform.on('submit(btn_submit)', function (data) {
    doSubmit(data.field, 'btn_submit')
})
//监听保存新增按钮
layform.on('submit(btn_submit_new)', function (data) {
    doSubmit(data.field, 'btn_submit_new')
})


/*审批相关-------------------------------------------------------------------------------------------------------------------------------*/

//前往审批
function to_verify() {
    $('#formVerify')[0].reset()  //重置表单
    layer.open({
        type: 1,
        resize: false,
        title: '审批',
        area: [`${$(this).attr('w') || 390}px`, `${$(this).attr('h') || 300}px`],
        content: $('#formVerify'),
    })
}

//前往签章
function to_signature() {
    $('#formSignature')[0].reset()  //重置表单
    layer.open({
        type: 1,
        resize: false,
        title: '签章',
        area: [`${$(this).attr('w') || 390}px`, `${$(this).attr('h') || 300}px`],
        content: $('#formSignature'),
    })
}

//监听审批通过按钮
layform.on('submit(btn_verify_ok)', function (data) {
    doVerify(data.field, 'btn_verify_ok')
})
//监听审批不通过按钮
layform.on('submit(btn_verify_no)', function (data) {
    doVerify(data.field, 'btn_verify_no')
})

//监听签字通过按钮
layform.on('submit(btn_sign_ok)', function (data) {
    doSignature(data.field, 'btn_sign_ok')
})
//监听签字不通过按钮
layform.on('submit(btn_sign_no)', function (data) {
    doSignature(data.field, 'btn_sign_no')
})

//修改传值只传变化的字段
function deleteField(thisform, field) {
    for (let item in thisform) { //遍历暂存表单内容
        for (let thisItem in field) { //遍历现表单内容
            if (item == thisItem && thisform[item] == field[thisItem]) {
                delete field[thisItem]
            }
        }
    }
}


/*详情页相关-------------------------------------------------------------------------------------------------------------------------------*/
//加入详情通用按钮栏
$('.disabled').prop('disabled', true)
$('#formInsert').prepend(`<header class="header_detail">
	<div class="btnBar">
		<div class="btn-group">
			<button type="button" class="btn btn-outline-secondary btn_return"><img src="_img/main/btn_return.png"> 返回</button>
		</div>
	</div>
	<div class="btnBar">
		<div class="btn-group btnBox_pageTurn">
			<button type="button" class="btn btn-link btn_first"><i class="fa fa-step-backward"></i></button>
			<button type="button" class="btn btn-link btn_prev"><i class="fa fa-caret-left fa-lg"></i></button>
			<button type="button" class="btn btn-link btn_next"><i class="fa fa-caret-right fa-lg"></i></button>
			<button type="button" class="btn btn-link btn_last"><i class="fa fa-step-forward"></i></button>
		
		
			<!--<button type="button" class="btn btn-outline-secondary btn_first"><img src="_img/main/btn_first.png"></button>
			<button type="button" class="btn btn-outline-secondary btn_prev"><img src="_img/main/btn_prev.png"></button>
			<button type="button" class="btn btn-outline-secondary btn_next"><img src="_img/main/btn_next.png"></button>
			<button type="button" class="btn btn-outline-secondary btn_last"><img src="_img/main/btn_last.png"></button>-->
		</div>
	</div>
</header>`)

let thisi = 0 //当前行序列, 供详情翻页用
$(document).on('click', '.btn_xiangqing', function (e) { //监听表格内详情按钮
    $('#formInsert .form-control').prop('disabled', true)
    $('body').attr('thisdo', 'detail')
    thisi = $(this).parents('tbody').find('.btn_xiangqing').index($(this))  //获取当前行序列
    init_detailTurn()   //渲染详情翻页器

}).on('click', '.btnBox_pageTurn>button', function () { //监听详情翻页器
    if ($(this).is('.btn_first')) {
        thisi = 0
    } else if ($(this).is('.btn_prev')) {
        thisi--
    } else if ($(this).is('.btn_next')) {
        thisi++
    } else if ($(this).is('.btn_last')) {
        thisi = thistable.length - 1
    }
    init_detailTurn()   //渲染详情翻页器
})

//详情翻页器
function init_detailTurn() {
    $('.btnBox_pageTurn>button').prop('disabled', false)
    if (thisi == 0) {
        $('.btn_first, .btn_prev').prop('disabled', true)
    }
    if (thisi == thistable.length - 1) {
        $('.btn_last,.btn_next').prop('disabled', true)
    }
    thistr = thistable[thisi]   //获取当前行数据
    if ($('#formInsert')[0].tagName != 'FORMBOX') {
        $('#formInsert')[0].reset()  //重置表单
    }
    getDetail()   //查询详情
}


/*导入、导出、打印相关-------------------------------------------------------------------------------------------------------------------------------*/
//导入文件框联动
$('[data-id="fileName"]').click(function () {
    $(this).next('[data-id="file"]').click()
})
$('[data-id="file"]').change(function () {
    let fileUrl = $(this).val()
    let fileName = fileUrl.substr(fileUrl.lastIndexOf('\\') + 1)
    // let size = $(this)[0].files[0].size
    $(this).prev('[data-id="fileName"]').val(fileName)
})

//创建打印导出临时区域
function init_printAndExportBox(flag) {
    let $printAndExportBox = $('<div id="printAndExportBox"></div>')
    if (flag == 'form') {
        $printAndExportBox.html($('#formInsert>main').clone())
    } else {
        $printAndExportBox.html(
            $('<table></table>').append(
                $('[lay-id="dataTable"] .layui-table-header table').html(),
                $('[lay-id="dataTable"] .layui-table-main table').html(),
                $('[lay-id="dataTable"] .layui-table-total table').html()
            )
        )
    }
    $printAndExportBox.find('.laytable-cell-checkbox, .laytable-cell-radio').parents('th,td').remove()
    $printAndExportBox.find('.layui-table-patch').remove()
    $('body').append($printAndExportBox)
}


/*下拉按钮hover显示-------------------------------------------------------------------------------------------------------------------------------*/
let dropdown_show = false  //状态:菜单是否展开
$('dropdown').mouseenter(function () {
    dropdown_show = true //当鼠标移上菜单时必定是展开态
    $(this).children('div').show()
    $('dropdown').not(this).children('div').hide(100)
}).mouseleave(function () {
    dropdown_show = false   //当鼠标离开菜单时, 先暂时设置关闭态
    setTimeout(() => {   //等待300毫秒后, 若依然是关闭态(即没有其他触发展开的情况), 则全部关闭
        if (dropdown_show == false) $('dropdown>div').hide(100)
    }, 300)
}).on('click', '.dropdown-item', function (e) { //当鼠标点击时
    if (!$('.custom-control').find($(e.target))[0]) {  //若不是勾选框
        $('dropdown>div').hide(100)
    }
})


/*侧栏(树)收缩-------------------------------------------------------------------------------------------------------------------------------*/
$('.bodyGrid>main').prev('aside').children('ul').before(`<i class="ctrl_flexible"></i>`)
$(document).on('click', '.ctrl_flexible', function () {
    $(this).parents('.bodyGrid').toggleClass('flexible')
    laytable.resize()
})

/*表格内没有复选框时, 点击整行即高亮选中-------------------------------------------------------------------------------------------------------------------------------*/
$(document).on('click', '.layui-table-body tr', function (e) {
    let $tableBox = $(this).parents('.layui-table-box')
    // console.log($tableBox[0])
    if (!$tableBox.find('.layui-form-checkbox')[0] && !$(e.target).is('.layui-form-radio')) {  //后半句防止递归
        let i = $(this).attr('data-index')
        if ($tableBox.find('.layui-form-radio')[0]) { //如果有单选按钮
            let $tr = $tableBox.find(`.layui-table-main tr[data-index="${i}"]`)
            if ($tableBox.find('.layui-table-fixed .layui-form-radio')[0]) {  //如果单选按钮列被固定
                $tr = $tableBox.find(`.layui-table-fixed tr[data-index="${i}"]`)
            }
            $tr.find('.layui-form-radio').click()
        }
        $tableBox.find(`tr[data-index="${i}"]`).addClass('checked').siblings().removeClass('checked')
    }
})


/*必填小红点-------------------------------------------------------------------------------------------------------------------------------*/
$('[lay-verify]').attr('lay-verType', 'tips')
    .parents('.form-group').find('.col-form-label').prepend('<i class="required">*</i>')

/*必填小红点初始化*/
function init_required() {
    $('#tableInsert input[lay-verify^="required"]').each(function () {
        $(this).prop('placeholder', '*')
    })
    $('#tableInsert textarea[lay-verify^="required"]').each(function () {
        $(this).prop('placeholder', '*')
    })
    $('#tableInsert select[lay-verify^="required"]').each(function () {
        if (!$(this).siblings('.required')[0]) {  //如果没有小红点
            $(this).before('<span class="required">*</span>')
        }
        // $(this).siblings('.required').hide()
    }).change(function () {
        $(this).val() ? $(this).siblings('.required').hide() : $(this).siblings('.required').show()
    })
}

/*自定义下拉菜单、弹窗相关-------------------------------------------------------------------------------------------------------------------------------*/
//初始化
$('.diyOpen').after(`<img class="diyClear" title="清空" src="_img/main/diyClear.png">`)
//自定义下拉菜单控制显隐
setTimeout(function () {
    if ($('.diySelect')[0] || $('.diyOpen')[0]) {
        $(document).on('click', function (e) {
            var dom = e.target
            if ($(dom).is('.diySelect:not(:disabled)')) {//如果是下拉框本体
                let $thisBox = $(dom).siblings('.diySelectBox')
                if ($thisBox.is(':visible')) {//如果当前为展开状态
                    $thisBox.slideUp(100)
                } else {//如果当前为关闭状态
                    $('.diySelectBox').slideUp(100)
                    $thisBox.slideDown(100)
                }
            } else if ($('.diySelectBox').find(dom).length == 0) {//如果是下拉控件区以外的区域
                $('.diySelectBox').slideUp(100)
                if ($(dom).is('.diyClear')) { //如果是清空按钮
                    $(dom).parents('.form-group').find('input').val('')
                    if ($(dom).parents().is('#tableInsert')) {
                        $(dom).siblings('input').val('')
                    }
                    let id = $(dom).next('.diySelectBox').find('ul').attr('id')
                    let treeObj = $.fn.zTree.getZTreeObj(id);
                    // console.log(treeObj)
                    if (treeObj) {
                        if (treeObj.checkAllNodes) {
                            treeObj.checkAllNodes(false)
                        }
                    }
                }
            }
        })
    }
}, 100)


/*//获取营销区域数据权限(当code=3时为自定义区域,取本接口返回的树;否则查营销区域全部树)
function get_areaPower() {
	$.ajaxSettings.async = false
	$.post(
		'../adminx/access/findRoleDate.do',
		{
			token: token,
			areaId: loginAreaId,
			roleId: loginUserRole,
			menuId,
			buttonId,
		},
		function (res) {
			let data = res.data || {}
			areaTypeCode = data.code
		}
	)
	$.ajaxSettings.async = true
}

//获取营销区域下拉菜单(新增|修改|查询)
function get_areaSelect($this) {
	$('[data-id="areaName"]').parent('div').find('.diyClear, .diySelectBox').remove()
	buttonId = $this.attr('data-buttonid')
	get_areaPower()   //获取营销区域数据权限
	if (areaTypeCode == 0) {  //无权限
		area_nodata()  //无数据权限时的下拉
	} else if (areaTypeCode == 3) {   //自定义
		getTree_areaDiy()
	} else {
		// $.getScript(`_js/tree_area.js`)
		getTree_area()
	}
}

//无数据权限时的下拉
function area_nodata() {
	//渲染下拉框
	$('[data-id="areaName"]').each(function (i) {
		let areaSelectBox = `
		<img class="diyClear" title="清空" src="_img/main/diyClear.png">
		<div class="diySelectBox" style="padding: 10px;color: #999">
			无数据
		</div>`
		$(this).after(areaSelectBox)
	})
}*/


// 下划线转驼峰
/*function replaceUnderLine(val, char = '_') {
	const arr = val.split('')
	const index = arr.indexOf(char)
	arr.splice(index, 2, arr[index + 1].toUpperCase())
	val = arr.join('')
	return val
}*/
function toHump(name) {
    return name.replace(/\_(\w)/g, function (all, letter) {
        return letter.toUpperCase();
    });
}
