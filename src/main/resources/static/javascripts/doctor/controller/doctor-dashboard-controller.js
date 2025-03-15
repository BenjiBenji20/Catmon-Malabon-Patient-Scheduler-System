import { DoctorServiceAPI } from '../service/doctor-service.js';
import { dataValidator } from '../../validation/data-validator.js';

export async function loadDoctorProfile() {
  try {
    // get data from service
    const data = await DoctorServiceAPI.getDoctorProfile();

    // validata
    dataValidator(data, '#doctor-name-js');

    return data;
  } catch (error) {
    console.error('Error fetching data', error);
    throw error;
  }
}
