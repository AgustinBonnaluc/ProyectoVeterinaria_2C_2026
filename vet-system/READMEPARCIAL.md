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

###Solapamiento 
El algoritmo de deteccion lo hace TurnoService.registrarTurno antes de persistir, 
Comparo los parametros veterinarioId, fecha y hora porque son los 3 parametros que definen la si ya existe un turno asignado
a un veterinario en una fecha y una hora.
Para la modificacion de un turno uso una variante: `findByVeterinarioIdAndFechaAndHoraAndIdNot`, que
excluye el turno que se está editando.

###Cupo de mascotas 
La validacion se hace en MascotaService con .guardarMascota, se verifica despues de verificar que el dueño existe.
Elegí "count" en la consulta porque el conteo lo resuelve la base de datos
La comparacion es porque si un dueño ya tiene 5 mascotas no puede sumar una sexta mascota, para ese caso uso CupoMascotasExcedidoException.

El criterio de mascotas activas en este caso creo que lo definiria como que toda mascota es una mascota activa, y simplemente
al contarlas las contamos como activas. 

###Decision mas dificil
Las decisiones mas dificiles las tuve desde el lado del razonar con la logica que implementa la nueva entidad Medicamento.
El cambio de existsBy a findBy: la consigna pedia informar el turno conflictivo y el método que
tenía solo devolvia true/false, así que tuve que rehacer la consulta.
Elegir entre @ManyToMany y una entidad asociativa/intermedia: cual me conviene y por que es mejor una asociativa.
Decidir el codigo HTTP: 400 contra 422 contra 409, cual usar en este caso.