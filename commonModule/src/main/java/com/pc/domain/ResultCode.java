package com.pc.domain;

public enum  ResultCode {

    SERVICE_FAIL("503"),SUCCESS("200");

    private  String resultCode;
    private ResultCode(String resultCode){
        this.resultCode = resultCode;
    }

    public String getResultCode() {
        return resultCode;
    }
}
