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

export async function loadPatientsToday() {
  try {
    const data = await DoctorServiceAPI.getPatientsToday();

    if(data.error) {
      document.querySelector('.patients-today-collapse').innerHTML = data.error;
      return;
    }

    return data;
  } catch (error) {
    console.error('Error fetching data', error);
    throw error;
  }
}

export async function loadPatientsByDay() {
  try {
    // get data from service
    const data = await DoctorServiceAPI.getPatientsByDay();

    // validata
    if(data.error) {
      document.querySelector('.patients-by-day-js').innerHTML = data.error;
      return;
    }

    return data;
  } catch (error) {
    console.error('Error fetching data', error);
    throw error;
  }
}
