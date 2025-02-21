@file:Suppress("UnstableApiUsage")

package dev.slne.surf.gui.nms.v1_21_4

import io.papermc.paper.adventure.PaperAdventure
import io.papermc.paper.math.BlockPosition
import io.papermc.paper.math.Position
import net.minecraft.core.BlockPos
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.Display
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemDisplayContext
import net.minecraft.world.level.block.Block
import org.bukkit.Material
import org.bukkit.block.BlockState
import org.bukkit.block.data.BlockData
import org.bukkit.craftbukkit.block.CraftBlockState
import org.bukkit.craftbukkit.block.data.CraftBlockData
import org.bukkit.craftbukkit.entity.CraftPlayer
import org.bukkit.craftbukkit.inventory.CraftItemStack
import org.bukkit.craftbukkit.inventory.CraftItemType
import org.bukkit.craftbukkit.util.CraftMagicNumbers
import org.bukkit.entity.Display.Billboard
import org.bukkit.entity.ItemDisplay.ItemDisplayTransform
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.ItemType
import net.kyori.adventure.text.Component as AdventureComponent
import net.minecraft.world.item.ItemStack as NmsItemStack
import net.minecraft.world.level.block.state.BlockState as NmsBlockState

fun Player.toNms(): ServerPlayer = (this as CraftPlayer).handle
fun Material.toNmsBlock(): Block = CraftMagicNumbers.getBlock(this)
fun Material.toNmsItem(): Item = CraftMagicNumbers.getItem(this)
fun BlockData.toNms(): NmsBlockState = (this as CraftBlockData).state
fun BlockState.toNms(): NmsBlockState = (this as CraftBlockState).handle

fun Billboard.toNms(): Display.BillboardConstraints =
    Display.BillboardConstraints.valueOf(this.name)

fun ItemStack.toNms(): NmsItemStack = CraftItemStack.asNMSCopy(this)
fun ItemDisplayTransform.toNms(): ItemDisplayContext = ItemDisplayContext.BY_ID.apply(this.ordinal)
fun NmsItemStack.toBukkit(): ItemStack = CraftItemStack.asBukkitCopy(this)
val ItemType.nms: Item get() = CraftItemType.bukkitToMinecraftNew(this)
fun BlockPosition.toNms(): BlockPos = BlockPos(blockX(), blockY(), blockZ())

fun BlockPos.toBukkit(): BlockPosition = Position.block(x, y, z)

fun Array<Component>.toBukkit() = this.map { it.toBukkit() }
fun AdventureComponent.toNms(): Component = PaperAdventure.asVanilla(this)
fun Component.toBukkit(): AdventureComponent = PaperAdventure.asAdventure(this)