package com.ufrj.escalaiv2.dto;

import com.google.gson.annotations.SerializedName;

public class LoginResponse {
    @SerializedName("token")
    private String token;

    @SerializedName("user")
    private UserData user;

    @SerializedName("expires_in")
    private long expiresIn;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public UserData getUser() {
        return user;
    }

    public void setUser(UserData user) {
        this.user = user;
    }

    public long getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(long expiresIn) {
        this.expiresIn = expiresIn;
    }

    public static class UserData {
        @SerializedName("id")
        private long id;

        @SerializedName("name")
        private String name;

        @SerializedName("email")
        private String email;

        @SerializedName("gender")
        private String gender;

        @SerializedName("weight")
        private double weight;

        @SerializedName("height")
        private int height;

        @SerializedName("climbing_grades")
        private ClimbingGrades climbingGrades;

        @SerializedName("created_at")
        private String createdAt;

        @SerializedName("updated_at")
        private String updatedAt;

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getGender() {
            return gender;
        }

        public void setGender(String gender) {
            this.gender = gender;
        }

        public double getWeight() {
            return weight;
        }

        public void setWeight(double weight) {
            this.weight = weight;
        }

        public int getHeight() {
            return height;
        }

        public void setHeight(int height) {
            this.height = height;
        }

        public ClimbingGrades getClimbingGrades() {
            return climbingGrades;
        }

        public void setClimbingGrades(ClimbingGrades climbingGrades) {
            this.climbingGrades = climbingGrades;
        }

        public String getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(String createdAt) {
            this.createdAt = createdAt;
        }

        public String getUpdatedAt() {
            return updatedAt;
        }

        public void setUpdatedAt(String updatedAt) {
            this.updatedAt = updatedAt;
        }
    }

    public static class ClimbingGrades {
        @SerializedName("redpoint")
        private String redpoint;

        @SerializedName("onsight")
        private String onsight;

        @SerializedName("boulder")
        private String boulder;

        public String getRedpoint() {
            return redpoint;
        }

        public void setRedpoint(String redpoint) {
            this.redpoint = redpoint;
        }

        public String getOnsight() {
            return onsight;
        }

        public void setOnsight(String onsight) {
            this.onsight = onsight;
        }

        public String getBoulder() {
            return boulder;
        }

        public void setBoulder(String boulder) {
            this.boulder = boulder;
        }
    }
}
