// Call the dataTables jQuery plugin
$(document).ready(function() {
      getPersons();
  $('#persons').DataTable();
});

async function getPersons(){

  const request = await fetch('/persons', {
    method: 'GET',
    headers: {
      'Accept': 'application/json',
      'Content-Type': 'application/json'
    },

  });
  const persons = await request.json();
    let listadoHtml="";
  for(let person of persons){
  let photo=person.personalPhoto == null ? '-' : person.personalPhoto;
  let botonUpdate='<a href="update.html" class="btn btn-primary btn-icon-split btn-sm"><span class="icon text-white-50"><i class="fas fa-flag"></i></span><span class="text">Update</span></a>';
  let botonDelete='<a href="#" onclick="deletePerson('+person.id+')" class="btn btn-danger btn-icon-split btn-sm"><span class="icon text-white-50"><i class="fas fa-flag"></i></span><span class="text">Eliminar</span></a>'

  //let botonDelete='<a href="#" onclick="deletePerson('+person.id+')" class="btn btn-danger btn-circle btn-sm"><i class="fas fa-trash"></i></a>';
  let personHtml='<tr><td>'+person.id+'</td><td>'+person.firstName+'</td><td>'+person.secondName+'</td><td>'+person.addresses+'</td><td>'+person.dateOfBirth+'</td><td>'+person.phoneNumbers+'</td><td>'+photo+'</td><td>'+botonDelete+botonUpdate+'</td</tr>';
  listadoHtml+=personHtml
  }
  console.log(persons);
document.querySelector('#persons tbody').outerHTML=listadoHtml;
}

async function getPersonById(id){

  const request = await fetch('/persons/'+id, {
    method: 'GET',
    headers: {
      'Accept': 'application/json',
      'Content-Type': 'application/json'
    },

  });
  const person = await request.json();

  let photo=person.personalPhoto == null ? '-' : person.personalPhoto;
  let botonUpdate='<a href="update.html" class="btn btn-primary btn-icon-split btn-sm"><span class="icon text-white-50"><i class="fas fa-flag"></i></span><span class="text">Update</span></a>';
  let botonDelete='<a href="#" onclick="deletePerson('+person.id+')" class="btn btn-danger btn-icon-split btn-sm"><span class="icon text-white-50"><i class="fas fa-flag"></i></span><span class="text">Eliminar</span></a>'

  //let botonDelete='<a href="#" onclick="deletePerson('+person.id+')" class="btn btn-danger btn-circle btn-sm"><i class="fas fa-trash"></i></a>';
  let personHtml='<tr><td>'+person.id+'</td><td>'+person.firstName+'</td><td>'+person.secondName+'</td><td>'+person.addresses+'</td><td>'+person.dateOfBirth+'</td><td>'+person.phoneNumbers+'</td><td>'+photo+'</td><td>'+botonDelete+botonUpdate+'</td</tr>';

  //console.log(persons);
document.querySelector('#persons tbody').outerHTML=personHtml;
}

async function deletePerson(id){
if(!confirm('Desea eliminar el usuario')){
return;
}

const request = await fetch('/persons/delete/'+id, {
    method: 'DELETE',
    headers: {
      'Accept': 'application/json',
      'Content-Type': 'application/json'
    }

  });
  location.reload()
}