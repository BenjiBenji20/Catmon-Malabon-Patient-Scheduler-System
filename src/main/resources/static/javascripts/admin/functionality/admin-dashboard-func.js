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

  // switch tab background color based on url path
  // Function to switch active tab dynamically
  function switchTab(clickedTab) {
    // Reset all tabs
    document.querySelectorAll('.nav-item').forEach(tab => {
      tab.classList.remove('nav-active');
      tab.classList.add('nav-off');
    });

    // Activate clicked tab
    clickedTab.classList.add('nav-active');
    clickedTab.classList.remove('nav-off');
  }

  // Attach event listener to each navigation item
  document.querySelectorAll('.nav-item').forEach(nav => {
    nav.addEventListener('click', function () {
      switchTab(this); // Pass clicked element
    });
  });

  // Ensure the correct tab is active when the page loads
  window.addEventListener("load", function () {
    const hash = window.location.hash;
    if (hash === "#appointment-list-tab") {
      switchTab(document.querySelector(".appointment-nav"));
    } else {
      switchTab(document.querySelector(".patient-nav"));
    }
  });

  /**
   * ADD FUNCTIONALITY TO HIDE FILTER FORM BOTH PATIENT AND APPOINTMENT TABLE
   */
  // patient table filter button
  const patientFilterBtn = document.getElementById("patient-filter-btn");
  const patientFilterBox = document.getElementById("patient-profile-table-filter-form-js");

  // Show/hide filter box on button click
  patientFilterBtn.addEventListener("click", function (event) {
      event.stopPropagation();
      patientFilterBox.style.display = (patientFilterBox.style.display === "block") ? "none" : "block";
  });

  // Hide filter box when clicking outside
  document.addEventListener("click", function (event) {
      if (!patientFilterBox.contains(event.target) && event.target !== patientFilterBtn) {
          patientFilterBox.style.display = "none";
      }
  });

  // Prevent hiding when clicking inside the filter box
  patientFilterBox.addEventListener("click", function (event) {
      event.stopPropagation();
  });

  // appointment table filter button
  const appointmentFilterBtn = document.getElementById("appointment-filter-btn");
  const appointmentFilterBox = document.getElementById("appointment-table-filter-form-js");

  // Show/hide filter box on button click
  appointmentFilterBtn.addEventListener("click", function (event) {
      event.stopPropagation();
      appointmentFilterBox.style.display = (appointmentFilterBox.style.display === "block") ? "none" : "block";
  });

  // Hide filter box when clicking outside
  document.addEventListener("click", function (event) {
      if (!appointmentFilterBox.contains(event.target) && event.target !== appointmentFilterBtn) {
          appointmentFilterBox.style.display = "none";
      }
  });

  // Prevent hiding when clicking inside the filter box
  appointmentFilterBox.addEventListener("click", function (event) {
      event.stopPropagation();
  });
});
