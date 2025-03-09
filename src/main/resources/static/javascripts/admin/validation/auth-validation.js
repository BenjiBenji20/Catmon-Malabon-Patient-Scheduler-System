// Listen for the form submission event
document.getElementById("form-login").addEventListener("submit", (event) => {
  event.preventDefault();  // Prevent form submission

  // Get values from the input fields
  const email = document.getElementById("email").value.trim();
  const password = document.getElementById("password").value.trim();

  // Check if both fields are filled
  if (email === "" || password === "") {
    const messageField = document.querySelector('.message');
    messageField.innerHTML = 'Please fill in all fields';
    return;
  }
});