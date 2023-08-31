$(function () {
    //点击菜单=================================================
    $('.index_menu').on('click', 'label', function () {
        $menu_in = $(this).next('.menu_in')
        if ($menu_in[0]) {   //如果存在下属菜单
            if ($('body').is('.flexible')) return false  //如果菜单处于收缩状态则无响应
            $menu_in.slideToggle(100)
            $(this).toggleClass('open').children('.sign').toggleClass('fa-chevron-down').toggleClass('fa-chevron-up')
        } else {
            $('.index_menu label').removeClass('active')
            $(this).addClass('active')
            $(this).parents('.menu_in').prev('label').addClass('active')
            addMainTab(this)  //激活/生成tab页
        }
    })
    // $('.index_menu label').click()   //点击所有菜单项(调试用,慎用)

    //生成|激活tab页=================================================
    function addMainTab(dom) {
        let text = $(dom).text().trim()
        let url = $(dom).attr('data-url')
        let id = $(dom).attr('data-id')
        let menuCode = $(dom).attr('data-menucode')

        let $tab = $(`.index_main_tabs li[data-id="${id}"]`)   //判断是否已被打开
        if ($tab[0]) {   //如果已被打开
            $tab.addClass('active').siblings().removeClass('active')
            let $iframe = $(`.index_main_iframeBox iframe[data-id="${id}"]`)
            $iframe.addClass('active').siblings().removeClass('active')
        } else {
            $(".systemSettings").css({
                'display': 'none'
            });
            if (url === 'xitongxx.html') {
                // 如果点击的是 系统设置页
                $(".systemSettings").css({
                    'display': 'black'
                });
                layer.open({
                    type: 2,
                    // maxmin: true,
                    resize: false,
                    title: '系统选项',
                    area: [`780px`, `650px`],
                    content: `xitongxx.html`
                })

            } else {
                // 如果不是系统设置页
                let tab =
                    `<li data-id="${id}" class="active">
					<label>${text}</label>
					<i class="fal fa-times index_main_tab_close"></i>
				</li>`
                $('.index_main_tabs li').removeClass('active')
                $('.index_main_tabs').append(tab)

                let iframe = `<iframe data-id="${id}" data-title="${text}" data-menucode="${menuCode}" src="${url}" class="active"></iframe>`
                $('.index_main_iframeBox iframe').removeClass('active')
                $('.index_main_iframeBox').append(iframe)
                updateTabsCtrlStatus()//判断'向左/向右'控件是否启用
            }
        }
    }

    //点击tab标签=================================================
    $('.index_main_tabs').on('click', 'li', function () {
        let id = $(this).attr('data-id')
        $(`.index_menu label[data-id="${id}"]`).click()
    })

    //点击tab标签的关闭按钮=================================================
    $('.index_main_tabs').on('click', '.index_main_tab_close', function (e) {
        e.stopPropagation()//防止事件冒泡

        let $tab = $(this).parent('li')
        let $iframe = $(`.index_main_iframeBox iframe[data-id="${$tab.attr('data-id')}"]`)

        if ($tab.is('.active')) {  //如果关闭当前标签页, 则跳到左一个标签页
            let prevId = $tab.prev('li').attr('data-id')
            $(`.index_menu label[data-id="${prevId}"]`).click()
        }
        $tab.remove()
        $iframe.remove()
        updateTabsCtrlStatus()//判断'向左/向右'控件是否启用
    })

    //改变页面尺寸时=================================================

    $(window).resize(function () {
        updateTabsCtrlStatus()  //判断'向左/向右'控件是否启用
        updateBodyGrid()  //更新body布局方式
    })

    //更新body布局方式=================================================
    let windowWidth = $(window).width()
    windowWidth < 1200 ? $('body').addClass('flexible') : $('body').removeClass('flexible')

    function updateBodyGrid() {
        if ($(this).width() < 1200 && $(this).width() < windowWidth) {//窗口小于1200且继续缩小
            $('body').addClass('flexible')
        } else if ($(this).width() > 1200 && $(this).width() > windowWidth) {//窗口大于1200且继续扩大
            $('body').removeClass('flexible')
        }
        windowWidth = $(window).width()
    }

    //点击侧边伸缩控件=================================================
    $('.index_ctrl_flexible').on('click', function () {
        $('body').toggleClass('flexible')
        updateTabsCtrlStatus()  //判断'向左/向右'控件是否启用(可能不需要这句)
    })

    //点击向左控件=================================================
    $('.index_ctrl_left').on('click', function () {
        if ($(this).is('.disabled')) return false
        let parent = $('.index_main_tabs').width()
        let step = 0   //右移步长
        $('.index_main_tabs li:hidden').get().reverse().forEach(function (dom) {
            step = zMath.plus(step, $(dom).outerWidth())
            if (step <= parent) $(dom).show()   //步长小于容器宽度则继续右移
        })
        updateTabsCtrlStatus()
    })

    //点击向右控件=================================================
    $('.index_ctrl_right').on('click', function () {
        if ($(this).is('.disabled')) return false
        let parent = $('.index_main_tabs').width()
        let step = 0   //左移步长
        $('.index_main_tabs li:visible').each(function () {
            step = zMath.plus(step, $(this).outerWidth())
            if (step <= parent) $(this).hide()   //步长小于容器宽度则继续左移
        })
        updateTabsCtrlStatus()
    })

    //判断'向左/向右'控件是否启用
    function updateTabsCtrlStatus() {
        //判断左侧是否有隐藏标签, 有则启用'向左', 否则禁用
        if ($('.index_main_tabs li:hidden')[0]) $('.index_ctrl_left').removeClass('disabled')
        else $('.index_ctrl_left').addClass('disabled')
        //判断可显示标签(视口内+视口右侧)宽度是否大于视口宽度, 大于则启用'向右', 否则禁用 并尝试右移补齐
        let parent = $('.index_main_tabs').width()
        let length = 0
        $('.index_main_tabs li:visible').each(function () {
            length = zMath.plus(length, $(this).outerWidth())
        })
        if (length > parent) {
            $('.index_ctrl_right').removeClass('disabled')
        } else {
            $('.index_ctrl_right').addClass('disabled')
            //可显示标签宽度小于容器宽度&&左侧存在隐藏tab 则继续右移
            $('.index_main_tabs li:hidden').get().reverse().forEach(function (dom) {
                length = zMath.plus(length, $(dom).outerWidth())
                if (length <= parent && $('.index_main_tabs li:visible')[0]) {
                    $(dom).show()
                    updateTabsCtrlStatus()
                }
            })
        }
    }

    //点击刷新控件=================================================
    $('.index_ctrl_refresh').on('click', function (e) {
        let $iframe = $('.index_main_iframeBox iframe.active')
        $iframe[0].contentWindow.location.reload()
    })

    //点击全屏控件=================================================
    $('.index_ctrl_fullscreen').on('click', function () {
        $(this).is('.isFullscreen') ? exitFullscreen() : enterFullscreen()
    })

    function enterFullscreen() { //进入全屏
        let docElem = document.documentElement //被控对象
        if (docElem.requestFullscreen) {//W3C
            docElem.requestFullscreen()
        } else if (docElem.webkitRequestFullscreen) {//Chrome
            docElem.webkitRequestFullscreen()
        } else if (docElem.mozRequestFullScreen) {//FireFox
            docElem.mozRequestFullScreen()
        } else if (docElem.msRequestFullscreen) {//IE11
            docElem.msRequestFullscreen()
        }
        $('.index_ctrl_fullscreen').addClass('isFullscreen').attr('title', '退出全屏')
    }

    function exitFullscreen() { //退出全屏
        if (document.exitFullscreen) {
            document.exitFullscreen()
        } else if (document.webkitExitFullscreen) {
            document.webkitExitFullscreen()
        } else if (document.mozCancelFullScreen) {
            document.mozCancelFullScreen()
        } else if (document.msExitFullscreen) {
            document.msExitFullscreen()
        }
        $('.index_ctrl_fullscreen').removeClass('isFullscreen').attr('title', '全屏')
    }

    //监听全屏状态变化
    document.addEventListener('fullscreenchange', changeFullScreen)
    document.addEventListener('webkitfullscreenchange', changeFullScreen)
    document.addEventListener('mozfullscreenchange', changeFullScreen)
    document.addEventListener('MSFullscreenChange', changeFullScreen)

    function changeFullScreen() {
        $('.index_ctrl_fullscreen i').toggleClass('fa-expand-arrows').toggleClass('fa-compress-alt')
    }

    //控制所有弹出菜单展开隐藏=================================================
    let dialog_menu_show = false  //状态:菜单是否展开
    $('body').on('mouseenter', '.index_dialog', function () {
        dialog_menu_show = true //当鼠标移上菜单时必定是展开态
    })
    $('body').on('mouseenter', '.index_head_right .index_ctrl', function () {
        if ($(this).find('.index_dialog')[0]) {   //当鼠标移上顶部右侧按钮时, 若内部包含菜单
            $('.index_dialog').hide()              //则先关闭所有菜单
            $(this).find('.index_dialog').show()   //再展开内部菜单
            dialog_menu_show = true                //设置展开态
        }
    })
    $('body').on('mouseleave', '.index_head_right .index_ctrl, .index_dialog', function () {
        dialog_menu_show = false   //当鼠标离开顶部右侧按钮或菜单时, 先暂时设置关闭态
        setTimeout(() => {   //等待300毫秒后, 若依然是关闭态(即没有其他触发展开的情况), 则正式关闭
            if (dialog_menu_show == false) $('.index_dialog').slideUp(100)
        }, 300)
    })
    $('body').on('click', '.index_dialog', function () {
        $('.index_dialog').slideUp(100)
    })

    //tab标签区域右键菜单=================================================
    let $tab_rightclick
    $('body').append(`<div class="index_dialog dialog_menu">
			<a href="##" class="one"><i class="iconfont-rookie bangzhu_o"></i><span>关闭</span></a>
			<a href="##" class="left"><i class="iconfont-rookie bangzhu_o"></i><span>关闭左侧</span></a>
			<a href="##" class="right"><i class="iconfont-rookie jiesuo_o"></i><span>关闭右侧</span></a>
			<hr>
			<a href="##" class="other"><i class="iconfont-rookie dengchu_o"></i><span>关闭其他</span></a>
			<a href="##" class="all"><i class="iconfont-rookie dengchu_o"></i><span>全部关闭</span></a>
		</div>`)
    $('body').on('contextmenu', '.index_main_tabsBox', function (e) {  //标签区域点击右键展示菜单
        e.originalEvent.preventDefault() //取消浏览器默认右键菜单
        let w = $('.index_dialog.dialog_menu').outerWidth()
        let x = e.clientX + w <= innerWidth ? e.clientX - 2 : e.clientX - w + 2
        let y = e.clientY - 2
        $('.index_dialog').hide()
        $('.index_dialog.dialog_menu').css('left', x + 'px').css('top', y + 'px').show()
        dialog_menu_show = true
        //获取当前标签
        $('.index_dialog.dialog_menu .one span').text('关闭')
        if ($(e.target).is('.index_main_tabs>li:not([_data-id="home"])')) {   //如果是标签本身(不包括首页)
            $tab_rightclick = $(e.target)
        } else if ($('.index_main_tabs>li:not([_data-id="home"])').find(e.target)[0]) { //如果是标签内部成员(不包括首页)
            $tab_rightclick = $(e.target).parents('.index_main_tabs>li')
        } else {   //如果是标签之外区域
            $('.index_dialog.dialog_menu .one span').text('关闭当前')
            $tab_rightclick = $('.index_main_tabs>li.active')
        }
    }).on('contextmenu', '.index_dialog.dialog_menu', function (e) {   //菜单上点击右键无响应
        e.originalEvent.preventDefault() //取消浏览器默认右键菜单
    }).on('click', '.index_dialog.dialog_menu a', function () {   //点击菜单项执行命令
        if ($(this).is('.one')) {
            $tab_rightclick.find('.index_main_tab_close').click()
        } else if ($(this).is('.left')) {
            $tab_rightclick.prevAll().find('.index_main_tab_close').click()
        } else if ($(this).is('.right')) {
            $tab_rightclick.nextAll().find('.index_main_tab_close').click()
        } else if ($(this).is('.other')) {
            $tab_rightclick.siblings().find('.index_main_tab_close').click()
        } else if ($(this).is('.all')) {
            $('.index_main_tab_close').click()
        }
    })
})

