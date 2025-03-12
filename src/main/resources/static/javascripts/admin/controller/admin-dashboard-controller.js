import { AdminServiceAPI } from "../service/admin-service.js";

export async function loadAdminProfile() {
  try {
    // get the loaded data from service
    const data = await AdminServiceAPI.getAdminProfile();

    // if error happens
    if(data.error) {
      document.querySelector('#admin-name-js').innerHTML = data.error;
      return;
    }

    return data;
  } catch (error) {
    console.error('Error fetching data', error);
    throw error;
  }
}

export async function loadAdminList() {
  try {
    // get the loaded data from service
    const data = await AdminServiceAPI.getAllAdmins();

    // if error happens
    if(data.error) {
      document.querySelector('#admin-list-collapse').innerHTML = data.error;
      return;
    }

    return data;
  } catch (error) {
    console.error('Error fetching data', error);
    throw error;
  }
}

export async function loadDoctorsList() {
  try {
    // get the loaded data from service
    const data = await AdminServiceAPI.getAllDoctors();
    
    // if error happens
    if(data.error) {
      document.querySelector('#doctors-list-collapse').innerHTML = data.error;
      return;
    }

    return data;
  } 
  catch (error) {
    console.error('Error fetching data', error);
    throw error;
  }
}

export async function loadPendingDoctorsList() {
  try {
    // get the loaded data from service
    const data = await AdminServiceAPI.getAllPendingDoctors();
    
    // if error happens
    if(data.error) {
      document.querySelector('#pending-doctors-list-collapse').innerHTML = data.error;
      return;
    }

    return data;
  } 
  catch (error) {
    console.error('Error fetching data', error);
    throw error;
  }
}

export async function acceptPendingDoctor(id) { 
  try {
    // pass id to the service
    const response = await AdminServiceAPI.acceptPendingDoctor(id);

    return response;
  } catch (error) {
    console.error('Error fetching data', error);
    throw error;
  }
}

export async function deletePendingDoctor(id) { 
  try {
    // pass id to the service
    const response = await AdminServiceAPI.deletePendingDoctor(id);

    return response;
  } catch (error) {
    console.error('Error fetching data', error);
    throw error;
  }
}

export async function loadPatientsList() {
  try {
    // get the loaded data from service
    const data = await AdminServiceAPI.getAllPatients();

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

export async function loadAppointmentsList() {
  try {
    // get the loaded data from service
    const data = await AdminServiceAPI.getAllAppointments();

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

export async function loadFilterPatient(gender, age, status) {
  try {
    // get the loaded data from service
    const data = await AdminServiceAPI.filterPatient(gender, age, status);

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
    const data = await AdminServiceAPI.searchPatient(keyword);

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