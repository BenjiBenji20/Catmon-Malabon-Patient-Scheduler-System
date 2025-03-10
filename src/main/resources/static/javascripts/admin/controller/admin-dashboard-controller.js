import { AdminServiceAPI } from "../service/admin-service.js";

loadDoctorsList();

export async function loadDoctorsList() {
  try {
    // get the loaded data from service
    const data = await AdminServiceAPI.getAllDoctors();
    
    // if error happens
    if(data.error) {
      document.querySelector('.doctors-list-collapse').innerHTML = data.error;
      return;
    }

    return data;
  } 
  catch (error) {
    console.error('Error fetching data', error);
    throw error;
  }
}