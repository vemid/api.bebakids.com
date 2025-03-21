package com.example.apibebakids.model;

import java.io.Serializable;
import java.util.List;

/**
 * Response klasa za povratnice maloprodaje
 */
public class PovratnicaMpResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    private boolean responseResult;
    private String errorMessage;
    private int respResultCount;
    private List<PovratnicaMpDTO> povratnice;

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

    public List<PovratnicaMpDTO> getPovratnice() {
        return povratnice;
    }

    public void setPovratnice(List<PovratnicaMpDTO> povratnice) {
        this.povratnice = povratnice;
    }
}