package code.blurone.reusableshriekers

import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.SoundCategory
import org.bukkit.block.data.type.SculkShrieker
import org.bukkit.event.Event
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.plugin.java.JavaPlugin

class ReusableShriekers : JavaPlugin(), Listener {
    private val shallLog = config.getBoolean("logger", false)
    private val reactivationItem = Material.getMaterial(config.getString("item")?.uppercase() ?: Material.ECHO_SHARD.name) ?: Material.ECHO_SHARD

    override fun onEnable() {
        // Plugin startup logic
        saveDefaultConfig()
        server.pluginManager.registerEvents(this, this)
    }

    @EventHandler
    private fun onPlayerInteract(event: PlayerInteractEvent) {
        val shrieker = event.clickedBlock ?: return
        if (event.action != Action.RIGHT_CLICK_BLOCK ||
            event.material != reactivationItem ||
            shrieker.type != Material.SCULK_SHRIEKER ||
            (shrieker.blockData as SculkShrieker).isCanSummon ||
            event.player.gameMode == GameMode.SPECTATOR) return

        event.setUseItemInHand(Event.Result.ALLOW)
        if (event.player.gameMode != GameMode.CREATIVE)
            event.item!!.amount--

        if (event.hand == EquipmentSlot.HAND)
            event.player.swingMainHand()
        else
            event.player.swingOffHand()

        val blockData = shrieker.blockData as SculkShrieker
        blockData.isCanSummon = true
        shrieker.blockData = blockData
        event.clickedBlock!!.world.playSound(
            event.clickedBlock!!.location,
            Sound.BLOCK_SCULK_CHARGE,
            SoundCategory.BLOCKS,
            2.5f,
            1.5f
        )

        if (shallLog)
            logger.info("Player ${event.player.name} has enabled shrieker at ${event.clickedBlock!!.location}")
    }
}
