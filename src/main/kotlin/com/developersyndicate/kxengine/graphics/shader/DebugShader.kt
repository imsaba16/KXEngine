package com.developersyndicate.kxengine.graphics.shader

import com.developersyndicate.kxengine.graphics.shader.Shader

class DebugShader : Shader(
    vertexSource = """
        #version 330 core
        layout (location = 0) in vec2 aPos;

        uniform mat4 uVP;

        void main() {
            gl_Position = uVP * vec4(aPos, 0.0, 1.0);
        }
    """.trimIndent(),

    fragmentSource = """
        #version 330 core
        uniform vec4 uColor;
        out vec4 FragColor;

        void main() {
            FragColor = uColor;
        }
    """.trimIndent()
)