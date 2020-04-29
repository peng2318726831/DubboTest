package com.pc.domain;

public class ErrorResult {

    private  String resultCode;

    private String msg;

    public ErrorResult(String resultCode,String msg){
        this.resultCode = resultCode;
        this.msg = msg;
    }

    public String getResultCode() {
        return resultCode;
    }

    public void setResultCode(String resultCode) {
        this.resultCode = resultCode;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
