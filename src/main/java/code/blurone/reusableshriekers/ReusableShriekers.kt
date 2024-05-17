package code.blurone.reusableshriekers

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
        if (event.action != Action.RIGHT_CLICK_BLOCK ||
            event.material != reactivationItem ||
            event.clickedBlock?.type != Material.SCULK_SHRIEKER ||
            (event.clickedBlock!!.blockData as SculkShrieker).isCanSummon) return

        event.setUseItemInHand(Event.Result.ALLOW)
        event.item!!.amount--

        if (event.hand == EquipmentSlot.HAND)
            event.player.swingMainHand()
        else
            event.player.swingOffHand()

        (event.clickedBlock!!.blockData as SculkShrieker).isCanSummon = true
        event.clickedBlock!!.world.playSound(
            event.clickedBlock!!.location,
            Sound.BLOCK_SCULK_CHARGE,
            SoundCategory.BLOCKS,
            1f,
            1.5f
        )

        if (shallLog)
            logger.info("Player ${event.player.name} has enabled shrieker at ${event.clickedBlock!!.location}")
    }
}
