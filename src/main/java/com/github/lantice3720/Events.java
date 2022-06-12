package com.github.lantice3720;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

import java.util.logging.Level;

public class Events implements Listener {

    Plugin plugin;

    public Events(Plugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e) {
        // 수정모드의 GUI 창인지 확인
        if (e.getView().getTitle().contains("" + ChatColor.DARK_RED)) {
            for (GUI iter : TyverGUI.guiHashMap.values()) {
                // 위의 if와 비슷하지만 iter 돌리는 부분에서의 if이기에 삭제가 불가능. 위의 if는 최적화 때문(for 안 돌리도록)
                if (e.getView().getTitle().contains(iter.getDisplayName() + ChatColor.DARK_RED)) {
                    // 수정모드의 GUI 닫음
                    iter.setInventory(e.getInventory());

                    TyverGUI.guiHashMap.put(iter.getName(), iter);

                    e.getPlayer().sendMessage("성공적으로 GUI를 저장했습니다: " + iter.getName());
                }
            }
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        // 일반 GUI 창인지 확인
        if (e.getWhoClicked() instanceof Player player) {

            if (e.getClickedInventory() != null &&  e.getClickedInventory().getType().equals(InventoryType.PLAYER)) {
                // 플레이어 인벤토리 클릭
                if (e.getView().getTitle().contains("" + ChatColor.DARK_BLUE)) {
                    // GUI 오픈인지 확인
                    e.setCancelled(true);
                }

                // 메뉴 오픈아이템 클릭인 경우
                if (e.getCurrentItem() != null && e.getCurrentItem().hasItemMeta()) {
                    String itemID = e.getCurrentItem().getItemMeta().getPersistentDataContainer().get(new NamespacedKey(plugin, "itemID"), PersistentDataType.STRING);
                    if (itemID != null && itemID.equals("menuOpener")) {
                        e.setCancelled(true);
                    }

                }
            } else if (e.getClickedInventory() != null && e.getView().getTitle().contains("" + ChatColor.DARK_BLUE)) {
                // GUI 내부 클릭
                for (GUI iter : TyverGUI.guiHashMap.values()) {
                    // GUI 찾기
                    if (e.getView().getTitle().contains(iter.getDisplayName() + ChatColor.DARK_BLUE)) {
                        if (e.getClickedInventory().getSize() != 41) {
                            // GUI 클릭
                            String command;
                            int index = 0;

                            if (iter.getCommand((e.getSlot())) != null) {
                                for (; index < iter.getCommand(e.getSlot()).size(); index++) {
                                    // 인덱스로 등록된 명령어 전부 실행
                                    command = iter.getCommand(e.getSlot()).get(index).get("command");
                                    // 커맨드 실질적 실행라인
                                    if (command != null) {

                                        if (command.contains("@clicker")) {
                                            command = command.replace("@clicker", player.getName());
                                        }

                                        if (!iter.getCommand(e.getSlot()).get(index).containsKey("runAs") || iter.getCommand(e.getSlot()).get(index).get("runAs").equals("player")) {
                                            // 플레이어 명의로 실행
                                            player.performCommand(command);
                                        } else {
                                            // 콘솔 명의로 실행
                                            Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command);
                                        }
                                    }
                                }
                            }
                        }
                        // 아래 구문이 한칸 안쪽에 있다면 플레이어 인벤토리 쪽은 사용 가능해지나, 버그 발생이 우려됨
                        e.setCancelled(true);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onToolSwap(PlayerSwapHandItemsEvent e) {
        Player player = e.getPlayer();
        NamespacedKey itemIdKey = new NamespacedKey(plugin, "itemID");
        if (e.getOffHandItem() != null && e.getOffHandItem().hasItemMeta() && e.getOffHandItem().getItemMeta().getPersistentDataContainer().has(itemIdKey, PersistentDataType.STRING) && e.getOffHandItem().getItemMeta().getPersistentDataContainer().get(itemIdKey, PersistentDataType.STRING).equals("menuOpener")) {
            // 메뉴아이템 스왑
            e.setCancelled(true);
        }

        if (player.isSneaking()) {
            // 메뉴 오픈
            String menuOption = player.getPersistentDataContainer().get(new NamespacedKey(plugin, "optionMenuOpen"), PersistentDataType.STRING);
            if (menuOption != null && menuOption.equals("sneakF")) {
                if (!TyverGUI.guiHashMap.containsKey("menu")) {
                    Bukkit.getLogger().log(Level.WARNING, "메뉴를 찾을 수 없습니다! menu 라는 이름의 GUI를 생성해주세요.");
                    return;
                }
                TyverGUI.guiHashMap.get("menu").showGUI(player, false);
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        // 플레이어 인터렉트 감지
        if (e.getAction().equals(Action.RIGHT_CLICK_AIR) || e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            // 우클릭 이벤트 감지
            if (e.getItem() != null) {
                // 빈손 아님
                String itemID = e.getItem().getItemMeta().getPersistentDataContainer().get(new NamespacedKey(plugin, "itemID"), PersistentDataType.STRING);
                if (itemID != null) {
                    // itemID 가 존재하는 아이템 우클릭
                    if (itemID.equals("menuOpener")) {
                        // 메뉴아이템 우클릭
                        if (!TyverGUI.guiHashMap.containsKey("menu")) {
                            Bukkit.getLogger().log(Level.WARNING, "메뉴를 찾을 수 없습니다! menu 라는 이름의 GUI를 생성해주세요.");
                            return;
                        }
                        TyverGUI.guiHashMap.get("menu").showGUI(e.getPlayer(), false);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onItemDrop(PlayerDropItemEvent e) {
        NamespacedKey itemIdKey = new NamespacedKey(plugin, "itemID");
        if (e.getItemDrop().getItemStack().hasItemMeta() && e.getItemDrop().getItemStack().getItemMeta().getPersistentDataContainer().has(itemIdKey, PersistentDataType.STRING)) {
            if (e.getItemDrop().getItemStack().getItemMeta().getPersistentDataContainer().get(itemIdKey, PersistentDataType.STRING).equals("menuOpener")) {
                e.setCancelled(true);
            }
        }
    }

}
