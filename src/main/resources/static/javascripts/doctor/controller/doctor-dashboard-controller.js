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

export async function setPatientRecord(patientId, record) {
  try {
    // pass inputs to the service method param
    const postInputs = await DoctorServiceAPI.setPatientRecord(patientId, record);

    // validate
    if(postInputs.error) {
      console.error("Error sending data:", response.error);
      return { error: response.error };
    }

    return postInputs;
  } catch (error) {
    console.error('Error sending and fetching data', error);
    throw error;
  }
}

export async function loadAllMyPatients() {
  try {
    // pass inputs to the service method param
    const data = await DoctorServiceAPI.getAllMyPatients();

    // validate
    if(data.error) {
      document.querySelector('.message-js').innerHTML = data.error;
      return;
    }

    return data;
  } catch (error) {
    console.error('Error fetching data', error);
    throw error;
  }
}

export async function loadPatientDetails(id) {
  try {
    const data = await DoctorServiceAPI.getPatientDetails(id);

    // validate
    if(data.error) {
      document.querySelector('.message-js').innerHTML = data.error;
      return;
    }
    console.log("Controller load details: ", data);
    
    return data;
  } catch (error) {
    console.error('Error fetching data', error);
    throw error;
  }
}

export async function loadUpdatedPatientStatus(id, status) {
  try {
    console.log(`Updating status for patient ${id} to ${status}`);
    const response = await DoctorServiceAPI.updatePatientStatus(id, status);

    // validate
    if(response.error) {
      document.querySelector('.message-js').innerHTML = response.error;
      return;
    }

    return response;
  } catch (error) {
    console.error('Error updating record', error);
    throw error;
  }
}

export async function loadFilterPatient(gender, age, status) {
  try {
    // get the loaded data from service
    const data = await DoctorServiceAPI.filterPatient(gender, age, status);

    // if error happens
    if(data.error) {
      const tableBody = document.querySelector('tbody td');
      tableBody.classList.add('display-error-message');

      tableBody.innerHTML = data.error;
      return;
    }

    return data;
  } catch (error) {
    console.error('Error fetching data', error);
    throw error;
  }
}

export async function loadSearchPatients(keyword) {
  try {
    // get the loaded data from service
    const data = await DoctorServiceAPI.searchPatient(keyword);

    // if error happens
    if(data.error) {
      const tableBody = document.querySelector('tbody td');
      tableBody.classList.add('display-error-message');

      tableBody.innerHTML = data.error;
      return;
    }

    return data;
  } catch (error) {
    console.error('Error fetching data', error);
    throw error;
  }
}