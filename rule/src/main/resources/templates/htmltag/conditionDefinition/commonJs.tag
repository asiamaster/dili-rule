<script>


    /**
     * 动态加载数据源信息
     * @param dataSourceId 默认数据源ID
     * @param _viewTargetId 回显控件ID
     * @param _tips 空值提示值
     */
    function getDataSourceInfo(dataSourceId, _viewTargetId, _tips) {
        let targetId = $('#' + _viewTargetId);
        targetId.empty();
        let msg = null;
        if (typeof (_tips) != "undefined" && _tips) {
            msg = "<option value=''>" + _tips + "</option>"
        }
        let dataSource = [msg];
        $.ajax({
            type: 'post',
            url: "${contextPath}/dataSourceDefinition/getDataSource.action",
            async: false,
            success: function (ret) {
                if (ret.success) {
                    $.each(ret.data, function (i, item) {
                        //数据来源信息判断
                        if (typeof (dataSourceId) != "undefined" && null != dataSourceId && '' != dataSourceId && parseInt(dataSourceId) == parseInt(item["id"])) {
                            dataSource.push('<option selected value="' + item["id"] + '">' + item["name"] + '</option>');
                        } else {
                            dataSource.push('<option value="' + item["id"] + '">' + item["name"] + '</option>');
                        }
                    });
                }
            }
        });
        targetId.html(dataSource.join(''));
        targetId.trigger('change')
    }

    /**
     * 获取数据列信息
     * @param defaultValue 默认值
     * @param _viewTargetId 回显控件ID
     * @param _tips 空值提示
     */
    function getMatchColumn(defaultValue, _viewTargetId, _tips) {
        let dataSourceId = $('#dataSourceId').val();
        let targetId = $('#' + _viewTargetId);
        targetId.empty();
        let msg = null;
        if (typeof (_tips) != "undefined" && _tips) {
            msg = "<option value=''>" + _tips + "</option>"
        }
        let data = [msg];
        if (dataSourceId) {
            $.ajax({
                type: 'post',
                url: "${contextPath}/dataSourceColumn/getByDataSource.action",
                data: {dataSourceId: dataSourceId},
                async: false,
                success: function (ret) {
                    if (ret.success) {
                        $.each(ret.data, function (i, item) {
                            //数据来源信息判断
                            if (typeof (defaultValue) != "undefined" && null != defaultValue && '' != defaultValue && defaultValue == item["columnCode"]) {
                                data.push('<option selected value="' + item["columnCode"] + '">' + item["columnName"] + '</option>');
                            } else {
                                data.push('<option value="' + item["columnCode"] + '">' + item["columnName"] + '</option>');
                            }
                        });
                    } else {
                        bs4pop.alert(ret.result, {width: 400, type: 'error'});
                    }
                }
            });
        }
        targetId.html(data.join(''));
    }

</script>