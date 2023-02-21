package com.mchowto.jumplift;

import java.util.logging.Logger;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class JumpLift extends JavaPlugin {

   private static final Logger LOGGER = Logger.getLogger("JumpLift");

   public void onEnable() {
      this.saveDefaultConfig();

      Configuration configuration = new Configuration();
      configuration.readConfig(this.getConfig(), LOGGER);

      PluginManager pm = this.getServer().getPluginManager();
      pm.registerEvents(new PlayerListener(configuration), this);
   }

}
