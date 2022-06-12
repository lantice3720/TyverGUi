package com.github.lantice3720.command;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.logging.Level;

public class MenuCommand implements CommandExecutor {

    Plugin plugin;

    public MenuCommand(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (sender instanceof Player player) {
            switch (args[0]) {
                case "openOption":
                    if (args.length == 1) {
                        // 메뉴옵션 토글기능
                        NamespacedKey optionMenuOpenKey = new NamespacedKey(plugin, "optionMenuOpen");
                        NamespacedKey itemIDKey = new NamespacedKey(plugin, "itemID");
                        String menuOption = player.getPersistentDataContainer().get(optionMenuOpenKey, PersistentDataType.STRING);
                        if (menuOption == null || menuOption.equals("toolbarMenuItem")) {
                            // 메뉴옵션이 없거나 툴바옵션
                            
                            // 아이템이 존재하지 않는 오류
                            if (player.getInventory().getItem(8) != null && player.getInventory().getItem(8).hasItemMeta() && player.getInventory().getItem(8).getItemMeta().getPersistentDataContainer().get(itemIDKey, PersistentDataType.STRING).equals("menuOpener"))  {
                                player.getInventory().setItem(8, new ItemStack(Material.AIR));
                            } else {
                                Bukkit.getLogger().log(Level.WARNING, "Player doesn't have menu item!");
                            }

                            player.getPersistentDataContainer().set(optionMenuOpenKey, PersistentDataType.STRING, "sneakF");
                            return true;
                        } else if (menuOption.equals("sneakF")) {
                            // 쉬스왑옵션
                            
                            if (player.getInventory().getItem(8) != null) {
                                // 아이템 소실 방지
                                player.sendMessage(ChatColor.RED + "툴바 마지막 칸이 비어있어야 전환이 가능합니다.");
                                return true;
                            }

                            // 메뉴아이템 생성
                            ItemStack menuOpener = new ItemStack(Material.BOOK);
                            ItemMeta meta = menuOpener.getItemMeta();
                            meta.getPersistentDataContainer().set(itemIDKey, PersistentDataType.STRING, "menuOpener");
                            TextComponent component = Component.text("메뉴").color(NamedTextColor.GREEN);
                            meta.displayName(component);

                            menuOpener.setItemMeta(meta);

                            // 아이템 지급
                            player.getInventory().setItem(8, menuOpener);
                            player.getPersistentDataContainer().set(optionMenuOpenKey, PersistentDataType.STRING, "toolbarMenuItem");
                            return true;
                        }
                    }
                    break;
            }

        }
        return false;
    }
}
