# Estructura Final del Proyecto - Bad Dopo Cream

## Árbol de Directorios

```
Bad_Dopo_Cream/
│
├── 📄 README.md                          # Documentación principal del proyecto
├── 📄 REFACTORING_SUMMARY.md            # Resumen de cambios realizados
│
├── 📁 docs/                              # Documentación técnica
│   ├── 📄 MAVEN_BEST_PRACTICES.md       # Guía de mejores prácticas Maven
│   ├── 📄 domain-diagram.puml            # Diagrama de dominio
│   ├── 📄 presentation-diagram.puml      # Diagrama de presentación
│   ├── 📄 sequence-diagrams.puml         # Diagramas de secuencia
│   └── 📄 Bad_Dopo_CreamV2.asta         # Modelo Astah
│
├── 📁 saves/                             # Archivos de guardado del juego
│   └── guardar1.bdcsave
│
└── 📁 demo/                              # Proyecto Maven principal
    │
    ├── 📄 pom.xml                        # Configuración Maven
    ├── 📄 .gitignore                     # Exclusiones de Git
    │
    ├── 📁 src/
    │   │
    │   ├── 📁 main/
    │   │   │
    │   │   ├── 📁 java/
    │   │   │   └── 📁 com/
    │   │   │       └── 📁 duran_jimenez/
    │   │   │           └── 📁 baddopocream/
    │   │   │               │
    │   │   │               ├── 📁 domain/                    # 🎮 Lógica del Juego (31 clases)
    │   │   │               │   ├── 📄 BadDopoCream.java         # Controlador principal
    │   │   │               │   ├── 📄 Level.java                # Gestión de niveles
    │   │   │               │   ├── 📄 LevelBuilder.java         # Constructor de niveles
    │   │   │               │   ├── 📄 LevelConfigurator.java    # Configurador de niveles
    │   │   │               │   ├── 📄 GameState.java            # Estado del juego
    │   │   │               │   │
    │   │   │               │   ├── 🍦 Personajes
    │   │   │               │   │   ├── 📄 IceCream.java         # Helado jugador
    │   │   │               │   │   ├── 📄 IceCreamAI.java       # IA del helado
    │   │   │               │   │   ├── 📄 IceBreaker.java       # Rompehielos
    │   │   │               │   │   └── 📄 IceWall.java          # Muro de hielo
    │   │   │               │   │
    │   │   │               │   ├── 🍓 Frutas
    │   │   │               │   │   ├── 📄 Fruit.java            # Clase base
    │   │   │               │   │   ├── 📄 Banana.java           # +1 punto
    │   │   │               │   │   ├── 📄 Cherry.java           # +2 puntos
    │   │   │               │   │   ├── 📄 Grapes.java           # +5 puntos
    │   │   │               │   │   ├── 📄 Pineapple.java        # +10 puntos
    │   │   │               │   │   └── 📄 Cactus.java           # Fruta especial
    │   │   │               │   │
    │   │   │               │   ├── 👾 Enemigos
    │   │   │               │   │   ├── 📄 Enemy.java            # Clase base
    │   │   │               │   │   ├── 📄 Troll.java            # Enemigo troll
    │   │   │               │   │   ├── 📄 Narval.java           # Enemigo narval
    │   │   │               │   │   ├── 📄 Maceta.java           # Enemigo maceta
    │   │   │               │   │   ├── 📄 Calamar.java          # Enemigo calamar
    │   │   │               │   │   └── 📄 CalamarNaranja.java   # Calamar naranja
    │   │   │               │   │
    │   │   │               │   ├── 🧱 Obstáculos
    │   │   │               │   │   ├── 📄 BaldosaCaliente.java  # Baldosa caliente
    │   │   │               │   │   └── 📄 Fogata.java           # Fogata
    │   │   │               │   │
    │   │   │               │   ├── 📊 DTOs (Data Transfer Objects)
    │   │   │               │   │   ├── 📄 PlayerInfo.java       # Información del jugador
    │   │   │               │   │   ├── 📄 FruitInfo.java        # Información de frutas
    │   │   │               │   │   ├── 📄 EnemyInfo.java        # Información de enemigos
    │   │   │               │   │   └── 📄 ObstacleInfo.java     # Información de obstáculos
    │   │   │               │   │
    │   │   │               │   ├── 🛠️ Utilidades
    │   │   │               │   │   ├── 📄 Location.java         # Coordenadas
    │   │   │               │   │   ├── 📄 Map.java              # Mapa del juego
    │   │   │               │   │   └── 📄 FruitCounter.java     # Interfaz contador
    │   │   │               │   │
    │   │   │               │   └── 📄 BadDopoCream_Exceptions.java  # Excepciones
    │   │   │               │
    │   │   │               └── 📁 presentation/                # 🖥️ Interfaz Gráfica (23 clases)
    │   │   │                   ├── 📄 BadDopoCreamGUI.java       # Ventana principal
    │   │   │                   │
    │   │   │                   ├── 🖼️ Pantallas
    │   │   │                   │   ├── 📄 WelcomeScreen.java                 # Pantalla bienvenida
    │   │   │                   │   ├── 📄 MenuPanel.java                     # Panel de menú
    │   │   │                   │   ├── 📄 GameModeSelectionScreen.java       # Selección de modo
    │   │   │                   │   ├── 📄 CharacterSelectionScreen.java      # Selección de personaje
    │   │   │                   │   ├── 📄 LevelSelectionScreen.java          # Selección de nivel
    │   │   │                   │   ├── 📄 LevelConfigurationScreen.java      # Configuración de nivel
    │   │   │                   │   ├── 📄 GameScreen.java                    # Pantalla de juego
    │   │   │                   │   ├── 📄 PvPModeSelectionScreen.java        # Modo PvP
    │   │   │                   │   ├── 📄 PvPCharacterSelectionScreen.java   # Selección PvP
    │   │   │                   │   ├── 📄 SaveLoadScreen.java                # Guardar/Cargar
    │   │   │                   │   ├── 📄 OptionsScreen.java                 # Opciones
    │   │   │                   │   └── 📄 LoadingScreen.java                 # Pantalla de carga
    │   │   │                   │
    │   │   │                   ├── 🎨 Configuradores de Imágenes
    │   │   │                   │   ├── 📄 BaseImageConfigurator.java         # Configurador base
    │   │   │                   │   ├── 📄 CharacterImageConfigurator.java    # Imágenes personajes
    │   │   │                   │   ├── 📄 FruitImageConfigurator.java        # Imágenes frutas
    │   │   │                   │   ├── 📄 EnemyImageConfigurator.java        # Imágenes enemigos
    │   │   │                   │   ├── 📄 ObstacleImageConfigurator.java     # Imágenes obstáculos
    │   │   │                   │   └── 📄 ButtonImageConfigurator.java       # Imágenes botones
    │   │   │                   │
    │   │   │                   ├── 🎮 Controladores
    │   │   │                   │   ├── 📄 KeyboardListener.java    # Control por teclado
    │   │   │                   │   └── 📄 GameMenuBar.java         # Barra de menú
    │   │   │                   │
    │   │   │                   └── 📊 Enums
    │   │   │                       ├── 📄 CharacterType.java       # Tipos de personajes
    │   │   │                       └── 📄 EnemyType.java           # Tipos de enemigos
    │   │   │
    │   │   └── 📁 resources/                        # 🎭 Recursos del Juego (198 archivos)
    │   │       ├── 📁 Personajes/
    │   │       │   ├── Chocolate/                   # Sprites personaje chocolate
    │   │       │   ├── Fresa/                       # Sprites personaje fresa
    │   │       │   ├── Vainilla/                    # Sprites personaje vainilla
    │   │       │   └── Intro/                       # Animaciones intro
    │   │       │
    │   │       ├── 📁 Frutas/
    │   │       │   ├── Banana/                      # Sprites banana
    │   │       │   ├── Cherry/                      # Sprites cereza
    │   │       │   ├── Grapes/                      # Sprites uvas
    │   │       │   ├── Pineapple/                   # Sprites piña
    │   │       │   └── Cactus/                      # Sprites cactus
    │   │       │
    │   │       ├── 📁 Monstruos/
    │   │       │   ├── Narval/                      # Sprites narval
    │   │       │   ├── Pot/                         # Sprites maceta
    │   │       │   ├── Troll/                       # Sprites troll
    │   │       │   └── YellowSquid/                 # Sprites calamar amarillo
    │   │       │
    │   │       ├── 📁 Obstaculos/
    │   │       │   └── Obstaculos/
    │   │       │       ├── Baldosa_Caliente/        # Sprites baldosa caliente
    │   │       │       ├── Fogata/                  # Sprites fogata
    │   │       │       └── Hielo/                   # Sprites hielo
    │   │       │
    │   │       ├── 📁 Animaciones Hielo/            # Animaciones de hielo
    │   │       ├── 📁 Calamar Amarillo/             # Assets calamar amarillo
    │   │       ├── 📁 Troll/                        # Assets adicionales troll
    │   │       ├── 📁 Recusos nuevos/               # Recursos nuevos (.xcf)
    │   │       │
    │   │       └── 🖼️ Imágenes Generales
    │   │           ├── Fondo.png                    # Fondo del juego
    │   │           ├── Fondo Inicio.png             # Fondo pantalla inicio
    │   │           ├── imagen_de_menu.jpg           # Imagen de menú
    │   │           ├── Opciones_de_juego.png        # Opciones de juego
    │   │           └── Upheaval.fon                 # Fuente personalizada
    │   │
    │   └── 📁 test/
    │       └── 📁 java/
    │           └── 📁 com/
    │               └── 📁 duran_jimenez/
    │                   └── 📁 baddopocream/
    │                       └── 📁 test/             # 🧪 Tests (3 clases)
    │                           ├── 📄 GameTest.java
    │                           ├── 📄 CharacterAnimationDemo.java
    │                           └── 📄 TestCharacterAnimations.java
    │
    └── 📁 target/                                   # ⚙️ Compilación Maven (generado)
        ├── 📁 classes/                              # Clases compiladas
        │   ├── com/duran_jimenez/baddopocream/
        │   └── [recursos copiados]
        ├── 📁 test-classes/                         # Tests compilados
        └── baddopocream-1.0-SNAPSHOT.jar           # JAR final
```

