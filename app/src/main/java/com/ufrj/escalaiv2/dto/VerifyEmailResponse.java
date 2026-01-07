package com.ufrj.escalaiv2.dto;

import com.google.gson.annotations.SerializedName;

public class VerifyEmailResponse {
    @SerializedName("success")
    private boolean success;

    @SerializedName("message")
    private String message;

    @SerializedName("already_verified")
    private Boolean alreadyVerified;

    @SerializedName("email")
    private String email;

    public VerifyEmailResponse() {
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Boolean getAlreadyVerified() {
        return alreadyVerified;
    }

    public void setAlreadyVerified(Boolean alreadyVerified) {
        this.alreadyVerified = alreadyVerified;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}



