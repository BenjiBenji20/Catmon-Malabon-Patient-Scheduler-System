package com.azathoth.CatmonMalabonHealthCenter.model.utils;

import com.azathoth.CatmonMalabonHealthCenter.model.AvailableDay;

public class UpdateDoctor {
    private String existingEmail;
    private String newCompleteName;
    private String newPassword;
    private AvailableDay[] newAvailableDays;

    public UpdateDoctor(String existingEmail, String newCompleteName, String newPassword, AvailableDay[] newAvailableDays) {
        this.existingEmail = existingEmail;
        this.newCompleteName = newCompleteName;
        this.newPassword = newPassword;
        this.newAvailableDays = newAvailableDays;
    }

    public String getExistingEmail() {
        return existingEmail;
    }

    public void setExistingEmail(String existingEmailEmail) {
        this.existingEmail = existingEmail;
    }

    public String getNewCompleteName() {
        return newCompleteName;
    }

    public void setNewCompleteName(String newCompleteName) {
        this.newCompleteName = newCompleteName;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public AvailableDay[] getNewAvailableDays() {
        return newAvailableDays;
    }

    public void setNewAvailableDays(AvailableDay[] newAvailableDays) {
        this.newAvailableDays = newAvailableDays;
    }
}
