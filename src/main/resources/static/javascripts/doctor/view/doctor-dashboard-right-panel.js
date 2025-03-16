import { setPatientRecord, loadAllMyPatients, loadPatientDetails,
  loadUpdatedPatientStatus
 } from "../controller/doctor-dashboard-controller.js";

document.addEventListener('DOMContentLoaded', async () => {
  const modal = new bootstrap.Modal(document.getElementById('patientModal'));
  let currentPatientId = null;

  // Event listener for opening the modal (using event delegation)
  document.body.addEventListener('click', async (e) => {
    const patientLink = e.target.closest('a[data-patient-id]');
    if (patientLink) {
      e.preventDefault();
      currentPatientId = patientLink.getAttribute('data-patient-id');

      // Fetch patient details
      const patientDetails = await loadPatientDetails(currentPatientId);
      console.log('Patient details:', patientDetails);

      if (patientDetails.error) {
        alert(patientDetails.error);
        return;
      }

      // Populate modal with patient details
      document.getElementById('modalPatientName').textContent = patientDetails.completeName;
      document.getElementById('modalPatientAge').textContent = `Age: ${patientDetails.age}`;
      document.getElementById('modalPatientGender').textContent = `Gender: ${patientDetails.gender}`;
      document.getElementById('modalPatientStatus').textContent = `Status: ${patientDetails.status}`;

      // Show the modal
      modal.show();
    }
  });

  // Event listener for saving the record
  document.getElementById('savePatientRecord').addEventListener('click', async () => {
    const diagnosis = document.getElementById('setDiagnosis').value.trim();
    const prescription = document.getElementById('setPrescription').value.trim();
    const attended = document.getElementById('isAttended').value === 'true';
    const status = document.getElementById('setRecordStatus').value.trim();

    const record = {
      attended,
      prescription,
      diagnosis
    };

    // Save the record
    const result = await setPatientRecord(currentPatientId, record);
    console.log('Setting patient record result:', result);

    // save status individually
    const statusResult = await loadUpdatedPatientStatus(currentPatientId, status);
    console.log(statusResult);
    

    if (result.error) {
      alert(result.error);
    } else {
      alert('Record saved successfully!');
      modal.hide();

      // Reset input fields
      document.getElementById('setDiagnosis').value = '';
      document.getElementById('setPrescription').value = '';
      document.getElementById('isAttended').value === 'true';
      document.getElementById('setRecordStatus').value = '';
    }
  });
});