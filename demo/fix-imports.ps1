# Script para actualizar imports después de reorganización de paquetes

$basePath = "src\main\java\com\duran_jimenez\baddopocream"

# Mapeo de clases movidas a sus nuevos paquetes
$classToPackage = @{
    # Fruits
    'Fruit' = 'com.duran_jimenez.baddopocream.domain.fruits'
    'Banana' = 'com.duran_jimenez.baddopocream.domain.fruits'
    'Cactus' = 'com.duran_jimenez.baddopocream.domain.fruits'
    'Cherry' = 'com.duran_jimenez.baddopocream.domain.fruits'
    'Grapes' = 'com.duran_jimenez.baddopocream.domain.fruits'
    'Pineapple' = 'com.duran_jimenez.baddopocream.domain.fruits'
    'FruitInfo' = 'com.duran_jimenez.baddopocream.domain.fruits'
    'FruitWave' = 'com.duran_jimenez.baddopocream.domain.fruits'
    
    # Enemies
    'Enemy' = 'com.duran_jimenez.baddopocream.domain.enemies'
    'Troll' = 'com.duran_jimenez.baddopocream.domain.enemies'
    'Maceta' = 'com.duran_jimenez.baddopocream.domain.enemies'
    'Calamar' = 'com.duran_jimenez.baddopocream.domain.enemies'
    'CalamarNaranja' = 'com.duran_jimenez.baddopocream.domain.enemies'
    'Narval' = 'com.duran_jimenez.baddopocream.domain.enemies'
    'EnemyInfo' = 'com.duran_jimenez.baddopocream.domain.enemies'
    
    # Obstacles
    'BaldosaCaliente' = 'com.duran_jimenez.baddopocream.domain.obstacles'
    'Fogata' = 'com.duran_jimenez.baddopocream.domain.obstacles'
    'IceWall' = 'com.duran_jimenez.baddopocream.domain.obstacles'
    'IceBreaker' = 'com.duran_jimenez.baddopocream.domain.obstacles'
    'ObstacleInfo' = 'com.duran_jimenez.baddopocream.domain.obstacles'
    
    # AI
    'EnemyAI' = 'com.duran_jimenez.baddopocream.domain.ai'
    'IceCreamAI' = 'com.duran_jimenez.baddopocream.domain.ai'
    'PathFinder' = 'com.duran_jimenez.baddopocream.domain.ai'
    
    # Level
    'Level' = 'com.duran_jimenez.baddopocream.domain.level'
    'LevelBuilder' = 'com.duran_jimenez.baddopocream.domain.level'
    'LevelConfigurator' = 'com.duran_jimenez.baddopocream.domain.level'
    'Map' = 'com.duran_jimenez.baddopocream.domain.level'
    
    # Core
    'BadDopoCream' = 'com.duran_jimenez.baddopocream.domain.core'
    'BadDopoCream_Exceptions' = 'com.duran_jimenez.baddopocream.domain.core'
    'IceCream' = 'com.duran_jimenez.baddopocream.domain.core'
    'Location' = 'com.duran_jimenez.baddopocream.domain.core'
    'Movable' = 'com.duran_jimenez.baddopocream.domain.core'
    'FruitCounter' = 'com.duran_jimenez.baddopocream.domain.core'
    'GameState' = 'com.duran_jimenez.baddopocream.domain.core'
    'PlayerInfo' = 'com.duran_jimenez.baddopocream.domain.core'
    
    # Screens
    'WelcomeScreen' = 'com.duran_jimenez.baddopocream.presentation.screens'
    'CharacterSelectionScreen' = 'com.duran_jimenez.baddopocream.presentation.screens'
    'GameModeSelectionScreen' = 'com.duran_jimenez.baddopocream.presentation.screens'
    'GameScreen' = 'com.duran_jimenez.baddopocream.presentation.screens'
    'LevelConfigurationScreen' = 'com.duran_jimenez.baddopocream.presentation.screens'
    'LevelSelectionScreen' = 'com.duran_jimenez.baddopocream.presentation.screens'
    'LoadingScreen' = 'com.duran_jimenez.baddopocream.presentation.screens'
    'OptionsScreen' = 'com.duran_jimenez.baddopocream.presentation.screens'
    'PvPCharacterSelectionScreen' = 'com.duran_jimenez.baddopocream.presentation.screens'
    'PvPModeSelectionScreen' = 'com.duran_jimenez.baddopocream.presentation.screens'
    'SaveLoadScreen' = 'com.duran_jimenez.baddopocream.presentation.screens'
    'MenuPanel' = 'com.duran_jimenez.baddopocream.presentation.screens'
    
    # Configurators
    'BaseImageConfigurator' = 'com.duran_jimenez.baddopocream.presentation.configurators'
    'ButtonImageConfigurator' = 'com.duran_jimenez.baddopocream.presentation.configurators'
    'CharacterImageConfigurator' = 'com.duran_jimenez.baddopocream.presentation.configurators'
    'EnemyImageConfigurator' = 'com.duran_jimenez.baddopocream.presentation.configurators'
    'FruitImageConfigurator' = 'com.duran_jimenez.baddopocream.presentation.configurators'
    'ObstacleImageConfigurator' = 'com.duran_jimenez.baddopocream.presentation.configurators'
}

Write-Host "Actualizando imports en todos los archivos Java..."

Get-ChildItem -Path $basePath -Recurse -Filter "*.java" | ForEach-Object {
    $file = $_
    $content = Get-Content $file.FullName -Raw
    $modified = $false
    
    foreach ($class in $classToPackage.Keys) {
        $newPackage = $classToPackage[$class]
        
        # Buscar referencias a la clase sin import correcto
        if ($content -match "\b$class\b") {
            # Verificar si ya tiene el import correcto
            if ($content -notmatch "import $newPackage\.$class;") {
                # Buscar el package statement
                if ($content -match '(package [^;]+;)') {
                    $packageStmt = $matches[1]
                    # Agregar el import después del package si no existe
                    if ($content -notmatch "import.*\.$class;") {
                        $content = $content -replace $packageStmt, "$packageStmt`nimport $newPackage.$class;"
                        $modified = $true
                    } else {
                        # Reemplazar import viejo con el nuevo
                        $content = $content -replace "import com\.duran_jimenez\.baddopocream\.(domain|presentation)\.$class;", "import $newPackage.$class;"
                        $modified = $true
                    }
                }
            }
        }
    }
    
    if ($modified) {
        Set-Content $file.FullName -Value $content -NoNewline
        Write-Host "Actualizado: $($file.FullName)"
    }
}

Write-Host "`nImports actualizados correctamente."
