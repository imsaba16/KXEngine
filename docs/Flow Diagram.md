flowchart TD

    %% INPUT
    Input[Input System<br/>(Keyboard / Player Intent)]

    %% CONTROLLERS
    PlayerController[Player Controller<br/>- movement intent<br/>- jump intent<br/>- attack intent]
    EnemyAI[Enemy AI<br/>- Patrol<br/>- Chase<br/>- Sets velocity only]

    %% COMBAT
    AttackSystem[Attack System<br/>- Temporary hitboxes<br/>- Lifetime based]
    DamageSystem[Damage System<br/>- Apply damage<br/>- Apply knockback]
    Health[Health<br/>- HP<br/>- Invincibility frames]
    EnemyDeath[Enemy Death<br/>ALIVE → DYING → DEAD]

    %% PHYSICS
    Body[Body<br/>- Velocity<br/>- Gravity<br/>- Hit stun]
    Physics[Physics System<br/>- Integrate velocity<br/>- Axis-separated motion]

    %% COLLISION
    Collider[Collider (AABB)]
    TriggerSystem[Trigger System<br/>- onEnter events<br/>- Non-solid]

    %% GAMEPLAY
    CheckpointSystem[Checkpoint System<br/>- Save position<br/>- Respawn restore]

    %% RENDERING
    Animator[Animator<br/>- Select animation frame]
    SpriteBatch[Sprite Batch<br/>- Texture atlas<br/>- Batched draw calls]
    DebugRenderer[Debug Renderer<br/>- Grid<br/>- Colliders]
    OpenGL[OpenGL Renderer<br/>- Draw only]

    %% FLOW
    Input --> PlayerController
    PlayerController --> Body
    EnemyAI --> Body

    PlayerController --> AttackSystem
    AttackSystem --> DamageSystem
    DamageSystem --> Health
    DamageSystem --> Body
    Health --> EnemyDeath

    Body --> Physics
    Physics --> Collider

    Collider --> TriggerSystem
    TriggerSystem --> CheckpointSystem
    TriggerSystem --> DamageSystem

    EnemyDeath --> EnemyAI

    Body --> Animator
    Animator --> SpriteBatch
    SpriteBatch --> OpenGL

    Collider --> DebugRenderer
    DebugRenderer --> OpenGL

    %% CONSTRAINT NOTES
    classDef terminal fill:#1e1e1e,color:#ffffff
    OpenGL:::terminal