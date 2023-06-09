$(document).ready(function() {
//on ready
});

async function updateView(id){

const request = await fetch('/persons/'+id, {
    method: 'GET',
    headers: {
      'Accept': 'application/json',
      'Content-Type': 'application/json'
    },

  });
  const person = await request.json();
  //window.location.replace("update.html");

  let listadoHtml='<div class="form-group row"><div class="col-sm-6 mb-3 mb-sm-0"><input type="text" class="form-control form-control-user" id="txtFirstName" placeholder="First Name" value='+person.firstName+'></div><div class="col-sm-6"><input type="text" class="form-control form-control-user" id="txtSecondName"placeholder="Second Name"value='+person.secondName+'></div></div><div class="form-group"><input type="text" class="form-control form-control-user" id="txtAddress" placeholder="Address" value='+person.addresses+'></div><div class="form-group row"> <div class="col-sm-6 mb-3 mb-sm-0"> <input type="text" class="form-control form-control-user"id="txtDate" placeholder="Date of Birth" value='+person.dateOfBirth+'> </div> <div class="col-sm-6"><input type="text" class="form-control form-control-user"id="txtPhone" placeholder="Phone Number" value='+person.phoneNumbers+'></div></div><a href="#" onclick="updatePerson('+person.id+')" class="btn btn-google btn-user btn-block"> Update person</a>';

document.querySelector('#formUpdate').outerHTML=listadoHtml;
}

async function updatePerson(id){
let person={};
person.firstName=document.getElementById('txtFirstName').value;
person.secondName=document.getElementById('txtSecondName').value;
person.addresses=document.getElementById('txtAddress').value;
person.dateOfBirth=document.getElementById('txtDate').value;
person.phoneNumbers=document.getElementById('txtPhone').value;

const request = await fetch('/persons/update/'+id, {
    method: 'PUT',
    headers: {
      'Accept': 'application/json',
      'Content-Type': 'application/json'
    },
    body:JSON.stringify(person)

  });
  window.location.replace("index.html");
}