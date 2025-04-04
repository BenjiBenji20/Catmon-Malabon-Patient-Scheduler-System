import { validateDoctorParsedToken } from "../../validation/jwt-validation.js";

export class DoctorServiceAPI {
  static async authenticateDoctor(doctorCredentials) {
    try {
      // fetch auth api to extract jwt
      const response = await fetch('http://localhost:8002/api/doctor/public/login', {
        method: 'POST',
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(doctorCredentials)
      });

      // extract async data
      const data = await response.json(); 

      if(!response.ok) {
        // return error from the backend
        return { error: data.error || "Invalid email or password" };
      }

      // save response token in the local storage
      localStorage.setItem("doctorJwt", JSON.stringify(data)); 

      return data;
    } catch (error) {
      console.error("Error authenticating the doctor", error);
      return { error: "Something went wrong. Please try again." };
    }
  }

  // extract saved token
  static tokenJson = localStorage.getItem('doctorJwt');
  static token = DoctorServiceAPI.tokenJson ? JSON.parse(DoctorServiceAPI.tokenJson) : null;

  static async getDoctorProfile() {
    try {
      // validate parsed token. If token is null, redirect to login
      validateDoctorParsedToken(this.token);

      const response = await fetch('http://localhost:8002/api/doctor/private/get-doctor-profile', {
        method: 'GET',
        headers: { 
          'Authorization' : `Bearer ${this.token}`,
          "Content-Type": "application/json" 
        },
      });      

      const data = await response.json();

      if(!response.ok) {
        return { error: data.error || "Invalid email or password" };
      }

      return data;
    } catch (error) {
      console.error("Error fetching data", error);
      return { error: "Something went wrong. Please try again." };
    }
  }

  static async getPatientsToday() {
    try {
      // validate parsed token. If token is null, redirect to login
      validateDoctorParsedToken(this.token);

      // get the current date today
      const currentDate = new Date().toISOString().split('T')[0];

      // fetch using jwt
      const response = await fetch(`http://localhost:8002/api/doctor/private/get-patients-today/${currentDate}`, {
        method: 'GET',
        headers: { 
          'Authorization' : `Bearer ${this.token}`,
          "Content-Type": "application/json" 
        },
      });

      if(!response.ok) {
        console.error(`Error: ${response.status} ${response.statusText}`);
        return { error: `Failed to load patients: ${response.statusText}` };
      }

      const data = await response.json();
      
      return data ? data : []; 
    } catch (error) {
      console.error("Error fetching data", error);
      return error;
    }
  }

  static async getPatientsByDay() {
    try {
      // validate parsed token. If token is null, redirect to login
      validateDoctorParsedToken(this.token);

      const response = await fetch(`http://localhost:8002/api/doctor/private/patients-by-available-days`, {
        method: 'GET',
        headers: { 
          'Authorization' : `Bearer ${this.token}`,
          "Content-Type": "application/json" 
        },
      });

      if(!response.ok) {
        console.error(`Error: ${response.status} ${response.statusText}`);
        return { error: `Failed to load patients: ${response.statusText}` };
      }

      const data = await response.json();
      
      return data ? data : [];
    } catch (error) {
      console.error("Error fetching data", error);
      return error;
    }
  }

  static async getAllMyPatients() {
    try {
      // validate parsed token. If token is null, redirect to login
      validateDoctorParsedToken(this.token);

      const response = await fetch(`http://localhost:8002/api/doctor/private/get-all-my-patients`, {
        method: 'GET',
        headers: { 
          'Authorization' : `Bearer ${this.token}`,
          "Content-Type": "application/json" 
        },
      });

      if(!response.ok) {
        console.error(`Error: ${response.status} ${response.statusText}`);
        return { error: `Failed to load patients: ${response.statusText}` };
      }

      const data = await response.json();
      
      return data;  
    } catch (error) {
      console.error("Error fetching data", error);
      return error;
    }
  }

  static async setPatientRecord(patientId, record) {

    try {
      // validate parsed token. If token is null, redirect to login
      validateDoctorParsedToken(this.token);

      const response = await fetch(`http://localhost:8002/api/doctor/private/patient-record/${patientId}`, {
        method: 'POST',
        headers: { 
          'Authorization' : `Bearer ${this.token}`,
          "Content-Type": "application/json" 
        },
        body: JSON.stringify(record) // pass the inputs as request body
      });

      if(!response.ok) {
        console.error(`Error: ${response.status} ${response.statusText}`);
        return { error: `Failed to load patients: ${response.statusText}` };
      }

      const data = await response.json();

      return data;
    } catch (error) {
      console.error("Error sending and fetching data", error);
      return error;
    }
  }

  static async getPatientDetails(id) {
    try {
      validateDoctorParsedToken(this.token);

      const response = await fetch(`http://localhost:8002/api/doctor/private/get-patient-details/${id}`, {
        method: 'GET',
        headers: { 
          'Authorization' : `Bearer ${this.token}`,
          "Content-Type": "application/json" 
        },
      });

      if(!response.ok) {
        console.error(`Error: ${response.status} ${response.statusText}`);
        return { error: `Failed to load patients: ${response.statusText}` };
      }

      const data = await response.json();

      return data;  
    } catch (error) {
      console.error("Error fetching data", error);
      return error;
    }
  }

  static async updatePatientStatus(id, status) {
    try {
      validateDoctorParsedToken(this.token);

      const response = await fetch(`http://localhost:8002/api/doctor/private/update-patient-status?patientId=${id}&newStatus=${status}`, {
        method: 'PUT',
        headers: { 
          'Authorization' : `Bearer ${this.token}`,
          "Content-Type": "application/json" 
        },
      });

      if(!response.ok) {
        console.error(`Error: ${response.status} ${response.statusText}`);
        return { error: `Failed to load patients: ${response.statusText}` };
      }

       const data = await response.json();
      
      return data;  
    } catch (error) {
      console.error("Error fetching data", error);
      return error;
    }
  }

/**
 * These params are came from user inputs
 * @param {*} gender a string
 * @param {*} age a numver
 * @param {*} status an enum
 * @returns patient data responsed from filter
 */
  static async filterPatient(gender, age, status) {
    try {
      // validate parsed token. If token is null, redirect to login
      validateDoctorParsedToken(this.token);

      // Construct query parameters dynamically
      const params = new URLSearchParams();
      if (gender) params.append("gender", gender);
      if (age) params.append("age", age);
      if (status) params.append("status", status);

      // Construct final URL
      const url = `http://localhost:8002/api/doctor/private/filter?${params.toString()}`;

      // use the token to fetch all the doctors using the backend's api
      const response = await fetch(url, {
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
      return error;
    }
  }

/**
   * 
   * @param {*} keyword passed from search box
   */
  static async searchPatient(keyword) {
    try {
      // validate parsed token. If token is null, redirect to login
      validateDoctorParsedToken(this.token);

      // use the token to fetch all the doctors using the backend's api
      const response = await fetch(`http://localhost:8002/api/doctor/private/search-patient?keyword=${keyword}`, {
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
      return { error: "Cannot fetch request." };
    }
  }
}