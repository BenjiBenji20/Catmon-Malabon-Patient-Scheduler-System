import { adminAuthenticationView } from "../view/auth-view.js";
import { AdminServiceAPI } from "../service/admin-service.js";

/**
 * Login form submit button event
 */
export function authenticationFormController() {
  document.getElementById("form-login").addEventListener('submit', async(e) => {
    e.preventDefault(); // prevent login without inputing all fields

    // collects all admin inputs
    const adminCredentials = {
      email: document.getElementById("email").value,
      password: document.getElementById("password").value
    };

    try {
      const result = await AdminServiceAPI.authAdminService(adminCredentials);

      console.log('In controller: ', result);
    } 
    catch (error) {
      console.error('Error login the admin', error);
      throw error;
    }
  });
}

const containerElement = document.querySelector('.container');
containerElement.innerHTML = adminAuthenticationView();
authenticationFormController(); // Call after rendering