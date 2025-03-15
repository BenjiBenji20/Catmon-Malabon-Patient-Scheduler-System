import { DoctorServiceAPI } from '../service/doctor-service.js';

export async function loadDoctorProfile() {
  try {
    // get data from service
    const data = await DoctorServiceAPI.getDoctorProfile();

    // validata
    if(data.error) {
      document.querySelector('#doctor-name-js').innerHTML = data.error;
      return;
    }

    return data;
  } catch (error) {
    console.error('Error fetching data', error);
    throw error;
  }
}
