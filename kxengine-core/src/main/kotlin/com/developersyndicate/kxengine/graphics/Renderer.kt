package com.developersyndicate.kxengine.graphics

import com.developersyndicate.kxengine.graphics.ui.UIManager
import com.developersyndicate.kxengine.physics.Collider
import com.developersyndicate.kxengine.scene.Scene

interface Renderer {
    var ambientColor: Color
    var pointLight: PointLight2D?

    fun render(scene: Scene, camera: Camera, debug: Boolean)
    fun renderSprites(sprites: List<Renderable>, camera: Camera)
    fun renderParticles(particleSystem: ParticleSystem, camera: Camera)
    fun renderUI(uiManager: UIManager, uiCamera: UICamera)
    fun renderFramebuffer(
        fbo: Framebuffer,
        windowWidth: Int,
        windowHeight: Int,
        grayscale: Boolean = false,
        vignetteStrength: Float = 0f
    )
    fun clear(r: Float, g: Float, b: Float, a: Float)
    fun renderColliders(colliders: List<Collider>, camera: Camera)
    fun destroy(mesh: Mesh)
}
