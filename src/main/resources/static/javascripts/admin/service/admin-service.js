export class AdminServiceAPI {
  /**
   * User login fetch json from backend to post a req
   */
  static async authAdminService(adminCredentials) {
    try {
      // fetch api
      const response = await fetch("http://localhost:8002/api/admin/public/auth", {
        method: "POST",
        headers: {"Content-Type": "application/json"},
        body: JSON.stringify(adminCredentials)
      });

      if(!response.ok) {
        throw new Error("Login failed. Invalid email or password");
      }

      const data = await response.json(); // extract data response
      localStorage.setItem("adminJwt", JSON.stringify(data));
      console.log("In service:", JSON.parse(localStorage.getItem("adminJwt")));
      
      return data;
    } 
    catch (error) {
      console.error("Error authenticating the admin", error);
      throw error;
    }
  }
}