## Estadísticas del Proyecto

### Código Fuente
- **Clases de Dominio**: 31 archivos
- **Clases de Presentación**: 23 archivos
- **Tests**: 3 archivos
- **Total Clases Java**: 57 archivos

### Recursos
- **Total Archivos de Recursos**: 198 archivos
- **Categorías de Recursos**: 6 (Personajes, Frutas, Monstruos, Obstáculos, Animaciones, UI)

### Organización por Paquetes

#### `com.duran_jimenez.baddopocream.domain` (Lógica de Negocio)
```
domain/
├── Core              (5 clases)  → BadDopoCream, Level, LevelBuilder, etc.
├── Characters        (4 clases)  → IceCream, IceWall, IceBreaker, etc.
├── Fruits            (6 clases)  → Banana, Cherry, Grapes, Pineapple, etc.
├── Enemies           (6 clases)  → Troll, Narval, Maceta, Calamar, etc.
├── Obstacles         (2 clases)  → BaldosaCaliente, Fogata
├── DTOs              (4 clases)  → PlayerInfo, FruitInfo, EnemyInfo, etc.
├── Utils             (3 clases)  → Location, Map, FruitCounter
└── Exceptions        (1 clase)   → BadDopoCream_Exceptions
```

#### `com.duran_jimenez.baddopocream.presentation` (Interfaz Gráfica)
```
presentation/
├── Main              (1 clase)   → BadDopoCreamGUI
├── Screens           (13 clases) → WelcomeScreen, GameScreen, MenuPanel, etc.
├── Image Config      (6 clases)  → BaseImageConfigurator, CharacterImageConfigurator, etc.
├── Controllers       (2 clases)  → KeyboardListener, GameMenuBar
└── Enums             (2 clases)  → CharacterType, EnemyType
```

