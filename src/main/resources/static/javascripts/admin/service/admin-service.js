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

  /**
   * Fetch all doctors from db using the token
   */
  static async getAllDoctors() {
    try {
      // Extract the stored token from local storage
      const tokenJson = localStorage.getItem('adminJwt');
      
      // if jwt isn't found from local storage 
      if (!tokenJson) {
          window.location.href = 'http://127.0.0.1:5500/src/main/resources/templates/admin/admin-auth.html';
          return null;
      }

      // extract the stored token from local storage
      const token = JSON.parse(tokenJson);
      
      // if token is null, redirect to login
      if(!token) {
        window.location.href = 'http://127.0.0.1:5500/src/main/resources/templates/admin/admin-auth.html';
        return null;
      }

      // use the token to fetch all the doctors using the backend's api
      const response = await fetch("http://localhost:8002/api/admin/private/get-all-doctors", {
        method: 'GET',
        headers: {
          'Authorization' : `Bearer ${token}`,
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