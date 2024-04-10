package com.nautico.usuarios;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("com.nautico.usuarios")
public class UsuariosApplication {

	public static void main(String[] args) {
		SpringApplication.run(UsuariosApplication.class, args);
	}
}


/*
* Problemas actuales
*  URGENTES
* El pattern de la contraseña da problemas con las contraseñas hasheadas. -- PARCHEADO
*  PIJERÍO
* Si usas un token expirado, el servido lanza una excepción interna no controlada y no avisa al usuario.
* Si usas un verbo no previsto, el servidor solo responde 401 unathorized
* No hay gestión de roles
* */