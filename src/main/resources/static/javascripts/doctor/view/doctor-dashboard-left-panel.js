import { loadDoctorProfile } from "../controller/doctor-dashboard-controller.js"; 
import { dataValidator } from '../../validation/data-validator.js';

displayDoctorProfile();

async function displayDoctorProfile() {
  const data = await loadDoctorProfile();

  try {
    // validata data
    dataValidator(data, '#doctor-name-js');

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
