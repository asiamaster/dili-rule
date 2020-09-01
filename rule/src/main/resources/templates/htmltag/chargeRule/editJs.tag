<script>

    /*********************变量定义区 begin*************/
    var dia;
    var choiceTarget;   //存储多选时的选择项
    let variable ;


    /*********************变量定义区 end***************/

    /******************************驱动执行区 begin***************************/
    $(function () {
        //获取规则条件数据
        getRuleCondition();
        getRuleVariable();
    });

    /******************************驱动执行区 end****************************/

    /*****************************************函数区 begin************************************/


    /*****************************************函数区 end**************************************/



    /*****************************************自定义事件区 end**************************************/

    /**
     * 获取规则条件信息
     */
    function getRuleCondition() {
        let ruleId = $('#id').val();
        let marketId = $('#marketId').val();
        let businessType = $('#businessType').val();
        $('#ruleConditionDiv').html('');
        if (marketId && businessType) {
            $.ajax({
                type: "POST",
                url: "${contextPath}/chargeRule/getRuleCondition.action",
                async: true,
                data: {id: ruleId, marketId: marketId, businessType: businessType},
                success: function (ret) {
                    $('#ruleConditionDiv').html(ret);
                    $('.eqInput').on('input', function () {
                        var target = $(this).data('target')
                        $('#' + target).val($(this).val());
                    });
                    $('.betweenMinInput').on('input', function () {
                        var target = $(this).data('target')
                        var currentVal = $('#' + target).val();
                        if (currentVal.indexOf(",") == -1) {
                            $('#' + target).val($(this).val() + ',');
                        } else {
                            $('#' + target).val($(this).val() + ',' + currentVal.split(",")[1]);
                        }
                    });
                    $('.betweenMaxInput').on('input', function () {
                        var target = $(this).data('target')
                        var currentVal = $('#' + target).val();
                        if (currentVal.indexOf(",") == -1) {
                            $('#' + target).val("," + $(this).val());
                        } else {
                            $('#' + target).val(currentVal.split(",")[0] + ',' + $(this).val());
                        }
                    });


                    $('[name="condition"]').on('blur', '.cusIsNaturalNum', function () {
                        $(this).siblings('.error').text('');
                        if ($(this).val() && (!(/^(0|[1-9][0-9]*)$/.test($(this).val())) || parseFloat($(this).val()) > 9999999)) {
                            $(this).siblings('.error').text('请输0到9999999之间数');
                            $(this).val('');
                        }
                    });
                    $('[name="condition"]').on('blur', '.cusFloatReserve', function () {
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
                    bs4pop.alert('获取规则条件失败', {type: 0});
                }
            })
        }
    }

    /**
     * 获取规则计算指标(变量)信息
     */
    function getRuleVariable() {
        let ruleId = $('#id').val();
        let marketId = $('#marketId').val();
        let businessType = $('#businessType').val();
        $('#ruleConditionDiv').html('');
        if (marketId && businessType) {
            $('#variableListDiv').html('');
            $.ajax({
                type: "POST",
                url: "${contextPath}/chargeRule/getRuleVariable.action",
                async: true,
                data: {id: ruleId, marketId: marketId, businessType: businessType},
                success: function (ret) {
                    var options = {variables: []};
                    $.each(ret, function () {
                        let label = this.label;
                        let matchKey = this.matchKey;
                        options.variables.push({variableId: this.id, name: matchKey})
                        $('#variableListDiv').append('<button class="btn btn-outline-secondary mr-1 btn-variable" type="button" data-variable="' + matchKey + '">' + label + '(' + matchKey + ')</button> ')

                    });
                    if ($('.expressionInput').attr('exp-id')) {
                        var expid = $('.expressionInput').attr('exp-id');
                        $('.expressionInput').removeAttr('exp-id');
                        var expressionInput = $('.expressionInput').clone();
                        $('#expressionDiv').html('');
                        $('#expressionDiv').append(expressionInput);
                        $('div[exp-id="' + expid + '"]').remove();
                    }
                    var expBuilder = $('.expressionInput').expressionBuilder(options);
                    // $('.expressionInput').trigger('input')
                    $('.expressionInput').on('input', function () {
                        var target = $(this).data('target');
                        if (expBuilder.isValid() == true) {
                            var inputExpression = expBuilder.getInput();
                            $.each(options.variables, function () {
                                inputExpression = inputExpression.replace(new RegExp(this.name, 'g'), "[" + this.variableId + "]");
                            });
                            $(target).val(inputExpression);
                        } else {
                            $(target).val("");
                        }
                    });
                    $.each(options.variables, function () {
                        var val = $('.expressionInput').val().replace('[' + this.variableId + ']', this.name);
                        $('.expressionInput').val(val)
                    });
                    expBuilder.isValid();
                },
                error: function (XMLHttpRequest, textStatus, errorThrown) {
                    bs4pop.alert('获取计算指标失败', {type: 0});
                }
            })
        }
    }



    //点击计算参数项
    $(document).on('click', '#calcVariable .btn-variable', function(){
        variable  = $('.expressionInput').val() + $(this).data('variable');
        $('[name="expressionInput"]').val(variable)
        $('.expressionInput').trigger('input');
    });

    $('#calcParamInfo .clear').on('click', function(){
        variable = '';
        $('.expressionInput').val(variable);
    });


    // 选择数据时的操作事件
    $(document).on('click', '.form-choice .choice', function () {
        let choice = $(this);
        choiceTarget = choice;
        let definitionId = choice.data('definitionid');
        let title = '选择' + choice.data('label');
        let choiceType = choice.data('type');
        let targetId = choice.data('target');
        if(choiceType=='matchType'){
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
                        $('#'+targetId).val(diaWindow.checkedids)
                        choice.data('checkedids', diaWindow.checkedids);
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

    /* ---------- 规则整个form data----START------ */
    // 基础项和计算指标项data
    function buildData() {
        let formJson = $('#addForm').serializeJSON({
            useIntKeysAsArrayIndex: true,
            customTypes: {
                stringToArray: function (str) {
                    if (str) {
                        return $.makeArray(str.split(","));
                    }
                    return [];
                },
                calculateExpression: function (str) {

                }
            }//end customTypes
        });
        return formJson;
    }

    $(document).on('click', '.btn-cancel', function () {
        window.location.href = document.referrer;
    });
    /**
     * 表单保存操作
     */
    $(document).on('click', '#formSubmit', function () {
        if ($('#addForm').validate().form() === true) {
            let expressionInput = $('#expressionInput').expressionBuilder();
            if ($('select[name="actionExpressionType"]').val()=='1'&&!expressionInput.isValid()){
                bs4pop.alert("计算表达式输入不正确", {type: 'error', position: 'center'});
                return;
            }
            bui.loading.show('努力提交中，请稍候。。。');
            let id = $('#id').val();
            let url = '${contextPath}/chargeRule/save.action';
            let data = buildData();

            $.ajax({
                type: "POST",
                dataType: "json",
                data: JSON.stringify(data),
                contentType: 'application/json',
                url: url,
                success: function (ret) {
                    bui.loading.hide();
                    if (ret.success) {
                        bs4pop.notice('操作成功', {type: 'info', position: 'center'});
                        parent.dia.hide();
                        parent.queryDataHandler();
                    } else {
                        bs4pop.alert(ret.message, {type: 'warning', position: 'center'})
                        return false;
                    }
                },
                error: function (error) {
                    bui.loading.hide();
                    bs4pop.alert(error, {type: 'error', position: 'center'})
                    return false;
                }
            })
        }
    });


</script>