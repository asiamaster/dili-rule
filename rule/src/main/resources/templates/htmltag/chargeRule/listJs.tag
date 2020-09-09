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
    function doModifyBackupHandler(){
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
        let url = "/chargeRule/preSave.html?id=" + selectedRow.backupedRuleId;
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
                    url: "${contextPath}/chargeRule/deleteBackupRule.action",
                    data: {id: selectedRow.backupedRuleId},
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
        if ($('#chargeRuleQueryForm').validate().form()) {
            currentSelectRowIndex = undefined;
            $('#toolbar button').attr('disabled', false);
            _dataGrid.bootstrapTable('refresh');
        } else {
            bs4pop.notice("请完善必填项", {type: 'warning', position: 'topleft'});
        }
    }

    /**
    更新groupid
    */

    function updateGroupId(ruleId,groupId){
        bui.loading.show('努力提交中，请稍候。。。');
        $.ajax({
            contentType: 'application/json',
            type: "POST",
            url: "${contextPath}/chargeRule/updateGroupId.action",
            data: JSON.stringify({id: ruleId, groupId: groupId}),
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
    _dataGrid.on('dbl-click-cell.bs.table', function (e, field, value, row, $element) {
    
            if('groupId'!==field){
                return;
            }
            var ruleId=row.id;
            var groupId=row.groupId;
            $element.attr('contenteditable', true);
            $element.focus();
            $element.one("blur", function(ev){
                let index = $element.parent().data('index');
                let tdValue = $element.html();
                $('#chargeRuleGrid').bootstrapTable('updateCell', {
                     index: index,       //行索引
                     field: field,       //列名
                     value: tdValue        //cell值
                });
              
                updateGroupId(ruleId,tdValue);
                $element.removeAttr('contenteditable', true);
              });
            $element.keypress(function(e){
                 /*console.info($element.val());
                 console.info(groupId)
                 console.info($element.html())*/
                 if($.trim($element.html())==''){
                     if(e.keyCode==48){
                         return false;
                     }
                }
                if(e.keyCode>=48&&e.keyCode<=57){
                    if(parseInt($.trim($element.html()))>=999||$.trim($element.html()).length==3){
                        return false;
                    }
                    return true;
                }
                if(e.keyCode==13){
                       $element.trigger('blur');
                }
                
                return false;
            });
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

          if (state == ${@com.dili.rule.domain.enums.RuleStateEnum.ENABLED.getCode()}) { //启用
            $('.control-btn').attr('disabled', true);
            $('#btn_disable').attr('disabled', false);
        } else if (state == ${@com.dili.rule.domain.enums.RuleStateEnum.DISABLED.getCode()}) {  //禁用
            $('.control-btn').attr('disabled', true);
            $('#btn_enable').attr('disabled', false);
        }  else {
            $('.control-btn').attr('disabled', true);
        }
        if ($.type(row.backupedRuleId) !='undefined') {  
            $('.control-btn').attr('disabled', true);
            $('#btn_delete_backup').attr('disabled', false);
            $('#btn_edit_backup').attr('disabled', false);
        }else{
            $('#btn_delete_backup').attr('disabled', true);
            $('#btn_edit_backup').attr('disabled', true);
        }
    });


    /*****************************************自定义事件区 end**************************************/

    /**
     * 规则优先级调整
     * @param id 规则ID
     * @param _flag 是否调升
     */
    function adjustPriorityHandler(id, _flag) {
        bui.loading.show('努力提交中，请稍候。。。');
        $.ajax({
            type: "POST",
            url: "${contextPath}/chargeRule/adjustPriority.action",
            data: {id: id, enlarge: _flag},
            processData: true,
            dataType: "json",
            async: true,
            success: function (ret) {
                bui.loading.hide();
                if (ret.success) {
                    queryDataHandler();
                } else {
                    bs4pop.notice(ret.result, {type: 'warning',position: 'center'});
                }
            },
            error: function () {
                bui.loading.hide();
                bs4pop.alert('远程访问失败', {type: 'error',position: 'center'});
            }
        });
    }

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
     * 优先级数据格式化操作显示
     */
    function priorityFormatter(value,row,index) {
        <%if(hasResource("adjustPriority")) {%>
            return '<a class="like" href="javascript:void(0)" onclick="adjustPriorityHandler(' + row.id + ',true)" >向上</a>&nbsp;&nbsp;' +
            '<a class="like" href="javascript:void(0)" onclick="adjustPriorityHandler(' + row.id + ',false)" >向下</a>';
        <%}else{%>
            return '向上&nbsp;&nbsp;向下';
        <%}%>
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

    /**
    *清空查询条件
    */
    function clearForm(){
        $("#businessType option:first").prop("selected", 'selected');  
        $("#state option:first").prop("selected", 'selected');  
        $("#ruleName").val('')
        defaultGetChargeItem();
    }
</script>