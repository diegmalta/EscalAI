package com.ufrj.escalaiv2.dto;

import com.google.gson.annotations.SerializedName;

public class VerifyEmailRequest {
    @SerializedName("email")
    private String email;

    @SerializedName("code")
    private String code;

    public VerifyEmailRequest(String email, String code) {
        this.email = email;
        this.code = code;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}



