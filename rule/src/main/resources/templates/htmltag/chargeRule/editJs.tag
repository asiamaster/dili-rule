<script>

    /*********************变量定义区 begin*************/
    var dia;

    /*********************变量定义区 end***************/

    /******************************驱动执行区 begin***************************/
    $(function () {
        //获取业务类型
        getBizType();
        //获取规则条件数据
        getRuleCondition();
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
            , type: 'datetime'
        });
    });


    /*****************************************函数区 end**************************************/



    /*****************************************自定义事件区 end**************************************/

    /**
     * 当市场或系统改变时，重新获取业务类型
     */
    function getBizType() {
        //重新获取业务类型
        defaultGetBizTypeInfo();
        //重新获取业务类型后，需重新获取收费项
        defaultGetChargeItem();
    }

    //业务类型变更时，获取收费项、规则条件信息
    $('#businessType').on('change', function(){
        defaultGetChargeItem();
        getRuleCondition();
    });

    /**
     * 获取规则条件信息
     */
    function getRuleCondition(){
        let ruleId = $('#id').val();
        let marketId = $('#marketId').val();
        let systemCode = $('#systemCode').val();
        let businessType = $('#businessType').val();
        $('#ruleConditionDiv').html('');
        if (marketId && systemCode && businessType){
            $.ajax({
                type: "POST",
                url: "${contextPath}/chargeRule/getRuleCondition.action",
                async:false,
                data: {id: ruleId, marketId: marketId, systemCode: systemCode, businessType: businessType},
                success: function (ret) {
                    $('#ruleConditionDiv').html(ret);
                    $('[name="condition"]').on('blur', '.cusIsNaturalNum', function(){
                        $(this).siblings('.error').text('');
                        if ($(this).val() && ( !(/^(0|[1-9][0-9]*)$/.test($(this).val())) || parseFloat($(this).val()) > 9999999)) {
                            $(this).siblings('.error').text('请输0到9999999之间数');
                            $(this).val('');
                        }
                    });
                    $('[name="condition"]').on('blur', '.cusFloatReserve', function(){
                        $(this).siblings('.error').text('');
                        if ($(this).val() && !(/^(([1-9]\d+)|\d)(\.\d{1,2})?$/.test($(this).val()))) {
                            $(this).siblings('.error').text('最多两位小数');
                            $(this).val('');
                        } else if (parseFloat($(this).val()) > 9999999.99) {
                            $(this).siblings('.error').text('请输0到9999999.99之间数');
                            $(this).val('');
                        }
                    });
                },
                error: function (XMLHttpRequest, textStatus, errorThrown) {
                    bs4pop.alert('获取规则条件失败', { type: 0 });
                }
            })
        }
    }

    let choiceTarget;
    // 选择数据时的操作事件
    $(document).on('click', '.form-choice .choice', function () {
        let choice = $(this);
        choiceTarget = choice;
        let definitionId = choice.data('definitionid');
        let title = '选择' + choice.data('label');
        let choiceType = choice.data('type');
        if(choiceType=='conditionType'){
            dia = bs4pop.dialog({
                title: title,
                content: "${contextPath}/conditionDefinition/getConditionData.action?definitionId="+definitionId,
                isIframe: true,
                closeBtn: true,
                backdrop: 'static',
                width: '98%',
                height: '95%',
                btns: []
            });
        }else if(choiceType=='targetType'){
            // layer.open({
            //     area: ['600px', '400px'],
            //     title: title,
            //     content: [get_targetType_url], //iframe的url
            //     btn: ['确定', '取消'],
            //     yes: function (index, layero) {
            //         getCheckedItem(choice, index, layero);
            //         $('.form-calcParam-box .calc-param .form-control').trigger('change');
            //     },
            //     btn2: function() {
            //         layer.closeAll();
            //     }
            // });
        }
    });

</script>