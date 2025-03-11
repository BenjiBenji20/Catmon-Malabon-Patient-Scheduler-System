import { loadDoctorsList, loadPendingDoctorsList, loadAdminList,
  loadAdminProfile, acceptPendingDoctor, deletePendingDoctor,
  loadPatientsList, loadAppointmentsList
 } from '../controller/admin-dashboard-controller.js';

displayAdminProfile(); // display to render admin profile
displayAdminList(); // display to render all admins
displayDoctorList(); // display to render all doctors
displayPendingDoctorList(); // display to render all pending doctors
displayPatientTable(); // display to render all patients data
displayAppointmentTable(); // display to render all appoitnmetns data

/**
 * Render admin profile
 * admin name, admin email
 */
async function displayAdminProfile() {
  try {
    const adminData = await loadAdminProfile();

    // get admin profile element
    const adminProfileElement = document.querySelector('.admin-name-js');

    // validate data
    if(adminData.error) {
      adminProfileElement.innerHTML = adminData.error;
      return;
    }

    // get the profile container
    const profileContainer = document.querySelector('.profile-container');

    // render data and append to html element using .innerHTML
    profileContainer.innerHTML = `
        <div class="profile-container">
            <div class="offcanvas-header admin-profile-container">
              <i class="bi bi-person-circle"></i>
              <div id="admin-name-js">
                Name: ${adminData.adminName} <br>
                Email: ${adminData.email}
              </div>
            </div>
            
          </div>
      `;
  } catch (error) {
    console.error('Error displaying doctors list:', error);
    document.querySelector('#admin-name-js').innerHTML = 'Failed to load admin info';
    document.querySelector('#admin-email-js').innerHTML = 'Failed to load admin info';
  }
}

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
            <button class="btn btn-warning btn-sm ms-2" data-doctor-id="${doctor.id}" onclick="deleteDoctor(${doctor.id})">Delete</button>
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
      const listItem = document.createElement('li');
      listItem.className = 'list-group-item pending-doctor-data-cell d-flex justify-content-between align-items-center';
      listItem.dataset.pendingDoctorId = pendingDoctor.id;

      listItem.innerHTML = `
        <span>ID: ${pendingDoctor.id} : ${pendingDoctor.completeName}</span>
        <div>
          <button class="btn btn-primary btn-sm accept-pending-doctor-button">Accept</button>
          <button class="btn btn-danger btn-sm ms-2 delete-pending-doctor-button">Delete</button>
        </div>
      `;

      // create an event for the button
      const acceptButton = listItem.querySelector('.accept-pending-doctor-button');
      const deleteButton = listItem.querySelector('.delete-pending-doctor-button');


      // add an on click event
      acceptButton.addEventListener('click', () => {
        acceptPendingDoctorButton(pendingDoctor.id);
      });

      deleteButton.addEventListener('click', () => {
        deletePendingDoctorButton(pendingDoctor.id);
      });

      pendingDoctorListContainer.appendChild(listItem);
    });
  } 
  catch (error) {
    console.error('Error displaying pending doctors list:', error);
    document.querySelector('#pending-doctors-list-collapse').innerHTML = 'Failed to load pending doctors list.';
  }
};

/**
 * Accept pending doctor using its id. Extracted id using data attributes
 */
async function acceptPendingDoctorButton(id) {
  const message = document.querySelector('.message-js');
  try {
    // get the result
    const result = await acceptPendingDoctor(id);

    // if result has error
    if(result.error) {
      message.innerHTML = result.error;

      // remove the message after 2 seconds
      setTimeout(() => {
        message.innerHTML = '';
      }, 2000);

      return;
    }

    // display sucess message
    message.innerHTML = result.message;
    // remove the message after 2 seconds
    setTimeout(() => {
      message.innerHTML = '';
    }, 2000);

    // rerender the list again
    displayDoctorList();
    displayPendingDoctorList();
  } catch (error) {
    console.error('Error request for pending doctor:', error);
    message.innerHTML = 'Failed to grant request.';
  }
} 

/**
 * Accept pending doctor using its id. Extracted id using data attributes
 */
async function deletePendingDoctorButton(id) {
  const message = document.querySelector('.message-js');
  try {
    // get the result
    const result = await deletePendingDoctor(id);

    // if result has error
    if(result.error) {
      message.innerHTML = result.error;

      // remove the message after 2 seconds
      setTimeout(() => {
        message.innerHTML = '';
      }, 2000);

      return;
    }

    // display sucess message
    message.innerHTML = result.message;
    // remove the message after 2 seconds
    setTimeout(() => {
      message.innerHTML = '';
    }, 2000);

    // rerender the list again
    displayDoctorList();
    displayPendingDoctorList();
  } catch (error) {
    console.error('Error request for pending doctor:', error);
    message.innerHTML = 'Failed to grant request.';
  }
} 

/**
 * Render all patients fetched from the api
 */
async function displayPatientTable() {
  // extract data from controller
  const patientListData = await loadPatientsList();

  try {
    // get table and table body to render patient data
    const tableDataElement = document.querySelector('.patient-profile-table tbody');

    if(patientListData.error) {
      const tableBody = document.querySelector('tbody');
      tableBody.classList.add('display-error-message');

      tableBody.innerHTML = patientListData.error;
      return;
    }

    // clear existing content
    tableDataElement.innerHTML = '';

    // loop through the data and append each tab;e data (td) to the table
    patientListData.forEach(patient => {
      // create table row element to handle each table data
      const row = document.createElement('tr');

      row.innerHTML = `
        <td>${patient.id}</td>
        <td>${patient.completeName}</td>
        <td>${patient.gender}</td>
        <td>${patient.age}</td>
        <td>+63 ${patient.contactNumber}</td>
        <td>${patient.verificationNumber}</td>
        <td>${patient.status}</td>
        <td>
          <input type="checkbox" data-patient-id="${patient.id}">
        </td>
      `;

      tableDataElement.appendChild(row);
    });
  } catch (error) {
    console.error('Error displaying patients list:', error);
    const tableBody = document.querySelector('tbody');
      tableBody.classList.add('display-error-message');

      tableBody.innerHTML = patientListData.error;
  }
}

/**
 * Render all appointments fetched from api
 */
async function displayAppointmentTable() {
  // extract data from controller
  const appointmentListData = await loadAppointmentsList();

  try {
    // get table and table body to render patient data
    const tableDataElement = document.querySelector('.appointment-table tbody');

    // validate data
    if(appointmentListData.error) {
      const tableBody = document.querySelector('tbody');
      tableBody.classList.add('display-error-message');

      tableBody.innerHTML = appointmentListData.error;
      return;
    }

    // clear existing content
    tableDataElement.innerHTML = '';

    // loop through the data and append each tab;e data (td) to the table
    appointmentListData.forEach(appointment => {
      // create table row element to handle each table data
      const row = document.createElement('tr');

      row.innerHTML = `
        <td>${appointment.id}</td>
        <td>${appointment.scheduleDate}</td>
        <td>${appointment.patientId}</td>
        <td>${appointment.patientName}</td>
        <td>${appointment.doctorId}</td>
        <td>${appointment.doctorName}</td>
        <td>${appointment.status}</td>
        <td>
          <input type="checkbox" data-appointment-id="${appointment.id}">
        </td>
      `;

      tableDataElement.appendChild(row);
    });
  } catch (error) {
    console.error('Error displaying patients list:', error);
    const tableBody = document.querySelector('tbody');
      tableBody.classList.add('display-error-message');

      tableBody.innerHTML = appointmentListData.error;
  }
}
