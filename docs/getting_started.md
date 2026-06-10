# KXEngine — Getting Started Guide

Welcome to **KXEngine**, a lightweight, professional 2D game engine built in Kotlin and LWJGL (OpenGL). This guide outlines how to structure a game application, manage assets, handle physics collisions, configure audio effects, and implement custom shaders.

---

## 🚀 1. Application Lifecycle

Every game application in KXEngine implements the [Game](file:///Users/sanjay/Documents/VibeCode/KXEngine/src/main/kotlin/com/developersyndicate/kxengine/Game.kt) interface, which structures the application's runtime sequence into clear lifecycle hooks:

```kotlin
import com.developersyndicate.kxengine.Game
import com.developersyndicate.kxengine.KXEngine

class MyGame : Game {
    
    override fun init(engine: KXEngine) {
        // Load assets, create meshes, bind camera targets, setup world geometry
    }

    override fun update(fixedDelta: Float) {
        // Runs at a stable, fixed timestep (60Hz by default)
        // Perform physics calculations, AI updates, and combat checks here
    }

    override fun render(delta: Float) {
        // Runs at a variable frame rate
        // Clear buffers and draw frame elements here
    }

    override fun dispose() {
        // Cleanup resources, destroy FBOs and custom vertex buffers
    }
}
```

Boot the game using the [KXEngine](file:///Users/sanjay/Documents/VibeCode/KXEngine/src/main/kotlin/com/developersyndicate/kxengine/KXEngine.kt) class:

```kotlin
fun main() {
    val myGame = MyGame()
    val engine = KXEngine(myGame)
    engine.start()
}
```

---

## 🛠️ 2. Configuration Settings

Create a `config.properties` file in your root project directory to manage engine startup configurations:

```properties
# Window Dimensions & Metadata
window.width=1280
window.height=720
window.title=My Production 2D Game

# Developer Options
profiler.enabled=true
hotreload.enabled=true
```

KXEngine automatically loads these configurations on startup when `EngineConfig.load()` is executed.

---

## 🧍 3. Advanced Physics & Collision Layers

KXEngine features a Swept AABB collision resolution pipeline that resolves sliding motion over multiple iterations and prevents high-speed tunneling.

### Defining Collider Layers & Masks
Set categories using bitwise integers to configure collision filters:

```kotlin
import com.developersyndicate.kxengine.physics.Collider

// Player belongs to Layer 1, collides with everything (-1 mask)
val playerCollider = Collider(player.transform, Vec2(0.25f, 0.25f), collisionLayer = 1, collisionMask = -1)

// Enemy belongs to Layer 2, collides with Player (Layer 1) and Ground (Layer 4) only (1 + 4 = 5 mask)
val enemyCollider = Collider(enemy.transform, Vec2(0.25f, 0.25f), collisionLayer = 2, collisionMask = 5)
```

### Dynamic Movement Resolution
Use `physics.resolveSliding` inside the `update` tick:

```kotlin
val result = physics.resolveSliding(
    dynamic = playerCollider,
    velocity = playerBody.velocity,
    statics = listOf(groundCollider),
    delta = fixedDelta
)

playerBody.velocity = result.velocity
playerBody.grounded = result.grounded
```

---

## 🔊 4. Audio Subsystem (OpenAL)

Initialize positional or stereo sound effects and music streams using the OpenAL wrappers.

```kotlin
import com.developersyndicate.kxengine.audio.Sound
import com.developersyndicate.kxengine.audio.SoundSource

// Load WAV audio file from resources
val jumpSound = Sound("assets/jump.wav")

// Initialize a playback source
val jumpSource = SoundSource(loop = false)

// Trigger playback
jumpSource.play(jumpSound)
```

Always release sources and buffers inside `dispose()` to prevent audio device memory leaks:
```kotlin
jumpSound.destroy()
jumpSource.destroy()
```

---

## 🎨 5. Shaders & Asset Hot-Reloading

During development, modifying asset files on disk immediately updates resources in the running window without requiring application restarts.

### File-Based Shaders
Load a shader from files using the path constructor:

```kotlin
import com.developersyndicate.kxengine.graphics.shader.Shader
import com.developersyndicate.kxengine.assets.Assets

val gameShader = Shader("shaders/game_vert.glsl", "shaders/game_frag.glsl")
Assets.registerShader(gameShader) // Registers the shader for filesystem hot-reloading
```

Whenever you save edits to `game_vert.glsl` or `game_frag.glsl` in your editor, KXEngine will recompile and link the shader dynamically in the next frame.
