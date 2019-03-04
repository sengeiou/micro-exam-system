package com.demo.dto;

import com.demo.exception.CommonError;

import java.util.HashMap;
import java.util.Map;

/**
 * 封装json对象，所有返回结果都使用它
 */
public class CommonResult extends HashMap<String, Object> {

    //TODO::深度依赖了，后面需要统一throw
    @Deprecated
    public static final String COMMON_ERROR = "接口调用出错";

    public static final String MESSAGE = "message";
    public static final String SUCCESS = "success";
    public static final String DATA = "data";
    public static final String CODE = "code";

    public static final CommonResult BLANK_SUCCESS = new CommonResult().setSuccess(true);

    private static final Map<CommonError, CommonResult> map = new HashMap<>();

    public static CommonResult fixedError(CommonError commonError) {
        if (map.get(commonError) == null) {
            synchronized (CommonResult.class) {
                map.computeIfAbsent(commonError, e -> new CommonResult().setState(e));
            }
        }
        return map.get(commonError);
    }

    public CommonResult() {
        put(MESSAGE, "");
        put(SUCCESS, true);
    }

    public CommonResult setState(CommonError commonError) {
        put(MESSAGE, commonError.errMsg);
        put(CODE, commonError.code);
        put(SUCCESS, false);
        return this;
    }

    private CommonResult setCode(int code) {
        put(CODE, code);
        return this;
    }

    public CommonResult setMessage(String msg) {
        setSuccess(false);
        put(MESSAGE, msg);
        return this;
    }

    public CommonResult setSuccess(boolean ret) {
        put(SUCCESS, ret);
        return this;
    }

    public CommonResult setData(Object obj) {
        setSuccess(true);
        put(DATA, obj);
        return this;
    }

}
