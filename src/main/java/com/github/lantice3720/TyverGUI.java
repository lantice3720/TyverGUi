package com.github.lantice3720;

import com.github.lantice3720.command.GUICommand;
import com.github.lantice3720.command.MenuCommand;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.logging.Level;

public class TyverGUI extends JavaPlugin {

    public static HashMap<String, GUI> guiHashMap = new HashMap<>();

    File guiConfigFile = new File(getDataFolder(), "guiList.yml");
    YamlConfiguration guiConfig;

    static {
        ConfigurationSerialization.registerClass(GUI.class, "GUI");
    }

    @Override
    public void onEnable() {

        // 커맨드 등록
        getCommand("gui").setExecutor(new GUICommand(this));
        getCommand("gui").setTabCompleter(new GUICommand(this));
        getCommand("menu").setExecutor(new MenuCommand(this));

        // 이벤트 등록
        getServer().getPluginManager().registerEvents(new Events(this), this);

        Bukkit.getLogger().info("Loading GUIs");

        // 데이터 입력
        guiConfig = YamlConfiguration.loadConfiguration(guiConfigFile);
        for (String key : guiConfig.getKeys(true)) {
            if (key.endsWith("_gui")) {
                // key = gui
                guiHashMap.put(((GUI) guiConfig.get(key)).getName(), (GUI) guiConfig.get(key));
            }
        }

        Bukkit.getLogger().info("Finished GUI Loading!");

        // 저장된 GUI 없음
        if(guiHashMap == null) {
            guiHashMap = new HashMap<>();
            Bukkit.getLogger().log(Level.WARNING, "guiHashMap is null. Saved GUIs might have been lost.");
        }
    }


    @Override
    public void onDisable() {
        // GUI 열어둔 플레이어들 GUI 강제 종료
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (!p.getOpenInventory().getType().equals(InventoryType.CRAFTING) && p.getOpenInventory().getTitle().contains("" + ChatColor.DARK_BLUE)) {
                // GUI 오픈상태
                p.closeInventory();
                p.sendMessage("플러그인 비활성화로 인해 화면이 꺼졌습니다. 불편을 끼쳐드려 죄송합니다.");
            }
        }
        // 데이터 출력
        guiConfig = new YamlConfiguration();

        for (GUI toSave : guiHashMap.values()) {
            // 'guis.[GUI 이름]_gui' 에 GUI 저장. 저장 방식은 버킷의 Serialize 사용.
            guiConfig.set("guis."+toSave.getName()+"_gui", toSave);
        }

        // 파일로 저장
        try {
            guiConfig.save(guiConfigFile);
        } catch (IOException e) {
            Bukkit.getLogger().log(Level.SEVERE, "Uh, cannot save gui!");
            e.printStackTrace();
        }
    }
}
