import { loadDoctorsList, loadPendingDoctorsList } from '../controller/admin-dashboard-controller.js';

displayDoctorList(); // display to render all doctors
displayPendingDoctorList();

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
      let listItem = document.createElement('li');
      listItem.classList.add('list-group-item', 'doctor-data-cell', 'd-flex', 'justify-content-between', 'align-items-center');
      listItem.setAttribute('data-doctor-id', doctor.id);

      // create text content
      let doctorInfo = document.createElement('span');
      doctorInfo.textContent = `ID: ${doctor.id} Name: ${doctor.completeName}`;

      // Create Delete Button
      let deleteButton = document.createElement('button');
      deleteButton.classList.add('btn', 'btn-danger', 'btn-sm', 'ms-2');
      deleteButton.textContent = 'Delete';
      deleteButton.setAttribute('data-doctor-id', doctor.id);
      deleteButton.onclick = () => deleteDoctor(doctor.id);

      // create div to contain delete button
      let buttonContainer = document.createElement('div');
      buttonContainer.appendChild(deleteButton);
      
      listItem.appendChild(doctorInfo);
      listItem.appendChild(buttonContainer);

      doctorList.appendChild(listItem);
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
      let listItem = document.createElement('li');
      listItem.classList.add('list-group-item', 'pending-doctor-data-cell', 'd-flex', 'justify-content-between', 'align-items-center');
      listItem.setAttribute('data-pending-doctor-id', pendingDoctor.id);
      
      // Create text content
      let doctorInfo = document.createElement('span');
      doctorInfo.textContent = `ID: ${pendingDoctor.id} Name: ${pendingDoctor.completeName}`;

      // Create Accept Button
      let acceptButton = document.createElement('button');
      acceptButton.classList.add('btn', 'btn-primary', 'btn-sm');
      acceptButton.textContent = 'Accept';
      acceptButton.setAttribute('data-pending-doctor-id', pendingDoctor.id);
      acceptButton.onclick = () => acceptPendingDoctor(pendingDoctor.id);

      // Create Delete Button
      let deleteButton = document.createElement('button');
      deleteButton.classList.add('btn', 'btn-danger', 'btn-sm', 'ms-2');
      deleteButton.textContent = 'Delete';
      deleteButton.setAttribute('data-pending-doctor-id', pendingDoctor.id);
      deleteButton.onclick = () => deletePendingDoctor(pendingDoctor.id);

      // Create a button container
      let buttonContainer = document.createElement('div');
      buttonContainer.appendChild(acceptButton);
      buttonContainer.appendChild(deleteButton);

      // Append elements to list item
      listItem.appendChild(doctorInfo);
      listItem.appendChild(buttonContainer);

      // Append list item to the container
      pendingDoctorListContainer.appendChild(listItem);
    });
  } 
  catch (error) {
    console.error('Error displaying pending doctors list:', error);
    document.querySelector('#pending-doctors-list-collapse').innerHTML = 'Failed to load pending doctors list.';
  }
};
