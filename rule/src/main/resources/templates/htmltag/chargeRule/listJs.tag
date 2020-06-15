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
        let marketId = $('#marketId').val();
        let systemCode = $('#systemCode').val();
        let businessType = $('#businessType').val();
        let chargeItem = $('#chargeItem').val();
        let url = "/chargeRule/preSave.html?marketId=" + marketId+'&systemCode='+systemCode+'&businessType='+businessType+'&chargeItem='+chargeItem;
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
        let url = "/chargeRule/preSave.html?id=" + selectedRow.id + "&dataSourceId=" + selectedRow.dataSourceId;
        dia = bs4pop.dialog({
            title: '更新数据列',
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
     * 禁启用操作
     * @param enable 是否启用:true-启用
     */
    function doEnableHandler(enable) {

    }

    /**
     * 检查审核是否通过操作
     * @param pass 是否通过:true-通过
     */
    function doCheckHandler(pass) {

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