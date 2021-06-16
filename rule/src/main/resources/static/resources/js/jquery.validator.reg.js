jQuery.validator.addMethod('reg', function (value, element, param) {
    var customMsg = '输入内容格式错误';
    var result    = true;
    try {
        debugger
        let regText = $(element).data('rule-reg');
        if ($.trim(regText) != '' && !(new RegExp($.trim(regText))).test(value)) {
            result= false;
        }
        let regMessage = $(element).data('reg-msg');
        if($.trim(regMessage) != ''){
            customMsg= $.trim(regMessage);
        }
    } catch (e) {
        result=false;
    }
    jQuery.validator.messages.reg = customMsg;
    return result;
});