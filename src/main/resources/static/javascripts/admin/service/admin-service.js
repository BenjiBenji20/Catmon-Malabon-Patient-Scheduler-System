import { validateParsedToken } from "../validation/jwt-validation.js";

export class AdminServiceAPI {
  /**
   * User login fetch json from backend to post a req
   * this function fetch the jwt if user successfully login
   */
  static async authenticateAdmin(adminCredentials) {
    try {
      // fetch auth api to extract jwt
      const response = await fetch("http://localhost:8002/api/admin/public/auth", {
        method: "POST",
        headers: {"Content-Type": "application/json"},
        body: JSON.stringify(adminCredentials)
      });

      const data = await response.json(); // extract data response

      if(!response.ok) {
        // return error from the backend
        return { error: data.error || "Invalid email or password" };
      }

      localStorage.setItem("adminJwt", JSON.stringify(data)); // save the jwt to the local storage

      return data;
    } 
    catch (error) {
      console.error("Error authenticating the admin", error);
      return { error: "Something went wrong. Please try again." };
    }
  }

  // extract saved token
  static tokenJson = localStorage.getItem('adminJwt');
  static token = AdminServiceAPI.tokenJson ? JSON.parse(AdminServiceAPI.tokenJson) : null;

  /**
   * Fetch logged admin profile using the token
   */
  static async getAdminProfile() {
    try {
      // validate parsed token. If token is null, redirect to login
      validateParsedToken(this.token);

      // use the token to fetch the admin profile using the backend's api
      const response = await fetch("http://localhost:8002/api/admin/private/get-admin-profile", {
        method: 'GET',
        headers: {
          'Authorization' : `Bearer ${this.token}`,
          'Content-Type' : 'application/json'
        },
      });

      if(!response.ok) {
        const errorData = await response.json();
        return { error: errorData.error || 'Cannot fetch data' };
      }

      // if response isn't null, send the response to the controller
      return response.json(); 

    } catch (error) {
      console.error("Error fetching data", error);
      return { error: "Cannot fetch data." };
    }
  }

  /**
   * Fetch all admins from db using the token
   */
  static async getAllAdmins() {
    try {
      // validate parsed token. If token is null, redirect to login
      validateParsedToken(this.token);

      // use the token to fetch all the admin using the backend's api
      const response = await fetch("http://localhost:8002/api/admin/private/get-all-co-admins", {
        method: 'GET',
        headers: {
          'Authorization' : `Bearer ${this.token}`,
          'Content-Type' : 'application/json'
        },
      });

      if(!response.ok) {
        const errorData = await response.json();
        return { error: errorData.error || 'Cannot fetch data' };
      }

      // if response isn't null, send the response to the controller
      return response.json(); 
    } catch (error) {
      console.error("Error fetching data", error);
      return { error: "Something went wrong. Please try again." };
    }
  }

  /**
   * Fetch all doctors from db using the token
   */
  static async getAllDoctors() {
    try {
      // validate parsed token. If token is null, redirect to login
      validateParsedToken(this.token);

      // use the token to fetch all the doctors using the backend's api
      const response = await fetch("http://localhost:8002/api/admin/private/get-all-doctors", {
        method: 'GET',
        headers: {
          'Authorization' : `Bearer ${this.token}`,
          'Content-Type' : 'application/json'
        },
      });

      if(!response.ok) {
        const errorData = await response.json();
        return { error: errorData.error || 'Cannot fetch data' };
      }

      // if response isn't null, send the response to the controller
      return response.json(); 
    } 
    catch (error) {
      console.error("Error fetching data", error);
      return { error: "Something went wrong. Please try again." };
    }
  }

  /**
   * Fetch all pending doctors from db using the token
   */
  static async getAllPendingDoctors() {
    try {
      // validate parsed token. If token is null, redirect to login
      validateParsedToken(this.token);

      // use the token to fetch all the doctors using the backend's api
      const response = await fetch("http://localhost:8002/api/admin/private/get-all-pending-doctors", {
        method: 'GET',
        headers: {
          'Authorization' : `Bearer ${this.token}`,
          'Content-Type' : 'application/json'
        },
      });

      if(!response.ok) {
        const errorData = await response.json();
        return { error: errorData.error || 'Cannot fetch data' };
      }

      // if response isn't null, send the response to the controller
      return response.json(); 
    } 
    catch (error) {
      console.error("Error fetching data", error);
      return { error: "Something went wrong. Please try again." };
    }
  }

  /**
   * Accept doctor's pending request
   */
  static async acceptPendingDoctor(id) {
    try {
      // validate parsed token. If token is null, redirect to login
      validateParsedToken(this.token);

      // pass a post request using the backend's api
      const response = await fetch(`http://localhost:8002/api/admin/private/accept-doctor-request/${id}`, {
        method: 'POST',
        headers: {
          'Authorization' : `Bearer ${this.token}`,
          'Content-Type' : 'application/json'
        },
      });

      // validate the response
      if(!response.ok) {
        const errorData = await response.json();
        return { error: errorData.error || 'Cannot grant request' };
      }

      // if response doesn't have error, send the response to the controller
      return response.json(); 
    } catch (error) {
      console.error("Error fetching data", error);
      return { error: "Something went wrong. Please try again." };
    }
  }

  /**
   * Accept doctor's pending request
   */
  static async deletePendingDoctor(id) {
    try {
      // validate parsed token. If token is null, redirect to login
      validateParsedToken(this.token);

      // pass a post request using the backend's api
      const response = await fetch(`http://localhost:8002/api/admin/private/delete-doctor-request/${id}`, {
        method: 'DELETE',
        headers: {
          'Authorization' : `Bearer ${this.token}`,
          'Content-Type' : 'application/json'
        },
      });

      // validate the response
      if(!response.ok) {
        const errorData = await response.json();
        return { error: errorData.error || 'Cannot grant request' };
      }

      // if response doesn't have error, send the response to the controller
      return response.json(); 
    } catch (error) {
      console.error("Error fetching data", error);
      return { error: "Something went wrong. Please try again." };
    }
  }

  /**
   * GET all patients from db using the token
   */
  static async getAllPatients() {
    try {
      // validate parsed token. If token is null, redirect to login
      validateParsedToken(this.token);

      // use the token to fetch all the doctors using the backend's api
      const response = await fetch("http://localhost:8002/api/admin/private/get-all-patients", {
        method: 'GET',
        headers: {
          'Authorization' : `Bearer ${this.token}`,
          'Content-Type' : 'application/json'
        },
      });

      if(!response.ok) {
        const errorData = await response.json();
        return { error: errorData.error || 'Cannot fetch data' };
      }

      // if response isn't null, send the response to the controller
      return response.json(); 
    } 
    catch (error) {
      console.error("Error fetching data", error);
      return { error: "Something went wrong. Please try again." };
    }
  }

  static async getAllPatientAppointment() {
    try {
      // validate parsed token. If token is null, redirect to login
      validateParsedToken(this.token);

      // use the token to fetch all the doctors using the backend's api
      const response = await fetch("http://localhost:8002/api/admin/private/get-all-patients", {
        method: 'GET',
        headers: {
          'Authorization' : `Bearer ${this.token}`,
          'Content-Type' : 'application/json'
        },
      });

      if(!response.ok) {
        const errorData = await response.json();
        return { error: errorData.error || 'Cannot fetch data' };
      }

      // if response isn't null, send the response to the controller
      return response.json(); 
    } 
    catch (error) {
      console.error("Error fetching data", error);
      return { error: "Something went wrong. Please try again." };
    }
  }
} 