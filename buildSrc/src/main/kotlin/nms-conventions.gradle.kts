plugins {
    `java-library`
    id("io.papermc.paperweight.userdev")
}

paperweight.reobfArtifactConfiguration =
    io.papermc.paperweight.userdev.ReobfArtifactConfiguration.MOJANG_PRODUCTION