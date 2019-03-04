package com.demo.exception;

public class CommonException extends Exception {

    public final CommonError commonError;

    public CommonException(CommonError commonError) {
        this.commonError = commonError;
    }
}
