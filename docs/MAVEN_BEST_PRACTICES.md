# Guía de Mejores Prácticas Maven - Bad Dopo Cream

## Convenciones de Nombres

### Paquetes
- **Siempre usar minúsculas**: `com.duran_jimenez.baddopocream`
- **Evitar guiones bajos en producción**: Preferir `com.duranjimenez.baddopocream`
- **Estructurar por capas**: `domain`, `presentation`, `util`, etc.

### Clases
- **PascalCase**: `BadDopoCream`, `IceCream`, `GameScreen`
- **Nombres descriptivos**: Evitar abreviaciones innecesarias
- **Sufijos apropiados**: 
  - `*Test` para clases de prueba
  - `*Exception` para excepciones
  - `*Builder` para builders
  - `*Configurator` para configuradores

## Estructura de Directorios Maven Estándar

```
proyecto/
├── pom.xml
├── src/
│   ├── main/
│   │   ├── java/           # Código fuente
│   │   └── resources/      # Recursos (imágenes, configs)
│   └── test/
│       ├── java/           # Tests unitarios
│       └── resources/      # Recursos para tests
├── target/                 # Generado (no versionar)
└── README.md
```

## Configuración pom.xml

### Propiedades Esenciales
```xml
<properties>
    <maven.compiler.source>17</maven.compiler.source>
    <maven.compiler.target>17</maven.compiler.target>
    <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
</properties>
```

### Información del Proyecto
```xml
<groupId>com.duran_jimenez</groupId>
<artifactId>baddopocream</artifactId>
<version>1.0-SNAPSHOT</version>
<packaging>jar</packaging>
<name>Bad Dopo Cream</name>
<description>Descripción del proyecto</description>
```

### Versionado Semántico
- **MAJOR.MINOR.PATCH**
  - MAJOR: Cambios incompatibles
  - MINOR: Nuevas funcionalidades compatibles
  - PATCH: Correcciones de bugs
- **-SNAPSHOT**: Versión en desarrollo

## Gestión de Dependencias

### Scope Apropiado
```xml
<dependency>
    <groupId>junit</groupId>
    <artifactId>junit</artifactId>
    <version>5.10.1</version>
    <scope>test</scope>  <!-- Solo para tests -->
</dependency>
```

### Scopes Disponibles
- **compile** (default): Disponible en todas las fases
- **test**: Solo para compilación y ejecución de tests
- **provided**: Proporcionado por el JDK o contenedor
- **runtime**: No necesario para compilación
- **system**: Similar a provided, pero debe especificarse path

## Ciclo de Vida Maven

### Fases Principales
1. **validate**: Valida estructura del proyecto
2. **compile**: Compila código fuente
3. **test**: Ejecuta tests unitarios
4. **package**: Empaqueta en JAR/WAR
5. **verify**: Ejecuta checks de calidad
6. **install**: Instala en repositorio local
7. **deploy**: Copia a repositorio remoto

### Comandos Útiles
```bash
# Limpiar y compilar
mvn clean compile

# Ejecutar tests
mvn test

# Empaquetar sin tests
mvn package -DskipTests

# Instalar en repo local
mvn clean install

# Ver árbol de dependencias
mvn dependency:tree

# Analizar dependencias no usadas
mvn dependency:analyze
```

## Plugins Esenciales

### Maven Compiler Plugin
```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-compiler-plugin</artifactId>
    <version>3.11.0</version>
    <configuration>
        <source>17</source>
        <target>17</target>
    </configuration>
</plugin>
```

### Maven Surefire (Tests)
```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-surefire-plugin</artifactId>
    <version>3.2.2</version>
</plugin>
```

### Maven JAR Plugin
```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-jar-plugin</artifactId>
    <version>3.3.0</version>
    <configuration>
        <archive>
            <manifest>
                <mainClass>com.duran_jimenez.baddopocream.presentation.BadDopoCreamGUI</mainClass>
            </manifest>
        </archive>
    </configuration>
</plugin>
```

## Gestión de Recursos

### Ubicación
- **Recursos principales**: `src/main/resources/`
- **Recursos de test**: `src/test/resources/`

### Acceso en Código
```java
// Cargar recurso desde classpath
InputStream is = getClass().getResourceAsStream("/images/icon.png");

// Usando ClassLoader
URL url = getClass().getClassLoader().getResource("config.properties");
```

### Filtrado de Recursos
```xml
<resources>
    <resource>
        <directory>src/main/resources</directory>
        <filtering>true</filtering>  <!-- Permite @property@ en archivos -->
    </resource>
</resources>
```

