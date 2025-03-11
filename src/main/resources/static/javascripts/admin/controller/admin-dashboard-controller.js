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