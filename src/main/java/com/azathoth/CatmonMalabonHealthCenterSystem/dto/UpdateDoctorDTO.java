package com.azathoth.CatmonMalabonHealthCenterSystem.dto;

import com.azathoth.CatmonMalabonHealthCenterSystem.utils.AvailableDay;

public class UpdateDoctorDTO {
    private String newCompleteName;
    private String newPassword;
    private AvailableDay[] newAvailableDays;

    public UpdateDoctorDTO(String newCompleteName, String newPassword, AvailableDay[] newAvailableDays) {
        this.newCompleteName = newCompleteName;
        this.newPassword = newPassword;
        this.newAvailableDays = newAvailableDays;
    }

    public UpdateDoctorDTO() {
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
