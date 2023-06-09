async function searchByRange(startAge, endAge) {
  // Construye la URL de la solicitud con los parámetros opcionales
  let url = '/age-range?';

  if (startAge) {
    url += `startAge=${startAge}&`;
  }
  if (endAge) {
    url += `endAge=${endAge}&`;
  }

  // Realiza la solicitud GET a la URL construida
  const request = await fetch('/persons'+url, {
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
