import { DoctorServiceAPI } from '../service/doctor-service.js';

/**
 * Login form submit button event
 */
  // add event to track the login form fields
  const loginForm = document.getElementById("login-form");
  loginForm.addEventListener('submit', async(e) => {
  e.preventDefault(); // prevent login without inputing all fields

  // collects all doctor inputs
  const doctorCredentials = {
    email: document.getElementById("email").value,
    password: document.getElementById("password").value
  };

  try {
    const response = await DoctorServiceAPI.authenticateDoctor(doctorCredentials);

    // if authentication failed, stay on the same page and sends a response
    if(response.error) {
      document.querySelector('.message').innerHTML = response.error;

      setTimeout(() => {
        document.querySelector('.message').innerHTML = '';
      }, 1500);
      return;
    }
    
    // if authentication is successful
    window.location.href = 'doctor-dashboard.html';
  } 
  catch (error) {
    console.error('Error login the doctor', error);
    throw error;
  }
});