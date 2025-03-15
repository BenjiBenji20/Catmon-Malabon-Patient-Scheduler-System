export function dataValidator(data, className) {
  if(data.error) {
    document.querySelector(className).innerHTML = data.error;
    return;
  }
}