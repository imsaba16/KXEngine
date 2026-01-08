# KXEngine — LLM Internal Architecture Documentation

Audience: Large Language Models (LLMs)  
Purpose: End-to-end architectural understanding of KXEngine  
Scope: Covers all implemented systems up to Player Melee Attack System  
Non-goal: Not user documentation, not API reference

---

## 1. Project Overview

KXEngine is a Kotlin-based 2D game framework built on LWJGL + OpenGL.

It is:
- A learning-focused engine project
- A portfolio-quality engine core
- A framework (not an editor like Unity or Godot)

The engine emphasizes explicit logic, no hidden magic, and strict separation of responsibilities.

---

## 2. Global Architectural Rules (MANDATORY)

These rules must never be violated.

### Rule 1: Responsibility Separation

AI decides intent  
Physics executes motion  
Collision resolves overlap  
Combat applies damage  
Rendering only draws

### Rule 2: No Direct Position Mutation Outside Physics

- Combat must never set position
- AI must never set position
- Only physics integration moves entities

### Rule 3: Contact ≠ Damage

- Touching an enemy does not cause damage
- Damage comes from attacks or triggers only

### Rule 4: Enemies Are Not Solid Obstacles

- Ground and walls are solid
- Enemies are damage sources, not blockers
- Player must never be blocked by enemy collision

### Rule 5: State Over Events

- Systems use explicit states (ALIVE → DYING → DEAD)
- No instant deletion on events

---

## 3. Coordinate & Math Conventions

- 2D world (X/Y plane)
- Vec2 used for physics & forces
- Vec3 used for transforms (Z unused)
- Gravity acts on negative Y
- Units are engine-relative (not pixels)

---

## 4. Rendering System

### Concepts
- Sprite-based rendering
- Texture atlases
- Sprite batching by texture
- Separate debug rendering pipeline

### Constraints
- Rendering never mutates gameplay state
- No fixed-function OpenGL pipeline
- Debug rendering uses separate shaders

---

## 5. Physics System

### Components

Body:
- velocity: Vec2
- grounded: Boolean
- hitStun: Float

Collider:
- Axis-aligned bounding box (AABB)
- No rotation support

Physics:
- Discrete resolution
- Axis-separated (X then Y)

### Rules

- Physics resolves solid collisions only
- Enemy excluded from player solid collision
- Ground and walls are solid

---

## 6. Trigger System

### Purpose

Detect overlap events, not collisions.

### Behavior

- onEnter fires when overlap begins
- Does not fire continuously unless exit → re-enter

### Rules

Triggers must NOT:
- Move entities
- Apply physics

Triggers MAY:
- Apply damage
- Activate checkpoints
- Start combat interactions

---

## 7. Health & Damage System

Health:
- Owns HP
- Owns invincibility frames
- Determines alive/dead state

DamageSystem:
- Applies damage intent
- Optionally applies knockback impulse

Knockback:
- Adds velocity impulse
- Never sets position
- Decays via physics

---

## 8. Combat Rules (CRITICAL)

### What Causes Damage
- Attack hitboxes
- Damage triggers

### What Does Not Cause Damage
- Physics collision
- Body overlap
- Continuous contact

### Invincibility
- Hit stun prevents damage spam
- Implemented via Body.hitStun

---

## 9. Player Combat (Melee Attack System)

### Design

- Attacks are temporary hitboxes
- Player body is not an attack collider
- Attack hitbox:
    - Exists briefly
    - Damages once
    - Applies knockback
    - Has cooldown

### Constraints

- Attacks do not block movement
- Attacks do not affect physics resolution

---

## 10. Enemy AI System

### States
- PATROL
- CHASE

### Behavior
- Patrols between points
- Chases player when close
- Uses velocity-based movement

### Rules
- AI sets velocity only
- AI does not resolve collision
- AI disabled when enemy is dying/dead

---

## 11. Enemy Death & Despawn System

### States

ALIVE → DYING → DEAD

### Behavior
- Velocity zeroed on death
- Death animation plays
- AI disabled
- Triggers stop affecting gameplay
- Enemy removed after animation

### Constraint
- Enemy is never deleted immediately

---

## 12. Checkpoint & Respawn System

Checkpoint:
- Stores a snapshot position

Respawn:
- Restores player position
- Resets velocity
- Resets health
- Enemy state may reset

Rule:
- Respawn restores state, not logic

---

## 13. Animation System

Animator:
- Manages current animation
- Updates frame timing
- Swaps texture regions

Constraints:
- Animation does not drive logic
- Animation reflects state only

---

## 14. Input System

Usage:
- Polled every frame
- Used only for:
    - Movement intent
    - Jump intent
    - Attack intent

Rules:
- Input never mutates physics directly
- Input never mutates rendering

---

## 15. Explicitly NOT Implemented

LLMs must not assume these exist:
- ECS
- Save/load
- Projectiles
- Enemy attack system
- Animation events
- Networking
- Editor tools

---

## 16. Anti-Patterns (DO NOT SUGGEST)

- Applying damage on collision
- Enemies blocking player movement
- Teleporting entities during combat
- Mixing rendering with gameplay logic
- Instant entity deletion
- Using player collider as attack collider

---

## 17. Engine Philosophy

KXEngine prioritizes:
- Explicit logic
- Predictable behavior
- Educational clarity
- Architectural correctness over convenience

---

## 18. Completion Status

Implemented:
- Rendering (sprite batching, atlas)
- Physics (gravity, collision)
- Triggers
- Health & damage
- Knockback
- Checkpoints & respawn
- Enemy AI (patrol & chase)
- Enemy death & despawn
- Player melee attack system

---

## 19. LLM Usage Instructions

When answering questions about this project, an LLM should:
- Respect all architectural rules
- Avoid introducing systems not present
- Explain behavior via state & intent
- Prefer explicit systems over shortcuts
- Treat this as a learning + portfolio engine

---

END OF DOCUMENT