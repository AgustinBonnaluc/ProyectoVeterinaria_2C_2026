Nombre del proyecto: vetSystem — Clínica Veterinaria "Patitas Felices"

Integrante: Agustin Bonnaluc
Legajo: 0133373
Usuario GitHub: AgustinBonnaluc

Como levantar el proyecto:
1. Clonar el repositorio
   git clone https://github.com/AgustinBonnaluc/ProyectoVeterinaria_2C_2026.git
   cd vet-system
   git checkout sprint-01
2. Crear la base de datos en MySQL
   CREATE DATABASE vet_system CHARACTER SET utf8mb4;
3. Configurar las credenciales
   spring.datasource.username=root
   spring.datasource.password=[tu_password]
4. Ejecutar: ./mvnw spring-boot:run 
    o desde el IDE ejecutar la clase VetSystemApplication 

