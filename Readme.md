# KXEngine — Kotlin 2D Game Engine

A lightweight 2D game engine built in **Kotlin + LWJGL**, designed as a **learning-first project** to understand engine architecture, physics, rendering pipelines, and gameplay systems.

This project intentionally prioritizes **clarity, correctness, and separation of concerns** over feature count.

---

## High-Level Architecture
```text
INPUT
  ↓
PLAYER / AI INTENT
  ↓
BODY (Velocity Only)
  ↓
PHYSICS (Gravity + Integration)
  ↓
COLLISION (AABB / Triggers)
  ↓
GAMEPLAY STATE (Health, Death, Checkpoints)
  ↓
RENDERING (SpriteBatch + Debug)
```

**Strict Rule:**  
Rendering is *read-only*. All gameplay logic happens **before** drawing.

---

## 🧠 Engine Systems Overview

### 🎮 Input & Control
- Keyboard input translated into **intent**
- Intent never modifies position directly
- Movement = velocity only

---

### 🧍 Physics & Collision
- Custom **AABB collision system**
- Axis-separated resolution (X then Y)
- Gravity, grounded detection, hit-stun
- Solid vs trigger colliders (no physics response)

---

### ⚔️ Combat System
- Separate **attack hitboxes**
- Damage + knockback handled by `DamageSystem`
- Invincibility frames & hit-stun
- No damage from body overlap

---

### ❤️ Health & Death
- Health component with cooldown
- Enemy death lifecycle:
- ALIVE → DYING (animation) → DEAD (despawn)
- Death disables AI, physics, and triggers

---

### 🧠 Enemy AI
- Patrol & chase behaviors
- AI writes velocity only
- AI disabled automatically on death

---

### 📍 Gameplay Systems
- Checkpoints via trigger volumes
- Respawn restores:
- Position
- Velocity
- Health
- No hard-coded resets

---

### 🎨 Rendering
- LWJGL + OpenGL
- Texture atlases
- Sprite batching (grouped by texture)
- Animator selects frame only (no timing logic in render)
- Separate debug shader & renderer

---

### 🛠 Debug Tools
- World grid
- Camera dead-zone visualization
- Collider outlines
- Toggleable at runtime

---

## 📐 Design Constraints (Intentional)

- ❌ Rendering never affects gameplay
- ❌ AI never teleports entities
- ❌ Combat never resolves physics
- ❌ Triggers never block movement
- ✅ Physics is the single source of truth for motion

---

## 🧩 Technologies Used
- **Language:** Kotlin (JVM)
- **Graphics:** LWJGL (OpenGL)
- **Math:** Custom vectors & matrices
- **Architecture:** Explicit systems (non-ECS, ECS-inspired)

---

## 🎯 Project Goals
- Learn engine architecture & algorithms
- Build a clean, explainable system
- Portfolio-ready, not production-ready
- Optimized for clarity over features

---

## 🚀 Current Status
✔ Movement & physics  
✔ Collision & triggers  
✔ Combat & knockback  
✔ Enemy AI  
✔ Health, death & respawn  
✔ Sprite animation & batching  
✔ Debug rendering

---

> KXEngine is a learning-first engine that demonstrates **how modern 2D engines are structured internally**, not just how games are made on top of them.