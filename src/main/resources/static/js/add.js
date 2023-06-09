// Call the dataTables jQuery plugin
$(document).ready(function() {
//on ready
});

async function createPersons() {
  const person = new FormData();

  person.append('firstName', document.getElementById('txtFirstName').value);
  person.append('secondName', document.getElementById('txtSecondName').value);
  person.append('address', document.getElementById('txtAddress').value);
  person.append('dateOfBirth', document.getElementById('txtDate').value);
  person.append('phoneNumber', document.getElementById('txtPhone').value);
  person.append('image', document.getElementById('imageInput').files[0]);

  const request = await fetch('/persons/create', {
    method: 'POST',
    body: person
  });

  //const personJson = await request.json();
  window.location.replace("index.html")
  //console.log(personJson);
}

