
//获取业务类型的url地址
let getBusinessTypeUrl = '/commonInfo/getBusinessType.action';
//获取收费项信息
let getChargeItemUrl = '/commonInfo/getChargeItem.action';

/**
 * 默认实现的获取业务类型的方法
 * @param _defaultValue 默认值
 * @param _fromLocal 是否从本地获取
 * @param _tips 首项提示信息(全部？请选择？null?)
 */
function defaultGetBizTypeInfo(_defaultValue, _fromLocal, _tips) {
    let marketId = $('#marketId').val();
    let systemCode = $('#systemCode').val();
    getBusinessType(marketId, systemCode, 'businessType', _defaultValue, _fromLocal, _tips);
}

/**
 * 根据市场及系统，获取对应的业务类型
 * 返回数据为下拉框中的option，会默认清空原来的值
 * @param marketId 市场值
 * @param systemCode 系统值
 * @param _viewTargetId 下拉回显到控件ID
 * @param _defaultValue 默认值(默认选中的值)
 * @param _fromLocal 是否从本地获取
 * @param _tips 首项提示信息(全部？请选择？null?)
 */
function getBusinessType(marketId, systemCode, _viewTargetId, _defaultValue, _fromLocal, _tips) {
    if (typeof (_fromLocal) != "undefined") {
        _fromLocal = false;
    }
    let targetId = $('#' + _viewTargetId);
    targetId.empty();
    let msg = null;
    if (typeof (_tips) != "undefined" && _tips) {
        msg = "<option value=''>" + _tips + "</option>"
    }
    let datas = [msg];
    if (typeof (marketId) != "undefined" && typeof (systemCode) != "undefined" && marketId && systemCode) {
        $.ajax({
            type: 'post',
            url: getBusinessTypeUrl,
            data: {marketId: marketId, systemCode: systemCode, fromLocal: _fromLocal},
            async: false,
            success: function (ret) {
                if (ret.success) {
                    $.each(ret.data, function (k, v) {
                        if (typeof (_defaultValue) != "undefined" && null != _defaultValue && '' != _defaultValue && _defaultValue == k) {
                            datas.push('<option selected value="' + k + '">' + v + '</option>');
                        } else {
                            datas.push('<option value="' + k + '">' + v + '</option>');
                        }
                    });
                }
            }
        });
    }
    targetId.html(datas.join(''));
}

/**
 * 根据信息获取收费项
 * @param marketId 市场
 * @param businessType 业务类型
 * @param _viewTargetId 回显ID
 * @param _defaultValue 默认值
 * @param _tips 首项提示信息(全部？请选择？null?)
 */
function getChargeItem(marketId, businessType, _viewTargetId, _defaultValue, _tips) {
    let targetId = $('#' + _viewTargetId);
    targetId.empty();
    let msg = null;
    if (typeof (_tips) != "undefined" && _tips) {
        msg = "<option value=''>" + _tips + "</option>"
    }
    let datas = [msg];
    if (typeof (marketId) != "undefined" && typeof (businessType) != "undefined" && marketId && businessType) {
        $.ajax({
            type: 'post',
            url: getChargeItemUrl,
            data: {marketId: marketId, businessType: businessType},
            async: false,
            success: function (ret) {
                if (ret.success) {
                    $.each(ret.data, function (i, item) {
                        if (typeof (_defaultValue) != "undefined" && null != _defaultValue && '' != _defaultValue && _defaultValue == item.id) {
                            datas.push('<option selected value="' + item.id + '">' + item.name + '</option>');
                        } else {
                            datas.push('<option value="' + item.id + '">' + item.name + '</option>');
                        }
                    });
                }
            }
        });
        targetId.html(datas.join(''));
    }
}

/**
 * 默认实现的获取收费项的方法
 * @param _defaultValue 默认值
 * @param _tips 为空时的显示值
 */
function defaultGetChargeItem(_defaultValue, _tips) {
    let marketId = $('#marketId').val();
    let businessType = $('#businessType').val();
    getChargeItem(marketId, businessType, 'chargeItem', _defaultValue, _tips);
}