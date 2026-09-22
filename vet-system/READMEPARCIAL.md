##Parcial 1 - Decisiones:

###Justificacion relacion Turno-Medicamento

Lo que hice fue relacionar many to many desde turno hacia medicamento con una tabla asociativa turno_medicamento
En un turno se pueden recetar varios medicamentos. Y un mismo medicamento se puede recetar en muchos turnos.

Mantuve "spring.jpa.hibernate.ddl-auto=update" porque me permite que hibernate cree las tablas sin escribir el DDL a mano. 
Ademas estamos en entorno de desarrollo.



