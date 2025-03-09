import { AdminServiceAPI } from "../service/admin-service.js";

/**
 * Login form submit button event
 */
  // add event to track the login form fields
  const loginForm = document.getElementById("login-form");
  loginForm.addEventListener('submit', async(e) => {
  e.preventDefault(); // prevent login without inputing all fields

  // collects all admin inputs
  const adminCredentials = {
    email: document.getElementById("email").value,
    password: document.getElementById("password").value
  };

  try {
    const response = await AdminServiceAPI.authenticateAdmin(adminCredentials);

    // if authentication failed, stay on the same page and sends a response
    if(response.error) {
      document.querySelector('.message').innerHTML = response.error;
      return;
    }
    
    window.location.href = 'admin-dashboard.html';
  } 
  catch (error) {
    console.error('Error login the admin', error);
    throw error;
  }
});