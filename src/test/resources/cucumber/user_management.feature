# language: es
Característica: Gestion de usuarios
  
  Escenario: Navegación a la lista de usuarios
    Dado un usuario esta en la pagina inicial
    Cuando el usuario hace click sobre el botón de Usuarios
    Entonces esta en la pagina de lista de usuarios

  
  Escenario: Comprobar que el formulario de creación de usuarios tiene todos los elementos
    Dado un usuario esta en la pagina creación de usuarios
    Entonces se muestra un campo de correo electrónico
    Y se muestra un campo de nombre
    Y se muestra un campo de primer apellido
    Y se muestra un campo de segundo apellido
    Y se muestra un botón de creación

  Escenario: Crear un usuario correctamente
    Dado un usuario esta en la pagina creación de usuarios
    Y el correo usuario@correo.com no esta asignado a otro usuario
    Cuando relleno el campo correo electrónico con usuario@correo.com
    Y relleno el campo nombre con David
    Y relleno el campo primer apellido con Hormigo
    Y el usuario hace click sobre el botón de crear usuario
    Entonces esta en la pagina de lista de usuarios
    Y se ha persistido el usuario en la base de datos

    Escenario: Error al crear un usuario porque falta el nombre
    Dado un usuario esta en la pagina creación de usuarios
    Cuando relleno el campo correo electrónico con usuario@correo.com
    Y relleno el campo primer apellido con testApellido
    Y el usuario hace click sobre el botón de crear usuario
    Entonces salta un error porque el nombre está vacío
    Y no se ha persistido el usuario en la base de datos

  Escenario: Error al crear un usuario porque falta el correo
    Dado un usuario esta en la pagina creación de usuarios
    Cuando relleno el campo nombre con test
    Y relleno el campo primer apellido con testApellido
    Y el usuario hace click sobre el botón de crear usuario
    Entonces salta un error porque el correo está vacío
    Y no se ha persistido el usuario en la base de datos

  Escenario: Error al crear un usuario porque falta el primer apellido
    Dado un usuario esta en la pagina creación de usuarios
    Cuando relleno el campo nombre con test
    Y relleno el campo correo electrónico con usuario@correo.com
    Y el usuario hace click sobre el botón de crear usuario
    Entonces salta un error porque el primer apellido está vacío
    Y no se ha persistido el usuario en la base de datos

  Escenario: Error al crear un usuario porque el correo ya existe
    Dado un usuario esta en la pagina creación de usuarios
    Y el correo usuario@correo.com si esta asignado a otro usuario
    Cuando relleno el campo nombre con test
    Y relleno el campo correo electrónico con usuario@correo.com
    Y relleno el campo primer apellido con testApellido
    Y el usuario hace click sobre el botón de crear usuario
    Entonces salta un error porque el correo ya existe
    Y no se ha persistido el usuario en la base de datos