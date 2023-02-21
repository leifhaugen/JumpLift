package com.mchowto.jumplift;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

public class PlayerListener implements Listener {

   private Set<UUID> playersPreviouslyOnGround = new HashSet<UUID>();
   private final Configuration configuration;

   public PlayerListener(Configuration configuration) {
      this.configuration = configuration;
   }

   @EventHandler(priority = EventPriority.NORMAL)
   public void onPlayerToggleSneakEvent(PlayerToggleSneakEvent event) {
      Player player = event.getPlayer();
      Block currentBlock = player.getLocation().getBlock().getRelative(BlockFace.DOWN);

      if (event.isSneaking() && this.isLiftBlock(currentBlock)) {
         Block nextLiftBlock = this.findNextLiftBlock(currentBlock, BlockFace.DOWN);

         if (nextLiftBlock != null) {
            this.teleportOntoBlock(player, nextLiftBlock);
         }
      }
   }

   @EventHandler(priority = EventPriority.NORMAL)
   public void onPlayerMoveEvent(PlayerMoveEvent event) {
      Player player = event.getPlayer();
      Block currentBlock = player.getLocation().getBlock().getRelative(BlockFace.DOWN);

      Boolean isJumping = player.getVelocity().getY() > 0
         && player.getLocation().getBlock().getType() != Material.LADDER
         && this.playersPreviouslyOnGround.contains(player.getUniqueId())
         && !player.isOnGround();

      if (player.isOnGround()) {
         this.playersPreviouslyOnGround.add(player.getUniqueId());
      } else {
         this.playersPreviouslyOnGround.remove(player.getUniqueId());
      }

      if (isJumping && this.isLiftBlock(currentBlock)) {
         Block nextLiftBlock = this.findNextLiftBlock(currentBlock, BlockFace.UP);

         if (nextLiftBlock != null) {
            this.teleportOntoBlock(player, nextLiftBlock);
            this.playersPreviouslyOnGround.add(player.getUniqueId());
         }
      }
   }

   private Boolean isLiftBlock(Block block) {
      if (!this.configuration.getLiftBlocks().contains(block.getType())) {
         return false;
      }

      return this.configuration.getPowerBlocks().contains(block.getRelative(BlockFace.DOWN).getType())
         || this.configuration.getPowerBlocks().contains(block.getRelative(BlockFace.NORTH).getType())
         || this.configuration.getPowerBlocks().contains(block.getRelative(BlockFace.EAST).getType())
         || this.configuration.getPowerBlocks().contains(block.getRelative(BlockFace.SOUTH).getType())
         || this.configuration.getPowerBlocks().contains(block.getRelative(BlockFace.WEST).getType());
   }

   private Block findNextLiftBlock(Block startingBlock, BlockFace direction) {
      // Skip 2 blocks since that's what the player needs to stand
      Block iterator = startingBlock.getRelative(direction, 2);

      for(int i = 0; i < this.configuration.getMaxSearchRange() - 2; ++i) {
         iterator = iterator.getRelative(direction);
         if (this.isLiftBlock(iterator)) {
            return iterator;
         }
      }

      return null;
   }

   private void teleportOntoBlock(Player player, Block block) {
      // TODO: ensure the location is "safe" to teleport to?

      Location playerLoc = player.getLocation();
      Location targetLoc = block.getRelative(BlockFace.UP).getLocation();
      Location destination = new Location(player.getWorld(), playerLoc.getX(), targetLoc.getY(), playerLoc.getZ());

      destination.setDirection(playerLoc.getDirection());

      player.teleport(destination, TeleportCause.PLUGIN);
      player.playSound(targetLoc, Sound.ITEM_CHORUS_FRUIT_TELEPORT, SoundCategory.AMBIENT, 1.0f, 1.0f);
   }

}
