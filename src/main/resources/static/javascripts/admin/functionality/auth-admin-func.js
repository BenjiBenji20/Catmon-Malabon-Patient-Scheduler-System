const eyeIcon = document.querySelector('.bi-eye-slash');
const inputPasswordField = document.getElementById('password');

eyeIcon.addEventListener('click', () => {

  if(eyeIcon.className === 'bi bi-eye-slash') {
    inputPasswordField.type = 'text';
    eyeIcon.classList.replace('bi-eye-slash', 'bi-eye');
  }
  else if(eyeIcon.className === 'bi bi-eye') {
    inputPasswordField.type = 'password';
    eyeIcon.classList.replace('bi-eye', 'bi-eye-slash');
  }
});
