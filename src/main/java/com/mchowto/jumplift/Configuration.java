package com.mchowto.jumplift;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

public class Configuration {

   private List<Material> liftBlocks;
   private List<Material> powerBlocks;
   private int maxSearchRange;

   public List<Material> getLiftBlocks() {
      return this.liftBlocks;
   }

   public List<Material> getPowerBlocks() {
      return this.powerBlocks;
   }

   public int getMaxSearchRange() {
      return this.maxSearchRange;
   }

   public void readConfig(FileConfiguration fileConfig, Logger logger) {
      this.liftBlocks = this.readMaterials("liftBlocks", Arrays.asList(new Material[] { Material.EMERALD_BLOCK }), fileConfig, logger);
      this.powerBlocks = this.readMaterials("powerBlocks", Arrays.asList(new Material[] { Material.REDSTONE_BLOCK }), fileConfig, logger);

      this.maxSearchRange = fileConfig.getInt("maxSearchRange", 10);
      logger.info(String.format("Setting 'maxSearchRange' to '%s'", this.maxSearchRange));
   }

   private List<Material> readMaterials(String key, List<Material> defaultMaterials, FileConfiguration fileConfig, Logger logger) {
      List<String> materialKeys = fileConfig.getStringList(key);
      List<Material> materials = new ArrayList<Material>();

      if (materialKeys.isEmpty()) {
         logger.info(String.format("'%s' is empty, defaulting to '%s'", key, this.stringifyMaterialList(defaultMaterials)));
         return defaultMaterials;
      }

      for (String materialKey : materialKeys) {
         Material material = Material.matchMaterial(materialKey);

         if (material == null) {
            logger.warning(String.format("Option '%s' contains '%s' which is an unknown material. Ignoring material."));
         } else {
            materials.add(material);
         }
      }

      logger.info(String.format("Setting '%s' to '%s'", key, this.stringifyMaterialList(materials)));

      return materials;
   }

   private String stringifyMaterialList(List<Material> materials) {
      List<String> keys = new ArrayList<String>();

      for (Material material : materials) {
         keys.add(material.getKey().toString());
      }

      return String.join(",", keys);
   }

}
