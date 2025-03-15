// if token is null, redirect to login
export function validateParsedToken(parsedToken) {
  if (!parsedToken) {
    // redirect to login page
     window.location.href = 'http://127.0.0.1:5500/src/main/resources/templates/admin/admin-auth.html';
     return null;
 }

 return parsedToken;
}

export function validateDoctorParsedToken(parsedToken) {
  if (!parsedToken) {
    // redirect to login page
     window.location.href = 'http://127.0.0.1:5500/src/main/resources/templates/doctor/doctor-auth.html';
     return null;
 }

 return parsedToken;
} 