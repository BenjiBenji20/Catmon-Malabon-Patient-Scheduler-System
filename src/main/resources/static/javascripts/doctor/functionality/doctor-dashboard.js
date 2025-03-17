document.addEventListener('DOMContentLoaded', () => {
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
});