## Patrones de Diseño Implementados

### MVC (Model-View-Controller)
- **Model**: Paquete `domain`
- **View**: Clases `*Screen` en `presentation`
- **Controller**: `BadDopoCreamGUI`, `KeyboardListener`

### Builder Pattern
- `LevelBuilder`: Construcción flexible de niveles

### Strategy Pattern
- `IceCreamAI`: Diferentes estrategias de IA

### Factory Pattern
- `LevelConfigurator`: Creación de niveles predefinidos

### Observer Pattern
- Uso implícito en eventos Swing

## Comandos Rápidos

```bash
# Compilar el proyecto
cd demo
mvn compile

# Ejecutar tests
mvn test

# Empaquetar JAR
mvn package

# Ejecutar el juego
java -jar target/baddopocream-1.0-SNAPSHOT.jar

# O directamente con Maven
mvn exec:java -Dexec.mainClass="com.duran_jimenez.baddopocream.presentation.BadDopoCreamGUI"
```

## Tecnologías Utilizadas

- **Java**: 17
- **Build Tool**: Maven 3.x
- **GUI Framework**: Swing
- **Testing**: JUnit 5
- **IDE**: Compatible con cualquier IDE (Eclipse, IntelliJ IDEA, VS Code)

---

✅ **Proyecto completamente refactorizado y listo para desarrollo**
