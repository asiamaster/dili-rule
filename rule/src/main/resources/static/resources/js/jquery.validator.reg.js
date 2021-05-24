jQuery.validator.addMethod("reg", function (v, a) {
    try {
        let regText = $(a).data('rule-reg');
        debugger
        if ($.trim(regText) != '' && !(new RegExp($.trim(regText))).test(v)) {
            return false;
        }
        return true;
    } catch (e) {
        return false;
    }
}, "输入内容格式错误");