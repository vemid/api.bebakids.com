package com.example.apibebakids.model;

import java.io.Serializable;
import java.util.List;

/**
 * Response klasa za otpremnice maloprodaje
 */
public class OtpremnicaMpResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    private boolean responseResult;
    private String errorMessage;
    private int respResultCount;
    private List<OtpremnicaMpDTO> otpremnice;

    public boolean isResponseResult() {
        return responseResult;
    }

    public void setResponseResult(boolean responseResult) {
        this.responseResult = responseResult;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public int getRespResultCount() {
        return respResultCount;
    }

    public void setRespResultCount(int respResultCount) {
        this.respResultCount = respResultCount;
    }

    public List<OtpremnicaMpDTO> getOtpremnice() {
        return otpremnice;
    }

    public void setOtpremnice(List<OtpremnicaMpDTO> otpremnice) {
        this.otpremnice = otpremnice;
    }
}