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
  
});