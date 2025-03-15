import { loadDoctorProfile, loadPatientsToday, loadPatientsByDay } from "../controller/doctor-dashboard-controller.js"; 

displayDoctorProfile();
displayPatientsToday();
displayPatientsByDay();

async function displayDoctorProfile() {
  const data = await loadDoctorProfile();

  try {
    // validata data
    if(data.error) {
      document.querySelector('#doctor-name-js').innerHTML = data.error;
      return;
    }

    const profileContainer = document.querySelector('.profile-container');

    profileContainer.innerHTML = `
      <div class="profile-container">
        <div class="offcanvas-header doctor-profile-container" style="padding: 0;">
          <i class="bi bi-person-circle"></i>
          <div id="doctor-name-js">
            ${data.completeName}
          </div>
          <div class="doctor-badge">
              doctor
          </div>
        </div>
        <i class="bi bi-escape" id="logout-button-js" title="logout"></i>
      </div>
    `;

    /**
     * logout button function
     */
    document.getElementById('logout-button-js').addEventListener('click', () => {
      localStorage.removeItem('doctorJwt'); // remove itm form localstorage

      window.location.href = 'http://127.0.0.1:5500/src/main/resources/templates/doctor/doctor-auth.html';
    });
    
  } catch (error) {
    console.error('Error fetching data', error);
    document.querySelector('#doctor-name-js').innerHTML = data.error;
  }
}

async function displayPatientsToday() {
  const patientsToday = await loadPatientsToday();
  const listContainer = document.querySelector('.patients-today-collapse');
  
  try {
    if(!patientsToday || patientsToday.error || patientsToday.length === 0) {
      listContainer.innerHTML = patientsToday?.error || "No patients available today.";
      return;
    }

    const cardBody = document.querySelector('.card-body');
    cardBody.innerHTML = ''; // clear existing content

    Object.keys(patientsToday).forEach(patient => {
      cardBody.innerHTML += `
          <ul class="list-group list-group-flush" data-patient-id="${patient.id}">
            ${patient.completeName} 
            <span class="patient-status" data-patient-status="${patient.status}">
              ${patient.status.toLowerCase()}
            </span>
          </ul>
      `;
    });

    // get the current date today
    const currentDate = new Date().toISOString().split('T')[0];
    const patientsTodayContainer = document.querySelector('.patients-today');
    patientsTodayContainer.innerHTML = `
      <div class="patients-today">
        <div data-bs-toggle="collapse" class="patients-today-collapse collapse-link" href="#patients-today-list-collapse" role="button" aria-expanded="false" aria-controls="patients-today-list-collapse">
          Patients today ${currentDate}
          <i class="bi bi-caret-down"></i>
        </div>
        <div class="row">
          <div class="col">
            <div class="collapse multi-collapse" id="patients-today-list-collapse">
              ${cardBody.innerHTML} 
            </div>
          </div>
        </div>
      </div> 
    `
  } catch (error) {
    console.error('Error fetching data', error);
    listContainer.innerHTML = 'No patients available today.';
  }
}

async function displayPatientsByDay() {
  const patientsByDayData = await loadPatientsByDay();
  const container = document.querySelector('.patients-by-day-js');
  try {
    if(!patientsByDayData || patientsByDayData.error) {
      container.innerHTML = patientsByDayData?.error || "No patients available today.";
      return;
    }

    container.innerHTML = ''; // clear existing content

    // loop through the object and render along with html element
    Object.keys(patientsByDayData).forEach(day => {
      if (patientsByDayData[day] && patientsByDayData[day].length > 0) {
        
        // Generate HTML for each day's section
        container.innerHTML += `
          <div class="day-list-collapse collapse-link" href="#${day}-list-collapse" data-bs-toggle="collapse" role="button" aria-expanded="false" aria-controls="${day}-list-collapse">
            ${patientsByDayData[day].length} Patients in <span class="day">${day}</span> 
            <i class="bi bi-caret-down"></i>
          </div>
          <div class="row">
            <div class="col">
              <div class="collapse multi-collapse" id="${day}-list-collapse">
                <div class="card card-body">
                  <ul class="list-group list-group-flush" id="${day}-patients-list">
                    <!-- Patients will be inserted here dynamically -->
                  </ul>
                </div>
              </div>
            </div>
          </div>
        `;
    
        // Select the list where patients should be appended
        const listContainer = document.getElementById(`${day}-patients-list`);
    
        // Loop through the patients array and append each patient
        patientsByDayData[day].forEach(patient => {
          listContainer.innerHTML += `
            <li class="list-group-item patient-data-cell" data-patient-id="${patient.id}">
              ${patient.completeName}
            </li>
          `;
        });
      }
    });
    
  } catch (error) {
    console.error('Error fetching data', error);
  }
}