## Testing

### Convenciones
- Clases de test en mismo paquete que la clase testeada
- Nombre: `ClaseTest.java` o `TestClase.java`
- Usar anotaciones JUnit: `@Test`, `@Before`, `@After`

### Ejemplo
```java
package com.duran_jimenez.baddopocream.domain;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class IceCreamTest {
    
    @Test
    public void testMovement() {
        IceCream ice = new IceCream("Test", Color.WHITE, new Location(0, 0));
        ice.moveUp();
        assertEquals(-1, ice.getLocation().getY());
    }
}
```

## .gitignore para Maven

```gitignore
# Maven
target/
pom.xml.tag
pom.xml.releaseBackup
pom.xml.versionsBackup
dependency-reduced-pom.xml

# IDE
.idea/
*.iml
.vscode/
.settings/
.classpath
.project

# OS
.DS_Store
Thumbs.db

# Compilados
*.class
*.jar
!maven-wrapper.jar
```

## Perfiles Maven

### Definición
```xml
<profiles>
    <profile>
        <id>development</id>
        <activation>
            <activeByDefault>true</activeByDefault>
        </activation>
        <properties>
            <env>dev</env>
        </properties>
    </profile>
    
    <profile>
        <id>production</id>
        <properties>
            <env>prod</env>
        </properties>
    </profile>
</profiles>
```

### Uso
```bash
mvn clean package -Pproduction
```

## Generación de Documentación

### JavaDoc
```bash
mvn javadoc:javadoc
```

### Plugin en pom.xml
```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-javadoc-plugin</artifactId>
    <version>3.6.3</version>
    <configuration>
        <show>public</show>
        <encoding>UTF-8</encoding>
    </configuration>
</plugin>
```

## Análisis de Código

### Checkstyle
```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-checkstyle-plugin</artifactId>
    <version>3.3.1</version>
</plugin>
```

### SpotBugs
```xml
<plugin>
    <groupId>com.github.spotbugs</groupId>
    <artifactId>spotbugs-maven-plugin</artifactId>
    <version>4.8.3.0</version>
</plugin>
```

## JAR Ejecutable con Dependencias

### Maven Assembly Plugin
```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-assembly-plugin</artifactId>
    <version>3.6.0</version>
    <configuration>
        <archive>
            <manifest>
                <mainClass>com.duran_jimenez.baddopocream.presentation.BadDopoCreamGUI</mainClass>
            </manifest>
        </archive>
        <descriptorRefs>
            <descriptorRef>jar-with-dependencies</descriptorRef>
        </descriptorRefs>
    </configuration>
    <executions>
        <execution>
            <id>make-assembly</id>
            <phase>package</phase>
            <goals>
                <goal>single</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```

### Crear JAR Fat
```bash
mvn clean package
java -jar target/baddopocream-1.0-SNAPSHOT-jar-with-dependencies.jar
```

## Troubleshooting Común

### Error: "Package does not exist"
- Verificar declaraciones `package` en archivos .java
- Asegurarse que estructura de carpetas coincide con paquetes

### Error: "Cannot find symbol"
- Revisar imports
- Verificar que dependencias estén en pom.xml
- Ejecutar `mvn clean compile`

### Tests no se ejecutan
- Verificar que clases terminen en `Test`
- Asegurarse que `maven-surefire-plugin` esté configurado
- Revisar que tests estén en `src/test/java`

### Recursos no se cargan
- Verificar que estén en `src/main/resources`
- Usar rutas relativas desde raíz del classpath
- No usar rutas absolutas del sistema de archivos

## Mejores Prácticas Generales

1. **Versionamiento**: Usar SNAPSHOT para desarrollo
2. **Dependencias**: Mantener actualizadas pero estables
3. **Tests**: Escribir tests para código crítico
4. **Documentación**: Mantener README.md actualizado
5. **Clean Build**: Ejecutar `mvn clean` regularmente
6. **IDE Integration**: Usar Maven plugins del IDE
7. **Continuous Integration**: Configurar CI/CD
8. **Code Review**: Revisar cambios en pom.xml
9. **Properties**: Externalizar configuraciones
10. **Profiles**: Usar perfiles para diferentes entornos

## Recursos Adicionales

- **Maven Official**: https://maven.apache.org/
- **Maven Repository**: https://mvnrepository.com/
- **Maven by Example**: https://books.sonatype.com/mvnex-book/
- **Maven Best Practices**: https://maven.apache.org/guides/

---

**Última actualización**: 15 de diciembre de 2025
