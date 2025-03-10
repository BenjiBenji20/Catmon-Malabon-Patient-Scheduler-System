import { loadDoctorsList, loadPendingDoctorsList, loadAdminList } from '../controller/admin-dashboard-controller.js';

displayAdminList() // display to render all admins
displayDoctorList(); // display to render all doctors
displayPendingDoctorList(); // display to render all pending doctors

/**
 * Render admin list
 */
async function displayAdminList() {
  try {
    const adminListData = await loadAdminList();

    // get admin list and ul element
    const adminListElement = document.querySelector('.co-admin-list ul');

    // validate data
    if(adminListData.error) {
      document.querySelector('.co-admin-list').innerHTML = adminListData.error;
      return;
    }

    adminListElement.innerHTML = ''; // clear existing content

    // loop through the data and append each element with object
    adminListData.forEach(admin => {
      adminListElement.innerHTML += `
        <li class="list-group-item" data-set-="admin-id" data-admin-id="${admin.id}">
          ID: ${admin.id} : ${admin.adminName}
        </li>
      `;
    });

  } catch (error) {
    console.error('Error displaying doctors list:', error);
    document.querySelector('#admin-list-collapse').innerHTML = 'Failed to load doctors list.';
  }
}

/**
 * Render doctor list
 */
async function displayDoctorList() {
  try {
    const doctorListData = await loadDoctorsList();

    // display doctor's list
    const doctorList = document.querySelector('.doctors-list ul');

    if(doctorListData.error) {
      document.querySelector('.doctors-list').innerHTML = doctorListData.error;
      return;
    }

    doctorList.innerHTML = ''; // clear the existing content

    // loop through the data and append each object
    doctorListData.forEach(doctor => {
      doctorList.innerHTML += `
        <li class="list-group-item doctor-data-cell d-flex justify-content-between align-items-center" data-doctor-id="${doctor.id}">
          <span>ID: ${doctor.id} : ${doctor.completeName}</span>
          <div>
            <button class="btn btn-danger btn-sm ms-2" data-doctor-id="${doctor.id}" onclick="deleteDoctor(${doctor.id})">Delete</button>
          </div>
        </li>
      `;
    });
  } 
  catch (error) {
    console.error('Error displaying doctors list:', error);
    document.querySelector('#doctors-list-collapse').innerHTML = 'Failed to load doctors list.';
  }
};

/**
 * Render pending doctor list
 */
async function displayPendingDoctorList() {
  try {
    const pendingDoctorListData = await loadPendingDoctorsList();

    // Get the UL container instead of a single LI element
    let pendingDoctorListContainer = document.querySelector('.pending-doctors-list ul');

    if (pendingDoctorListData.error) {
      document.querySelector('.pending-doctors-list').innerHTML = pendingDoctorListData.error;
      return;
    }

    pendingDoctorListContainer.innerHTML = ''; // Clear the existing list

    // Loop through the data and append each object as a new <li>
    pendingDoctorListData.forEach(pendingDoctor => {
      pendingDoctorListContainer.innerHTML += `
        <li class="list-group-item pending-doctor-data-cell d-flex justify-content-between align-items-center" data-pending-doctor-id="${pendingDoctor.id}">
          <span>ID: ${pendingDoctor.id} : ${pendingDoctor.completeName}</span>
          <div>
            <button class="btn btn-primary btn-sm" onclick="acceptPendingDoctor(${pendingDoctor.id})">Accept</button>
            <button class="btn btn-danger btn-sm ms-2" onclick="deletePendingDoctor(${pendingDoctor.id})">Delete</button>
          </div>
        </li>
      `;
    });
  } 
  catch (error) {
    console.error('Error displaying pending doctors list:', error);
    document.querySelector('#pending-doctors-list-collapse').innerHTML = 'Failed to load pending doctors list.';
  }
};
