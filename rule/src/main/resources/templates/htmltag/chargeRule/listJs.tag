<script>

    /*********************变量定义区 begin*************/
        //行索引计数器
        //如 let itemIndex = 0;
    let _dataGrid = $('#chargeRuleGrid');
    let currentSelectRowIndex;
    var dia;

    /*********************变量定义区 end***************/

    /******************************驱动执行区 begin***************************/
    $(function () {
        //获取默认的收费项
        defaultGetChargeItem();
        $(window).resize(function () {
            _dataGrid.bootstrapTable('resetView')
        });
        let size = ($(window).height() - $('#chargeRuleQueryForm').height() - 210) / 40;
        size = size > 10 ? size : 10;
        _dataGrid.bootstrapTable('refreshOptions', {url: '/chargeRule/listPage.action', pageSize: parseInt(size)});

        _dataGrid.on('load-success.bs.table', function () {
            $('[data-toggle="tooltip"]').tooltip()
        });

    });

    /******************************驱动执行区 end****************************/

    /*****************************************函数区 begin************************************/
    /**
     时间范围
     */
    lay('.laydatetime').each(function () {
        laydate.render({
            elem: this
            , trigger: 'click'
        });
    });

    /**
     * 打开新增窗口
     */
    function openInsertHandler() {
        let marketId = $('#marketId').val()==null?'':$('#marketId').val();
        let businessType = $('#businessType').val()==null?'':$('#businessType').val();
        let chargeItem = $('#chargeItem').val()==null?'':$('#chargeItem').val();
        let url = "/chargeRule/preSave.html?marketId=" + marketId+'&businessType='+businessType+'&chargeItem='+chargeItem;
        dia = bs4pop.dialog({
            title: '新增规则',
            content: url,
            isIframe: true,
            closeBtn: true,
            backdrop: 'static',
            width: '98%',
            height: '98%',
            btns: []
        });
    }

    /**
     * 打开更新窗口
     */
    function openUpdateHandler() {
        //获取选中行的数据
        let rows = _dataGrid.bootstrapTable('getSelections');
        if (null == rows || rows.length == 0) {
            bs4pop.alert('请选中一条数据');
            return;
        }
        //table选择模式是单选时可用
        let selectedRow = rows[0];
        let revisable = selectedRow.revisable;
        if (revisable != ${@com.dili.commons.glossary.YesOrNoEnum.YES.getCode()}) {
            bs4pop.alert('此规则已存在被修改的记录，暂时不能修改', {type: 'warning'});
            return;
        }
        let url = "/chargeRule/preSave.html?id=" + selectedRow.id;
        dia = bs4pop.dialog({
            title: '更新规则',
            content: url,
            isIframe: true,
            closeBtn: true,
            backdrop: 'static',
            width: '98%',
            height: '98%',
            btns: []
        });
    }

    /**
     * 删除数据列信息
     */
    function doDeleteHandler() {
        //获取选中行的数据
        let rows = _dataGrid.bootstrapTable('getSelections');
        if (null == rows || rows.length == 0) {
            bs4pop.alert('请选中一条数据');
            return;
        }
        let selectedRow = rows[0];
        let msg = '删除后，规则不可使用亦不可恢复，是否删除？';
        bs4pop.confirm(msg, undefined, function (sure) {
            if(sure){
                bui.loading.show('努力提交中，请稍候。。。');
                $.ajax({
                    type: "POST",
                    url: "${contextPath}/chargeRule/delete.action",
                    data: {id: selectedRow.id},
                    processData:true,
                    dataType: "json",
                    async : true,
                    success : function(ret) {
                        bui.loading.hide();
                        if(ret.success){
                            queryDataHandler();
                        }else{
                            bs4pop.alert(ret.result, {type: 'error'});
                        }
                    },
                    error : function() {
                        bui.loading.hide();
                        bs4pop.alert('远程访问失败', {type: 'error'});
                    }
                });
            }
        });
    }

    /**
     * 查看规则详细信息
     */
    function openViewHandler() {
        //获取选中行的数据
        let rows = _dataGrid.bootstrapTable('getSelections');
        if (null == rows || rows.length == 0) {
            bs4pop.alert('请选中一条数据');
            return;
        }
        //table选择模式是单选时可用
        let selectedRow = rows[0];
        let url = "/chargeRule/view.action?id=" + selectedRow.id;
        dia = bs4pop.dialog({
            title: '规则详情',
            content: url,
            isIframe: true,
            closeBtn: true,
            backdrop: 'static',
            width: '98%',
            height: '98%',
            btns: []
        });
    }

    /**
     * 禁启用操作
     * @param enable 是否启用:true-启用
     */
    function doEnableHandler(enable) {
        //获取选中行的数据
        let rows = _dataGrid.bootstrapTable('getSelections');
        if (null == rows || rows.length == 0) {
            bs4pop.alert('请选中一条数据');
            return;
        }
        let selectedRow = rows[0];
        let msg = (enable || 'true' == enable) ? '确定要启用该规则吗？' : '确定要禁用该规则吗？';
        bs4pop.confirm(msg, undefined, function (sure) {
            if(sure){
                bui.loading.show('努力提交中，请稍候。。。');
                $.ajax({
                    type: "POST",
                    url: "${contextPath}/chargeRule/enable.action",
                    data: {id: selectedRow.id, enable: enable},
                    processData:true,
                    dataType: "json",
                    async : true,
                    success : function(ret) {
                        bui.loading.hide();
                        if(ret.success){
                            queryDataHandler();
                        }else{
                            bs4pop.alert(ret.result, {type: 'error'});
                        }
                    },
                    error : function() {
                        bui.loading.hide();
                        bs4pop.alert('远程访问失败', {type: 'error'});
                    }
                });
            }
        });
    }

    /**
     * 检查审核是否通过操作
     * @param pass 是否通过:true-通过
     */
    function doCheckHandler(pass) {
        //获取选中行的数据
        let rows = _dataGrid.bootstrapTable('getSelections');
        if (null == rows || rows.length == 0) {
            bs4pop.alert('请选中一条数据');
            return;
        }
        let selectedRow = rows[0];
        let msg = (pass || 'true' == pass) ? '确定要通过该条规则吗？' : '确定要拒接该条规则吗？';
        bs4pop.confirm(msg, undefined, function (sure) {
            if(sure){
                bui.loading.show('努力提交中，请稍候。。。');
                $.ajax({
                    type: "POST",
                    url: "${contextPath}/chargeRule/approve.action",
                    data: {id: selectedRow.id, pass: pass},
                    processData:true,
                    dataType: "json",
                    async : true,
                    success : function(ret) {
                        bui.loading.hide();
                        if(ret.success){
                            queryDataHandler();
                        }else{
                            bs4pop.alert(ret.result, {type: 'error'});
                        }
                    },
                    error : function() {
                        bui.loading.hide();
                        bs4pop.alert('远程访问失败', {type: 'error'});
                    }
                });
            }
        });
    }

    /**
     * 查询处理
     */
    function queryDataHandler() {
        currentSelectRowIndex = undefined;
        $('#toolbar button').attr('disabled', false);
        _dataGrid.bootstrapTable('refresh');
    }

    /**
     * table参数组装
     * 可修改queryParams向服务器发送其余的参数
     * @param params
     */
    function queryParams(params) {
        let temp = {
            rows: params.limit,   //页面大小
            page: ((params.offset / params.limit) + 1) || 1, //页码
            sort: params.sort,
            order: params.order
        };
        return $.extend(temp, bui.util.bindGridMeta2Form('chargeRuleGrid', 'chargeRuleQueryForm'));
    }

    /*****************************************函数区 end**************************************/

    //选中行事件
    _dataGrid.on('uncheck.bs.table', function (e, row, $element) {
        currentSelectRowIndex = undefined;
    });


    //选中行事件 -- 可操作按钮控制
    _dataGrid.on('check.bs.table', function (e, row, $element) {
        let state = row.$_state;
        /**
         * 参与控制的操作按钮有：[通过]、[不通过]、[禁用]、[启用]
         * 在参数控制的按钮上添加了伪css control-btn 用来批量处理
         * 1.[待审核]状态下，可操作 [通过]、[不通过]
         * 2.[启用]或[未开始]状态下，可操作 [禁用]
         * 3.[禁用]状态下，可操作 [启用]
         * 4.[未通过]状态下，可操作 [通过]
         * 其它状态，则不可操作以上按钮
         */

        if (state == ${@com.dili.rule.domain.enums.RuleStateEnum.UNAUDITED.getCode()}) { //待审核
            $('.control-btn').attr('disabled', true);
            $('#btn_check_pass').attr('disabled', false);
            $('#btn_check_not_pass').attr('disabled', false);
        } else if (state == ${@com.dili.rule.domain.enums.RuleStateEnum.ENABLED.getCode()}) { //启用
            $('.control-btn').attr('disabled', true);
            $('#btn_disable').attr('disabled', false);
        } else if (state == ${@com.dili.rule.domain.enums.RuleStateEnum.DISABLED.getCode()}) {  //禁用
            $('.control-btn').attr('disabled', true);
            $('#btn_enable').attr('disabled', false);
        } else if (state == ${@com.dili.rule.domain.enums.RuleStateEnum.NOT_PASS.getCode()}) {  //未通过
            $('.control-btn').attr('disabled', true);
            $('#btn_check_pass').attr('disabled', false);
        } else {
            $('.control-btn').attr('disabled', true);
        }
    });


    /*****************************************自定义事件区 end**************************************/

    /**
     * 显示栏格式化显示tip
     * @param value
     * @param row
     * @param index
     */
    function dataFormatterTip(value,row,index) {
        if (value) {
            return "<span data-toggle='tooltip' data-placement='left' title='" + value + "'>" + value + "</span>";
        } else {
            return "";
        }
    }

    /**
     * 当市场或系统改变时，重新获取业务类型
     */
    function getBizType() {
        //重新获取业务类型
        defaultGetBizTypeInfo();
        //重新获取业务类型后，需重新获取收费项
        defaultGetChargeItem();
    }


</script>