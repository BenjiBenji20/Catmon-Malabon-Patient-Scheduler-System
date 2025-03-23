const registrationForm = document.getElementById('registration-form');
registrationForm.addEventListener('submit', async (e) => {
  e.preventDefault();

  // hold patient input detail values
  const patientDetails = {
    completeName : document.getElementById('completeName').value,
    gender : document.getElementById('gender').value,
    address : document.getElementById('address').value,
    age : document.getElementById('age').value,
    contactNumber : document.getElementById('contactNumber').value,
    appointment : {
      scheduleDate : document.getElementById('scheduleDate').value,
      status : document.getElementById('status').value,
    }
  };

  try {
    // fetch from backend
    const result = await fetch('http://localhost:8002/api/patient/register', {
      method: 'POST',
      headers: { 'Content-Type' : 'application/json' },
      body: JSON.stringify(patientDetails)
    });

    const response = await result.json(); // get response as json

    if(response.error) {
      alert(response.error);
      return;
    }

    alert(response.message + '\nYou will receive a verification code.\nPlease use it upon appointment.');

    // reset input value
    document.getElementById('completeName').value = '';
    document.getElementById('gender').value = '';
    document.getElementById('address').value = '';
    document.getElementById('age').value = '';
    document.getElementById('contactNumber').value = '';
    document.getElementById('scheduleDate').value = '';
    document.getElementById('status').value = '';

  } catch (error) {
     console.error('Error passing data for patient registration', error);
    throw error;
  }
})