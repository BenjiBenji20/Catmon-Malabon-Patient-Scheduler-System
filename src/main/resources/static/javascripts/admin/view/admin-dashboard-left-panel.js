import { loadDoctorsList, loadPendingDoctorsList, loadAdminList,
  loadAdminProfile, acceptPendingDoctor, deletePendingDoctor,
  loadPatientsList, loadAppointmentsList, loadFilterPatient,
  loadSearchPatients
 } from '../controller/admin-dashboard-controller.js';

displayAdminProfile(); // display to render admin profile
displayAdminList(); // display to render all admins
displayDoctorList(); // display to render all doctors
displayPendingDoctorList(); // display to render all pending doctors
displayPatientTable(); // display to render all patients data
displayAppointmentTable(); // display to render all appoitnmetns data
displayFilteredPatients();

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
                ${adminData.adminName} 
                <i class="bi bi-escape" id="logout-button-js" title="logout"></i>
                <br>
                ${adminData.email}
              </div>
            </div>
            
          </div>
      `;

      // logout admin and remove token from local storage
      document.getElementById('logout-button-js').addEventListener('click', () => {
        localStorage.removeItem('adminJwt'); // remove token from storage

        // redirect user
        window.location.href = 'http://127.0.0.1:5500/src/main/resources/templates/admin/admin-auth.html';
      });
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
      message.style.border = '2px solid #ef5350';
      message.style.backgroundColor = '#e57373';

      // remove the message after 2 seconds
      setTimeout(() => {
        message.innerHTML = '';
        message.style.border = '2px solid #ffff';
      }, 2000);

      return;
    }

    // display sucess message
    message.innerHTML = result.message;
    message.style.border = '2px solid #2d912d';
    message.style.backgroundColor = '#3dc13c';
    // remove the message after 2 seconds
    setTimeout(() => {
      message.innerHTML = '';
      message.style.border = '2px solid #ffff';
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
  try {
    // extract data from controller
    const patientListData = await loadPatientsList();

    if(patientListData.error) {
      const tableBody = document.querySelector('tbody');
      tableBody.classList.add('display-error-message');

      tableBody.innerHTML = patientListData.error;
      return;
    }

    // call function to render filtered patient data
    renderPatientTable(patientListData);
  } catch (error) {
    console.error('Error displaying patients list:', error);
    const tableBody = document.querySelector('tbody');
    tableBody.classList.add('display-error-message');

    tableBody.innerHTML = 'Cannot fetch patient records';
  }
}

/**
 * Render all appointments fetched from api
 */
async function displayAppointmentTable() {
  try {
    // extract data from controller
    const appointmentListData = await loadAppointmentsList();

    // validate data
    if(appointmentListData.error) {
      const tableBody = document.querySelector('tbody');
      tableBody.classList.add('display-error-message');

      tableBody.innerHTML = appointmentListData.error;
      return;
    }

    // call function to render appointment table
    renderAppointmentTable(appointmentListData);
  } catch (error) {
    console.error('Error displaying patients list:', error);
    const tableBody = document.querySelector('tbody td');
      tableBody.classList.add('display-error-message');

      tableBody.innerHTML = 'Cannot fetch appointment records';
  }
}

/**
 * collect and pass filter inputs,
 * fetch data and render to the table
 */
async function displayFilteredPatients() {
  const filterForm = document.getElementById('patient-profile-table-filter-form-js');
  // add event submit listener to the filter form
  filterForm.addEventListener('submit', async(e) => {
    e.preventDefault(); // prevent login without inputing all fields

    // collect form inputs
    const gender = document.getElementById('patient-filter-gender').value;
    const age = document.getElementById('patient-filter-age').value;
    const status = document.getElementById('patient-filter-status').value;

    try {
      // pass inputs and fetch filter result
      const patientFilterData = await loadFilterPatient(gender, age, status);

      // if error  response
      if(patientFilterData.error) {
        const tableBody = document.querySelector('tbody td');
        tableBody.classList.add('display-error-message');

        tableBody.innerHTML = patientFilterData.error;
        return;
      }

      // call function to render filtered patient data
      renderPatientTable(patientFilterData);
    } catch (error) {
      const tableBody = document.querySelector('tbody');
      tableBody.classList.add('display-error-message');

      tableBody.innerHTML = 'Cannot fetch patient records';
    }
  });
}

/**
 * Functions and events for rendering tables
 */
function renderPatientTable(patientData) {
  // get table and table body to render filtered patient data
  const tableDataElement = document.querySelector('.patient-profile-table tbody');
  // clear existing content
  tableDataElement.innerHTML = '';

  // loop through the data and append each tab;e data (td) to the table
  patientData.forEach(patient => {
  // create table row element to handle each table data
  const row = document.createElement('tr');
  row.setAttribute('data-id', patient.id);

  row.innerHTML = `
    <td>${patient.id}</td>
    <td>${patient.completeName}</td>
    <td>${patient.gender}</td>
    <td>${patient.age}</td>
    <td>+63 ${patient.contactNumber}</td>
    <td>${patient.verificationNumber}</td>
    <td>
      <div class="status-badge" data-status="${patient.status}">
      ${patient.status}
      </div>
    </td>
    <td>
      <input type="checkbox" data-patient-id="${patient.id}">
    </td>
  `;

    tableDataElement.appendChild(row);
  });

  // design the status based on its value
  colorStatus();

  return tableDataElement;
}

function renderAppointmentTable(appointmentData) {
   // get table and table body to render patient data
   const tableDataElement = document.querySelector('.appointment-table tbody');

   // clear existing content
   tableDataElement.innerHTML = '';

   // loop through the data and append each tab;e data (td) to the table
   appointmentData.forEach(appointment => {
     // create table row element to handle each table data
     const row = document.createElement('tr');
     row.setAttribute('data-id', appointment.id);

     row.innerHTML = `
       <td>${appointment.id}</td>
       <td>${appointment.scheduleDate}</td>
       <td>${appointment.patientId}</td>
       <td>${appointment.patientName}</td>
       <td>${appointment.doctorId}</td>
       <td>${appointment.doctorName}</td>
       <td>
        <div class="status-badge" data-status="${appointment.status}">
          ${appointment.status}
        </div>
       </td>
       <td>
         <input type="checkbox" data-appointment-id="${appointment.id}">
       </td>
     `;

     tableDataElement.appendChild(row);
   });
   // design the status based on its value
  colorStatus();

   return tableDataElement;
}

function colorStatus() {
  document.querySelectorAll('.status-badge').forEach(badge => {  
    const status = badge.getAttribute('data-status'); // get data attribute
  
    switch (status.toUpperCase()) {
      case 'PENDING':
        badge.style.backgroundColor = '#ebbe41';
        badge.style.color = '#ffff';
        badge.style.fontWeight = '500';
        badge.style.border = '2px solid #917826';
        break;
      
      case 'CANCELLED':
        badge.style.backgroundColor = '#bd3831';
        badge.style.color = '#ffff';
        badge.style.fontWeight = '500';
        badge.style.border = '2px solid #664344';
        break;
      
      case 'ONGOING':
        badge.style.backgroundColor = '#5a8ed6';
        badge.style.color = '#ffff';
        badge.style.fontWeight = '500';
        badge.style.border = '2px solid #3a5b8a';
        break;
  
      case 'DONE':
        badge.style.backgroundColor = '#59b56b';
        badge.style.color = '#ffff';
        badge.style.fontWeight = '500';
        badge.style.border = '2px solid #416a4a';
        break;
    
      default:
        badge.style.backgroundColor = '#ffff';
        badge.style.color = 'black';
        badge.style.border = '2px solid black';
        break;
    }
  });
}

function displayErrorMessage(message) {
  const tableBody = document.querySelector('tbody');
  tableBody.classList.add('display-error-message');
  tableBody.innerHTML = message;
}

// event to reload patient table
document.getElementById('reload-patient-table-js').addEventListener('click', () => {
    // call function to display table again after reload
    displayPatientTable();
});

// event to reload appointment table
document.getElementById('reload-appointment-table-js').addEventListener('click', () => {
  // call function to display table again after reload
  displayAppointmentTable();
});

const searchBar = document.getElementById('search-input-js');
searchBar.addEventListener('input', debounce(async (e) => {
  const keyword = e.target.value.trim(); // get search bar inputs

  if(keyword.length === 0) {
    // render table to its default content
    displayPatientTable();
    return;
  }

  try {
    // pass and fetch the data
    const searchData = await loadSearchPatients(keyword);

    // validate response
    if(searchData.error) {
      displayErrorMessage(searchData.error);
      return;
    }

    // call function to render searched patient via keyword
    renderPatientTable(searchData);
  } catch (error) {
    console.error('Error fetching patient records:', error);
    displayErrorMessage('Cannot fetch patient records');
  }
}, 300)); // 300ms delay to prevent execessive api calls

// function that prevents execessive api calls
function debounce(func, delay) {
  let timer;

  return(...args) => {
    clearTimeout(timer);
    timer = setTimeout(() => func(...args), delay);
  };
}