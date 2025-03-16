import { setPatientRecord, loadAllMyPatients, loadPatientDetails,
  loadUpdatedPatientStatus, loadFilterPatient, loadSearchPatients
 } from "../controller/doctor-dashboard-controller.js";

displayPatientTable();
displayFilteredPatients();

document.addEventListener('DOMContentLoaded', async () => {
  const modal = new bootstrap.Modal(document.getElementById('patientModal'));
  let currentPatientId = null;

  // Event listener for opening the modal (using event delegation)
  document.body.addEventListener('click', async (e) => {
    const patientLink = e.target.closest('a[data-patient-id]');
    if (patientLink) {
      e.preventDefault();
      currentPatientId = patientLink.getAttribute('data-patient-id');

      // Fetch patient details
      const patientDetails = await loadPatientDetails(currentPatientId);
      console.log('Patient details:', patientDetails);

      if (patientDetails.error) {
        alert(patientDetails.error);
        return;
      }

      // Populate modal with patient details
      document.getElementById('modalPatientName').textContent = patientDetails.completeName;
      document.getElementById('modalPatientAge').textContent = `Age: ${patientDetails.age}`;
      document.getElementById('modalPatientGender').textContent = `Gender: ${patientDetails.gender}`;
      document.getElementById('modalPatientStatus').textContent = `Status: ${patientDetails.status}`;

      // Show the modal
      modal.show();
    }
  });

  // Event listener for saving the record
  document.getElementById('savePatientRecord').addEventListener('click', async () => {
    const diagnosis = document.getElementById('setDiagnosis').value.trim();
    const prescription = document.getElementById('setPrescription').value.trim();
    const attended = document.getElementById('isAttended').value === 'true';
    const status = document.getElementById('setRecordStatus').value.trim();

    const record = {
      attended,
      prescription,
      diagnosis
    };

    // Save the record
    const result = await setPatientRecord(currentPatientId, record);
    console.log('Setting patient record result:', result);

    // save status individually
    const statusResult = await loadUpdatedPatientStatus(currentPatientId, status);
    console.log(statusResult);
    

    if (result.error) {
      alert(result.error);
    } else {
      alert('Record saved successfully!');
      modal.hide();

      // Reset input fields
      document.getElementById('setDiagnosis').value = '';
      document.getElementById('setPrescription').value = '';
      document.getElementById('isAttended').value === 'true';
      document.getElementById('setRecordStatus').value = '';
    }
  });
});

async function displayPatientTable() {
  const myPatientData = await loadAllMyPatients();

  try {
    if(myPatientData.error) {
      const tableBody = document.querySelector('tbody');
      tableBody.classList.add('display-error-message');

      tableBody.innerHTML = myPatientData.error;
      return;
    }

    // call function to render filtered patient data
    renderPatientTable(myPatientData);
  } catch (error) {
    console.error('Error displaying patients list:', error);
    const tableBody = document.querySelector('tbody');
    tableBody.classList.add('display-error-message');

    tableBody.innerHTML = 'Cannot fetch patient records';
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