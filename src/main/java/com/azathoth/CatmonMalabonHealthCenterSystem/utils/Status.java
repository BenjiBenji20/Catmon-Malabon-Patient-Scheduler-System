package com.azathoth.CatmonMalabonHealthCenterSystem.utils;

/**
 * This enum is use for appointment entity
 * This will mark the patient's status.
 * As the patient registered they will automatically become PENDING,
 * once their schedule has come, the status will automatically become ONGOING,
 * the doctor will change the patient's status based on scenario. Let's say
 * the patient didn't come to its selected schedule date, then the doctor
 * might mark it as CANCELLED, otherwise DONE if consultation happened.
 */
public enum Status {
    CANCELLED, PENDING, ONGOING, DONE
}