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

      // fetch auth api to extract jwt
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
}