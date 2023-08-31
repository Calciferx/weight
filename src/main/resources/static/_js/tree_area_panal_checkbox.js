$(function () {
    //配置项
    let areaSetting = {
        view: {
            showIcon: false,
            selectedMulti: false,
        },
        check: {
            enable: true
        },
        data: {
            simpleData: {
                enable: true
            }
        },
        edit: {},
        callback: {
            onClick: areaTreeOnClick,	//点击触发
            onExpand: areaTreeOnExpand,    //节点被展开时触发
            onCheck: areaTreeOnCheck,	//复选框被改变时触发
        }
    }

    //预加载数据
    let areaNodess = [{
        id: 0,
        // idKey: '0',
        name: '总区域',
        // children: [],
        isParent: true,
        open: true,
        data_areaId: '',
        data_areaCode: '',
    }]


    $.ajaxSettings.async = false
    $.post(
        host + '/adminx/marketingArea/listtree.do',
        {
            token: token,
            parentId: 0
        },
        function (data) {
            data = data.data
            $.each(data, function () {
                /*let areaLists = []
                this.areaLists.map(function (item) {
                    areaLists.push(Object.assign({}, item,
                        {
                            name: item.areaName,
                            idKey: item.areaId,
                            pIdKey: item.parentId,
                            children: item.areaLists,
                            data_areaName: item.areaName,
                            data_areaId: item.areaId,
                            data_ancestors: item.ancestors,
                        }))
                })*/

                areaNodess.push({
                    id: this.areaId,
                    pId: this.parentId,
                    // idKey: this.areaId,
                    // pIdKey: this.parentId,
                    name: this.areaName,
                    isParent: true,
                    // chkDisabled: true,//禁用勾选框
                    // nocheck: this.parentId == 0 ? true : false
                    // children: areaLists, //zTree 节点数据中保存子节点数据的属性名称。

                    data_areaId: this.areaId,
                    data_areaCode: '',
                    data_ancestors: this.ancestors
                })
            })
        }
    )
    $.ajaxSettings.async = true

    $.fn.zTree.init($('#areaTree'), areaSetting, areaNodess);
})


//节点被点击时触发, 改变复选框状态
function areaTreeOnClick(event, treeId, treeNode) {
    if (treeNode.level == 0) {//大区不可选
        return false
    }
    var areaTree = $.fn.zTree.getZTreeObj(treeId);
    if (treeNode.level == 1 || (!!$("#areaId").val() && $("#areaId").val() != treeNode.data_areaId)) {
        areaTree.checkAllNodes(false);   //如果分公司变了,则取消所有勾选
    }
    areaTree.checkNode(treeNode)
    areaTreeOnCheck(event, treeId, treeNode)

    if (treeNode.level == 1) {
        $('#areaName').val(treeNode.name)
        $('#areaTreeBox').hide()
    }
};

//复选框被改变时触发
function areaTreeOnCheck(event, treeId, treeNode) {
    // var nodes = $.fn.zTree.getZTreeObj(treeId).getCheckedNodes(true);
    /*var areaName = nodes.map(function (area) {
        return area.name
    })
    var areaCode = nodes.map(function (area) {
        return area.data_areaCode
    })
    $('#areaName').val(areaName).prop('title', treeNode.getParentNode().name)
    $('#areaCode').val(areaCode)
    $("#areaId").val(treeNode.data_areaId)*/

    var treeObj = $.fn.zTree.getZTreeObj(treeId);
    var nodes = treeObj.getCheckedNodes(true);
    // selected = nodes
    nodes = nodes.filter(function (item) { //过滤掉"总区域"
        return item.level != 0
    })

    selected = nodes
}


//节点被展开时触发
function areaTreeOnExpand(event, treeId, treeNode) {
    if (!treeNode.children) {//如果是第一级且没有子节点,则把当前改为叶子节点
        // $('#' + treeNode.tId + '_switch').css('background-position', '-84px -21px').css('cursor', 'default')
        // $('#' + treeNode.tId + '_ico').css('background-position', '-147px -43px')
        // } else {//如果是大于第一级且没有子节点
        $.post(
            host + '/adminx/marketingArea/listtree.do',
            {
                token: sessionStorage.getItem('token'),
                parentId: treeNode.id,
            },
            function (data) {
                data = data.data
                if (!data.length) {//如果不再有下级, 则改变switch和ico的样式
                    $('#' + treeNode.tId + '_switch').css('background-position', '-84px -21px').css('cursor', 'default')
                    $('#' + treeNode.tId + '_ico').css('background-position', '-147px -43px')
                    return false
                }
                let areaNodes = []
                $.each(data, function () {
                    areaNodes.push({
                        id: this.areaId, // 唯一标识的属性名称
                        pId: this.parentId, // 父节点唯一标识的属性名称
                        // idKey: this.areaId, // 唯一标识的属性名称
                        // pIdKey: this.parentId, // 父节点唯一标识的属性名称
                        name: this.areaName,
                        isParent: true,

                        data_areaName: this.areaName,
                        data_areaId: this.areaId,
                        data_ancestors: this.ancestors,
                    })
                })

                // console.log(areaNodes)
                let areaTree = $.fn.zTree.getZTreeObj(treeId)

                areaTree.addNodes(treeNode, areaNodes)
                if (is_getRole) {
                    getRole(areaTree)
                }
            }
        )
    }
}