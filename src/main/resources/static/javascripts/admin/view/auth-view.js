
const containerElement = document.querySelector('.container');

containerElement.innerHTML = adminAuthenticationView();


export function adminAuthenticationView() {
  return `
    <div class="form-page-js">
        <form id="form-login">
          <div class="mb-2">
            <input type="email" class="form-control" id="email" placeholder="Email" required>
          </div>
          <div class="mb-3">
            <input type="password" class="form-control" id="password" placeholder="Password" required>
            <span class="input-group-text password-eye-icon" id="inputGroupPrepend">
              <i class="bi bi-eye-slash eye" title="show password"></i>
            </span>
          </div>
          <button type="submit" class="btn btn-primary">Submit</button>
          <span class="message">
            <!-- MESSAGE GENERATED HERE USING JS -->
          </span>
        </form>
      </div>
  `;
}