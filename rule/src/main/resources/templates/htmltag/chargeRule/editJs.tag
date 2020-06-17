<script>

    /*********************变量定义区 begin*************/
    var dia;

    /*********************变量定义区 end***************/

    /******************************驱动执行区 begin***************************/
    $(function () {
        defaultGetChargeItem($("#oldChargeItem").val(),'-- 请选择 --');
        //获取规则条件数据
        getRuleCondition();
        getRuleVariable();
    });

    /******************************驱动执行区 end****************************/

    /*****************************************函数区 begin************************************/


    /*****************************************函数区 end**************************************/



    /*****************************************自定义事件区 end**************************************/

    //市场信息变更时，获取收费项、规则条件信息
    $('#marketId,#businessType').on('change', function () {
        defaultGetChargeItem('', '-- 请选择 --');
        getRuleCondition();
        getRuleVariable();
    });
    /**
     * 获取规则条件信息
     */
    function getRuleCondition(){
        let ruleId = $('#id').val();
        let marketId = $('#marketId').val();
        let businessType = $('#businessType').val();
        $('#ruleConditionDiv').html('');
        if (marketId && businessType){
            $.ajax({
                type: "POST",
                url: "${contextPath}/chargeRule/getRuleCondition.action",
                async:true,
                data: {id: ruleId, marketId: marketId, businessType: businessType},
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
    
    /**
     * 获取规则计算指标(变量)信息
     */
    function getRuleVariable(){
        let ruleId = $('#id').val();
        let marketId = $('#marketId').val();
        let businessType = $('#businessType').val();
        $('#ruleConditionDiv').html('');
        if (marketId && businessType){
        	$('#calcParamInfo').html('');
            $.ajax({
                type: "POST",
                url: "${contextPath}/chargeRule/getRuleVariable.action",
                async:true,
                data: {id: ruleId, marketId: marketId, businessType: businessType},
                success: function (ret) {
                	$.each(ret,function(){
						var label=this.label;
						var matchedKey=this.matchedKey;
						$('#calcParamInfo').append('<a href="">'+label+'</a>')
                	})
                },
                error: function (XMLHttpRequest, textStatus, errorThrown) {
                    bs4pop.alert('获取规则条件失败', { type: 0 });
                }
            })
        }
    }


    var choiceTarget;
    // 选择数据时的操作事件
    $(document).on('click', '.form-choice .choice', function () {
        debugger
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
                btns: [{label: '取消',className: 'btn btn-secondary px-5',onClick(e,$iframe){

                        }
                    }, {label: '确定',className: 'btn btn-primary px-5',onClick(e,$iframe){
                        let diaWindow = $iframe[0].contentWindow;
                        choice.attr('checkedids', diaWindow.checkedids);
                        choice.parents('.input-group').find('.form-control').val(diaWindow.checkedtexts);
                        dia.hide()

                    }
                }]

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



    //单个项清空
    $(document).on('click', '.form-group .input-group .clear',  function () {
        $(this).parents('.input-group').find('.form-control').val('');
        $(this).siblings('.choice').attr('checkedids', '');
        // isCalcParamDateRange();
    });

    /* 数组删除指定元素 */
    Array.prototype.remove = function(ele){
        let index = this.indexOf(ele);
        if( index > -1 ) {
            this.splice(index, 1);
        }
    };

    // 条件指标的校验
    function validCondition(){
        let flag = true;
        $('.form-conditionParam-box .form-range').each(function(i, ele){
            // 判断范围开始/结束比较
            let start =  $(ele).find('.input-group:nth-of-type(1) .form-control').val();
            let itemName = $(ele).find('label:first').text();
            let end = $(ele).find('.input-group:nth-of-type(2) .form-control').val();
            if ((start !== '' && end === '') || (start === '' && end !== '')) {
                bs4pop.notice(itemName + '范围输入不完整')
                flag = false
            }
            if (parseFloat(start) > parseFloat(end)) {
                bs4pop.notice(itemName + '开始不能大于结束')
                flag = false
            }
        });
        return flag;
    }

    /* ---------- 规则整个form data----START------ */
    // 基础项和计算指标项data
    function getBaseData(){
        let data = {},  calcValData = {};
        // 基础
        let basesome = $('.form-baseParam-box .form-control, [name="targetVal"], [name="remark"], [name="targetType"]').serializeArray();
        $.each(basesome, function(i, item){
            data[item.name] = item.value;
        });
        // 计算指标
        $('.form-calcParam-box .form-choice').each(function(i, ele){
            let val = $(ele).find('.choice').attr('checkedids');
            if (val) {
                let key = $(ele).find('.form-control').attr('name');
                data[key] = '['+val+']';
            }
        });
        // 计算参数
        $.each($('.calc-param-value .form-control').serializeArray(), function(i, item){
            if (item.value){
                calcValData[item.name] =  item.value ;
            }
        });
        data['targetVal'] = JSON.stringify(calcValData);
        return data;
    }

    // 条件指标data
    function getConditionData() {
        let data = {};
        // 通过弹框选择的项
        $('.form-conditionParam-box .form-choice').each(function(i, ele){
            let val = $(ele).find('.choice').attr('checkedids');
            if (val) {
                let key = $(ele).find('.form-control').attr('name');
                data[key] = '['+val+']';
            }
        });

        // 直接输入区间范围的项
        $('.form-conditionParam-box .form-range').each(function(i, ele){
            // 判断范围开始/结束比较
            let start =  $(ele).find('.input-group:nth-of-type(1) .form-control').val();
            let end = $(ele).find('.input-group:nth-of-type(2) .form-control').val();
            if (start && end) {
                let key = $(ele).attr('name');
                data[key] = '['+start + ',' + end+']';
            }
        });

        // 普通单个输入框的项
        $.each($('.form-number .form-control, .form-text .form-control').serializeArray(), function(i, item){
            if (item.value){
                data[item.name] = '['+item.value+']';
            }
        });
        return data;
    }

    // 规则整个form data
    function getRuleData(){
        return Object.assign({}, getBaseData(), {"conditions":JSON.stringify(getConditionData())});
    }

    /* ------------- 规则整个form data---END------------- */


    $(document).on('click', '.btn-cancel', function () {
        window.location.href = document.referrer;
    });
    $(document).on('click', '#formSubmit', function () {
        if ($('#addForm').validate().form() === true && validCondition()) {
            let id = $('#id').val();
            let url = '${contextPath}/chargeRule/save.action';
            debugger
            $.ajax({
                type: "POST",
                dataType: "json",
                url: url,
                data: getRuleData(),
                success: function (ret) {
                    if (ret.success) {
                        bs4pop.notice('操作成功', {type: 'danger', position: 'center'})
                        window.location.href = document.referrer;
                    } else {
                        bs4pop.notice(ret.message, {type: 'danger', position: 'center'})
                        return false;
                    }
                },
                error: function (error) {
                    bs4pop.notice(error, {type: 'danger', position: 'center'})
                    return false;
                }
            })
        }
    });


</script>