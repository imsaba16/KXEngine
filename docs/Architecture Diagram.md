┌──────────────────────────────┐
│            INPUT             │
│  (Keyboard / Player Intent)  │
└──────────────┬───────────────┘
│
▼
┌──────────────────────────────┐
│        PLAYER CONTROLLER     │
│  - movement intent           │
│  - jump intent               │
│  - attack intent             │
└──────────────┬───────────────┘
│
▼
┌──────────────────────────────┐
│            AI                │
│  EnemyAI                     │
│  - PATROL / CHASE            │
│  - sets velocity only        │
└──────────────┬───────────────┘
│
▼
┌──────────────────────────────┐
│           COMBAT             │
│                              │
│  AttackSystem                │
│   - temporary hitboxes       │
│   - cooldowns                │
│                              │
│  DamageSystem                │
│   - applies damage           │
│   - applies knockback        │
│                              │
│  Health                      │
│   - HP                       │
│   - invincibility frames     │
│                              │
│  EnemyDeath                  │
│   - ALIVE → DYING → DEAD     │
│   - disables AI & triggers   │
└──────────────┬───────────────┘
│
▼
┌──────────────────────────────┐
│           PHYSICS            │
│                              │
│  Body                        │
│   - velocity                 │
│   - gravity                  │
│   - hit stun                 │
│                              │
│  Physics                     │
│   - integrates velocity      │
│   - axis-separated motion    │
│   - solid collision resolve  │
└──────────────┬───────────────┘
│
▼
┌──────────────────────────────┐
│         COLLISION            │
│                              │
│  Collider (AABB)             │
│   - solid: ground/walls      │
│   - trigger: attacks, zones  │
│                              │
│  TriggerSystem               │
│   - onEnter events           │
│   - no physics               │
└──────────────┬───────────────┘
│
▼
┌──────────────────────────────┐
│      GAMEPLAY SYSTEMS        │
│                              │
│  CheckpointSystem            │
│   - save position            │
│   - respawn restore          │
│                              │
│  Respawn                     │
│   - reset velocity           │
│   - reset health             │
│   - restore transform        │
└──────────────┬───────────────┘
│
▼
┌──────────────────────────────┐
│         RENDERING            │
│                              │
│  SpriteBatch                 │
│   - texture atlas            │
│   - batched draw calls       │
│                              │
│  Animator                    │
│   - selects animation frame  │
│                              │
│  Debug Renderer              │
│   - grids                    │
│   - colliders                │
│                              │
│  OpenGL                      │
│   - draw only                │
│   - no gameplay logic        │
└──────────────────────────────┘