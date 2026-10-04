package com.example.data

object PreloadedGames {
    val POPULAR_GAMES = listOf(
        GameApp(
            packageName = "com.dts.freefireth",
            appName = "Free Fire",
            targetFps = 90,
            resolutionScale = "100%",
            autoBoost = true,
            gameCategory = "Sinh tồn Battle Royale"
        ),
        GameApp(
            packageName = "com.garena.game.kgvn",
            appName = "Liên Quân Mobile",
            targetFps = 60,
            resolutionScale = "100%",
            autoBoost = true,
            gameCategory = "Chiến thuật MOBA 5v5"
        ),
        GameApp(
            packageName = "com.vng.pubgmobile",
            appName = "PUBG Mobile VN",
            targetFps = 90,
            resolutionScale = "75%",
            autoBoost = true,
            gameCategory = "Bắn súng sinh tồn"
        ),
        GameApp(
            packageName = "com.roblox.client",
            appName = "Roblox",
            targetFps = 60,
            resolutionScale = "100%",
            autoBoost = true,
            gameCategory = "Thế giới mở & Sandbox"
        ),
        GameApp(
            packageName = "com.miHoYo.GenshinImpact",
            appName = "Genshin Impact",
            targetFps = 60,
            resolutionScale = "75%",
            autoBoost = true,
            gameCategory = "Phiêu lưu thế giới mở"
        ),
        GameApp(
            packageName = "com.vng.codmvn",
            appName = "Call of Duty: Mobile VN",
            targetFps = 120,
            resolutionScale = "100%",
            autoBoost = true,
            gameCategory = "Bắn súng FPS đỉnh cao"
        ),
        GameApp(
            packageName = "com.haegin.playtogether",
            appName = "Play Together",
            targetFps = 60,
            resolutionScale = "100%",
            autoBoost = true,
            gameCategory = "Thế giới ảo nhiều người chơi"
        )
    )
}
