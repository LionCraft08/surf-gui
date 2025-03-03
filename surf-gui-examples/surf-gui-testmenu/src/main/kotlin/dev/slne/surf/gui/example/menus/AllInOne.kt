package dev.slne.surf.gui.example.menus

import com.github.retrooper.packetevents.protocol.item.ItemStack
import com.github.retrooper.packetevents.protocol.item.enchantment.type.EnchantmentTypes
import com.github.retrooper.packetevents.protocol.item.type.ItemTypes
import dev.slne.surf.gui.bukkit.extensions.toUser
import dev.slne.surf.gui.common.toComponent
import dev.slne.surf.gui.menu.button.Button
import dev.slne.surf.gui.menu.button.ButtonBuilder
import dev.slne.surf.gui.menu.button.buttons.SwitchButton
import dev.slne.surf.gui.menu.menu.MenuType
import dev.slne.surf.gui.menu.menu.menu
import dev.slne.surf.gui.util.at
import kotlinx.coroutines.delay
import org.bukkit.Bukkit
import kotlin.random.Random
import kotlin.time.Duration.Companion.seconds

class AllInOne {
    private val stone: ItemStack = ItemStack.builder().type(ItemTypes.STONE).build()
    private val air: ItemStack = ItemStack.builder().type(ItemTypes.AIR).build()
    private val updateButtons = listOf(2 at 0, 4 at 0, 6 at 0, 8 at 0, 1 at 1)

    private val greenStateButton = Button {
        item { type(ItemTypes.GREEN_WOOL) }
        onClick { (user, _, _, _, _) ->
            user.sendMessage("<green>Green state button clicked!".toComponent())
        }
    }

    private val redStateButton = Button {
        item { type(ItemTypes.RED_WOOL) }
        onClick { (user, _, _, _, _) ->
            user.sendMessage("<red>Red state button clicked!".toComponent())
        }
    }

    fun startUpdate() {
        menu.launchJob {
            while (true) {
                println("tick")
                for (player in Bukkit.getOnlinePlayers()) {
                    val user = player.toUser()

                    if (user.getActiveMenu()?.name == menu.name) {
                        for (slot in updateButtons) {
                            if (chance(20)) {
                                val item = if (chance(50)) stone else air

                                menu.updateItem(user, slot, item)
                            }
                        }
                    }
                }

                delay(1.seconds)
            }
        }
    }

    val menu = menu(MenuType.GENERIC9X4) {
        name = "<gradient:#ff6d2e:#1e90ff><bold>Feature Showcase Menu".toComponent()

        buttons(updateButtons) {
            item(stone)
            onClick { (user, _, slot, _, menu) ->
                menu.updateItem(user, slot, air)
            }
        }

        buttons where { x == 9 } build {
            itemBuilder {
                itemType = ItemTypes.GLOWSTONE
                name = "<yellow><bold>Glowing Stone".toComponent()
            }
            onClick {
                it.user.sendMessage("<green>You clicked on the glowing button!".toComponent())
                it.user.sendMessage("Button type: ${it.buttonType}".toComponent())
            }
        }

        buttons where { x == 4 } build {
            itemBuilder {
                itemType = ItemTypes.RED_WOOL
                name = "<red><bold>Red Wool".toComponent()
                lore(
                    "<#f7983e>Shiny Red Wool".toComponent(),
                    "<#f7b33e>Perfect for decoration.".toComponent()
                )
                amount = 4
                enchantment(EnchantmentTypes.FIRE_ASPECT, 2, visible = true)
            }
            onClick {
                it.user.sendMessage("<gold>Clicked on Red Wool!".toComponent())
                it.user.sendMessage("Item type: ${it.itemStack?.type?.name}".toComponent())
            }
            cooldown(4000, 1000) {
                it.user.sendMessage("<red>Cooldown active. Wait before clicking again.".toComponent())
            }
        }

        buttons whereX 8 build {
            itemBuilder {
                itemType = ItemTypes.ACACIA_SIGN
                name = "<rainbow>Cool Sign".toComponent()
                lore("<gray>Invisible enchantment here.".toComponent())
                amount = 64
                enchantment(EnchantmentTypes.UNBREAKING, 2, visible = false)
            }
            onClick {
                it.user.sendMessage("<aqua>You clicked on the Cool Sign!".toComponent())
            }
        }

        fillEmptyButtons {
            item { type(ItemTypes.BLACK_STAINED_GLASS_PANE) }
            onClick { it.user.sendMessage("<gray><italic>Decorative Tile".toComponent()) }
        }

        menuCooldown(delay = 6000, freeze = 1200) {
            it.user.sendMessage("<yellow>Menu is on cooldown!".toComponent())
        }

        switchButton(0) {
            state(greenStateButton)
            state(redStateButton)

            onStateChange { from, to ->
                println("Switched from ${from.item.type.name} to ${to.item.type.name}")
            }
        }
    }

    private fun chance(percent: Int): Boolean {
        require(percent in 0..100) { "Percentage must be between 0 and 100" }
        return Random.nextFloat() * 100 < percent
    }
}
