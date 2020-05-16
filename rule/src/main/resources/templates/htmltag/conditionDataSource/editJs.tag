<script>

    /**
     * 保存数据
     */
    $('#formSubmit').on('click', function () {
        if (!$('#addForm').valid()) {
            return false;
        } else {
            //获取数据来源值
            let dataSourceType = $('#dataSourceType').val();
            if (dataSourceType == '${@com.dili.rule.domain.enums.DataSourceTypeEnum.LOCAL.getCode()}') {   //本地数据
                let dataJson = $.trim($('#dataJson').val()); //获取本地数据值
                if (null == dataJson || '' == dataJson) {
                    bs4pop.alert("数据来源为本地时，本地数据不能为空", {width: 400, type: 'error'});
                    return;
                }
            } else if (dataSourceType == '${@com.dili.rule.domain.enums.DataSourceTypeEnum.REMOTE.getCode()}') {
                // 获取远程数据值的url
                let queryUrl = $.trim($('#queryUrl').val());
                if (null == queryUrl || '' == queryUrl) {
                    bs4pop.alert("数据来源为远程时，数据URL不能为空", {type: 'error'});
                    return;
                }
            }
            bui.loading.show('努力提交中，请稍候。。。');
            // let _formData = new FormData($('#addForm')[0]);
            let _formData = $('#addForm').serialize();
            let url = "${contextPath}/conditionDataSource/save.action";
            $.ajax({
                type: "POST",
                url: url,
                data: _formData,
                async: true,
                success: function (ret) {
                    bui.loading.hide();
                    if (ret.success) {
                        bs4pop.alert('保存成功', {type: 'success',width: 400}, function () {
                            parent.dia.hide();
                            parent.queryDataHandler();
                        });
                    } else {
                        bs4pop.alert(ret.message, {width: 400,type: 'error'});
                    }
                },
                error: function (error) {
                    bui.loading.hide();
                    bs4pop.alert(error.result, {width: 400,type: 'error'},function () {

                    });
                }
            });
        }
    });

</script>