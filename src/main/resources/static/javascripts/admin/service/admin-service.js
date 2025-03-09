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
} 