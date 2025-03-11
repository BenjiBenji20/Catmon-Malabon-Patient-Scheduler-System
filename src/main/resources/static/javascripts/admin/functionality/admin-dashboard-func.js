document.addEventListener("DOMContentLoaded", function () {
  /**
   * MAKE MAIN CONTENT (TABLE) RESPONSIVE TO THE CANVAS
   */
  const offcanvasElement = document.getElementById("offcanvasScrolling");
  const rightPanel = document.querySelector(".right-panel");

  // Bootstrap Offcanvas event listeners
  offcanvasElement.addEventListener("show.bs.offcanvas", function () {
    rightPanel.classList.add("with-offcanvas"); // Expand right panel when offcanvas opens
  });

  offcanvasElement.addEventListener("hide.bs.offcanvas", function () {
    rightPanel.classList.remove("with-offcanvas"); // Reset when offcanvas closes
  });

  /**
 * MAKE THE TABLE HIDE AND VISIBLE WHEN CLICKING A TAB
 */
  const openPatientProfileTab = document.querySelector('.patients-profile-tab');
  const openAppointmentListTab = document.querySelector('.appointment-list-tab');
  const patientTableContainer = document.querySelector('.table-patient-profile-data');
  const appointmentTableContainer = document.querySelector('.table-appointment-data-hidden');

  // function to show and hide element
  function showOnly(elementToShow, elementToHide) {
    if (elementToShow.style.display !== "block") {
      elementToShow.style.display = "block"; // Make it visible
      setTimeout(() => {
        elementToShow.style.opacity = "1";
        elementToShow.style.transform = "translateY(0px)";
      }, 10);
    }
  
    if (elementToHide.style.display === "block") {
      elementToHide.style.opacity = "0"; // Start fade-out
      elementToHide.style.transform = "translateY(10px)";
      elementToHide.style.display = "none"; // Hide after animation
    }
  }

  // events for show and hide container
  openPatientProfileTab.addEventListener('click', () => {
    showOnly(patientTableContainer, appointmentTableContainer);
  });
  
  openAppointmentListTab.addEventListener('click', () => {
    showOnly(appointmentTableContainer, patientTableContainer);
  });

  // switch caret when click
  // const carretElement = document.querySelector('.bi bi-caret-down');
  // carretElement.addEventListener('click', () => {
  //   if (carretElement.classList.contains('bi bi-caret-down')) {
  //     carretElement.classList.replace('bi bi-caret-down', 'bi bi-caret-up');
  //   } else {
  //     carretElement.classList.replace('bi bi-caret-up', 'bi bi-caret-down');
  //   }
  // });
});

