##Parcial 1 - Decisiones:

###Justificacion relacion Turno-Medicamento

Lo que hice fue relacionar many to many desde turno hacia medicamento con una tabla asociativa turno_medicamento
En un turno se pueden recetar varios medicamentos. Y un mismo medicamento se puede recetar en muchos turnos.

Mantuve "spring.jpa.hibernate.ddl-auto=update" porque me permite que hibernate cree las tablas sin escribir el DDL a mano. 
Ademas estamos en entorno de desarrollo.


###Validacion de stock
En el service voy a poner el control de stock con un metodo llamado "descontarUnidad" en MedicamentoService. 
Lo puse en MedicamentoService porque el stock es un atributo propio del medicamento. 

Elegi como codigo de error 422 porque un 400 significa que la peticion esta mal formada o faltan datos y tampoco usé 409
porque no sería un caso de recurso o dato ya existente. 