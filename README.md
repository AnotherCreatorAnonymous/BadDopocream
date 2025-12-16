# Bad Dopo Cream

Juego desarrollado como proyecto final - Bad Dopo Cream.

## Descripción

Bad Dopo Cream es un juego de acción donde controlas helados en diferentes niveles, recolectando frutas y evitando enemigos.

## Estructura del Proyecto

```
demo/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── duran_jimenez/
│   │   │           └── baddopocream/
│   │   │               ├── domain/          # Lógica de negocio
│   │   │               └── presentation/    # Interfaz gráfica
│   │   └── resources/                       # Recursos (imágenes, fuentes)
│   └── test/
│       └── java/                            # Tests
├── target/                                  # Archivos compilados (generado)
└── pom.xml                                  # Configuración Maven
```

## Requisitos

- Java 17 o superior
- Maven 3.6 o superior

## Compilación

Para compilar el proyecto:

```bash
cd demo
mvn clean compile
```

## Ejecución

Para ejecutar el juego:

```bash
mvn exec:java -Dexec.mainClass="com.duran_jimenez.baddopocream.presentation.BadDopoCreamGUI"
```

O generar un JAR ejecutable:

```bash
mvn clean package
java -jar target/baddopocream-1.0-SNAPSHOT.jar
```

## Tests

Para ejecutar los tests:

```bash
mvn test
```

## Arquitectura

El proyecto sigue el patrón MVC (Model-View-Controller):

- **domain/**: Contiene toda la lógica del juego (modelo)
- **presentation/**: Contiene la interfaz gráfica (vista y controlador)

## Autores

Durán & Jiménez
