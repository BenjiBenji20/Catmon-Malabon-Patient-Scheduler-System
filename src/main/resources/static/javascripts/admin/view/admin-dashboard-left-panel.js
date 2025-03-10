import { loadDoctorsList } from '../controller/admin-dashboard-controller.js';

(async function displayDoctorList() {
  try {
    const doctorListData = await loadDoctorsList();


    // display doctor's list
    const doctorList = document.querySelector('.doctors-list');

    if(doctorListData.error) {
      document.querySelector('.doctors-list-collapse').innerHTML = doctorListData.error;
      return;
    }
    
    doctorList.innerHTML = ''; // clear the existing content

    // loop through the data and append each object
    doctorListData.forEach(doctor => {
      doctorList.innerHTML += `
      <a data-bs-toggle="collapse" class="doctors-list-collapse" href="#doctors-list-collapse" role="button" aria-expanded="false" aria-controls="doctors-list-collapse">Available Doctors
      </a>
                <div class="row">
                  <div class="col">
                    <div class="collapse multi-collapse" id="doctors-list-collapse">
                      <div class="card card-body">
                        <ul class="list-group list-group-flush">
                          <li class="list-group-item doctor-data-cell" data-doctor-id="${doctor.id}">
                            ID: ${doctor.id} Name: ${doctor.completeName}
                          </li>
                        </ul>
                      </div>
                    </div>
                  </div>
                </div>
      `;
    });
  } 
  catch (error) {
    console.error('Error displaying doctors list:', error);
    document.querySelector('.doctors-list-collapse').innerHTML = 'Failed to load doctors list.';
  }
});




