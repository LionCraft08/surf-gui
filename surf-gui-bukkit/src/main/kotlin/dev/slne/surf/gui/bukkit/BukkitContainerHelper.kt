package dev.slne.surf.gui.bukkit
//
//import dev.slne.surf.gui.nms.common.ContainerHelper
//import dev.slne.surf.gui.nms.v1_21_1.ContainerHelper1_21_1
//import dev.slne.surf.gui.nms.v1_21_4.ContainerHelper1_21_4
//import dev.slne.surf.gui.user.User
//import org.bukkit.Bukkit
//
//@Deprecated(level = DeprecationLevel.WARNING, message = "NMS usage is redundant")
//object BukkitContainerHelper {
//
//    private fun getContainerHelper(): ContainerHelper {
//        val serverVersion = Bukkit.getMinecraftVersion()
//
//        return when {
//            serverVersion.contains("1.21.1") -> ContainerHelper1_21_1
//            serverVersion.contains("1.21.4") -> ContainerHelper1_21_4
//            else -> error("Unsupported server version: $serverVersion")
//        }
//    }
//
//    fun getNextContainerId(user: User): Int {
//        return getContainerHelper().getNextContainerId(user)
//    }
//
//    fun hasOpenedContainer(user: User): Boolean {
//        return getContainerHelper().hasOpenedContainer(user)
//    }
//
//}