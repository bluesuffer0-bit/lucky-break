package com.example.game

data class PlayerStats(
    val hp: Int = 20,
    val maxHp: Int = 20,
    val hunger: Int = 20,
    val x: Int = 0,
    val y: Int = 0,
    val z: Int = 0,
    val dead: Boolean = false,
    val onGround: Boolean = true,
    val selectedSlot: Int = 0
)

data class GameSettings(
    val sensitivity: Float = 1.4f,
    val invertY: Boolean = false,
    val hapticsEnabled: Boolean = true,
    val highQualityGraphics: Boolean = true
)

enum class GameQuest(
    val id: String,
    val title: String,
    val description: String,
    val icon: String,
    val hint: String
) {
    MIRRORSHARDS(
        id = "mirrorshards",
        title = "Gather Mirrorshards",
        description = "Mine deep underground and break rare lucky blocks to discover shattered rainbow mirrorshards.",
        icon = "💎",
        hint = "Explore cavern depths below Y=30 or gamble at the cauldron"
    ),
    SPLITREX(
        id = "splitrex",
        title = "Defeat The Splitrex",
        description = "A primordial multi-voxel monstrosity that splits into smaller aggressive parts when struck.",
        icon = "🦖",
        hint = "Craft a bow and enchanted pickaxe before hunting in open plains"
    ),
    PRISM_PORTAL(
        id = "prism_portal",
        title = "Build the Prism Portal",
        description = "Assemble 10 Mirrorshards to forge the doorway to The Prism dimension.",
        icon = "🌈",
        hint = "Place rift frames in a rectangular portal frame and ignite"
    ),
    HAROLD_THE_PIG(
        id = "harold",
        title = "Confront Harold the Flying Pig",
        description = "The celestial supreme boss of fortune flying above the Prism clouds.",
        icon = "🐷",
        hint = "Step into The Prism and use launching carrots to reach Harold"
    )
}
