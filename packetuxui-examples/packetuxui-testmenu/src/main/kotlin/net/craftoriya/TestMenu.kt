package net.craftoriya

import com.github.shynixn.mccoroutine.folia.SuspendingJavaPlugin
import net.craftoriya.commands.OpenMenuCommand
import org.bukkit.plugin.java.JavaPlugin


internal val plugin: SuspendingJavaPlugin
    get() = JavaPlugin.getPlugin(TestMenu::class.java)

class TestMenu : SuspendingJavaPlugin() {

    override suspend fun onEnableAsync() {
        // Commands
        OpenMenuCommand
    }

}
