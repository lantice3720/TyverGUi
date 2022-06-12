package com.github.lantice3720;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;

/**
 * GUI 클래스. 이 플러그인의 목적이기도 합니다.
 */
@SerializableAs("GUI")
public class GUI implements ConfigurationSerializable {
    private final String name;
    private String displayName;
    private Inventory inventory;
    private HashMap<Integer, ArrayList<HashMap<String, String>>> command; // <슬롯, 리스트<해시맵<인덱스, 명령어>>> get 할 때 slot, index, command|permission 순번임

    { command = new HashMap<>(); }

    // 클래스 생성자
    public GUI(String name) {
        this.name = name;
        this.displayName = name;
        this.inventory = Bukkit.createInventory(null, 36);
    }

    public GUI(String name, Integer lines) {
        this.name = name;
        this.displayName = name;
        this.inventory = Bukkit.createInventory(null, lines*9);
    }

    public GUI(String name, String displayName) {
        this.name = name;
        this.displayName = displayName;
        this.inventory = Bukkit.createInventory(null, 36);
    }

    public GUI(String name, String displayName, Integer lines) {
        this.name = name;
        this.displayName = displayName;
        this.inventory = Bukkit.createInventory(null, lines*9);
    }

    public GUI(String name, Inventory inventory) {
        this.name = name;
        this.displayName = name;
        this.inventory = inventory;
    }

    public GUI(String name, String displayName, Inventory inventory) {
        this.name = name;
        this.displayName = displayName;
        this.inventory = inventory;
    }

    public GUI(String name, String displayName, Inventory inventory, HashMap<Integer, ArrayList<HashMap<String, String>>> command) {
        this.name = name;
        this.displayName = displayName;
        this.inventory = inventory;
        this.command = command;
    }


    /**
     * GUI의 이름을 불러옵니다.
     * 
     * @return
     * GUI의 이름
     */
    public String getName() {
        return this.name;
    }

    /**
     * GUI의 인벤토리를 불러옵니다.
     *
     * @return
     * GUI의 인벤토리
     */
    public Inventory getInventory() {
        return this.inventory;
    }

    /**
     * GUI의 인벤토리를 재설정합니다.
     *
     * @param inventory
     * 재설정할 값
     */
    public void setInventory(Inventory inventory) {
        this.inventory = inventory;
    }
    
    /**
     * 인벤토리의 아이템을 불러옵니다.
     *
     * @param index
     * 인벤토리 인덱스
     * @return
     * 인벤토리의 아이템
     */
    public ItemStack getItem(Integer index) {
        return inventory.getItem(index);
    }
    
    /**
     * 인벤토리의 아이템을 재설정합니다.
     *
     * @param item
     * 인벤토리에 넣을 아이템
     * @param index
     * 인벤토리 인덱스
     */
    public void setItem(ItemStack item, Integer index) {
        if(index+1 > inventory.getSize()) { // IndexOutOfBound 방지
            Bukkit.getLogger().log(Level.SEVERE, "[LanGUI] GUI 아이템 설정 오류. 인덱스가 GUI 크기를 벗어납니다.");
            return;
        }
        inventory.setItem(index, item);
    }

    /**
     * GUI창의 이름을 불러옵니다.
     * 
     * @return displayName
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * GUI창의 이름을 설정합니다.
     * 
     * @param displayName displayName
     */
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    /**
     * GUI 의 슬롯 클릭 명령어를 불러옵니다.
     *
     * @param slot 명령어가 발동될 슬롯
     * @return 명령어 목록
     */
    public ArrayList<HashMap<String, String>> getCommand(Integer slot) {
        return command.get(slot);
    }

    /**
     * GUI 전체의 클릭 명령어를 불러옵니다.
     *
     * @return 명령어 목록
     */
    public HashMap<Integer, ArrayList<HashMap<String, String>>> getCommand() {
        return command;
    }

    /**
     * GUI 클릭 명령어를 설정합니다.
     *
     * @param slot 명령어가 발동될 슬롯
     * @param command 명령어
     */
    public void setCommand(Integer slot, String command) {
        ArrayList<HashMap<String, String>> commandArr = new ArrayList<>();
        HashMap<String, String> commandHashM = new HashMap<>();
        commandHashM.put("command", command);
        commandArr.add(commandHashM);
        this.command.put(slot, commandArr);
    }

    /**
     * GUI 클릭 명령어를 설정합니다.
     *
     * @param slot 명령어가 발동될 슬롯
     * @param command 명령어
     */
    public void setCommand(Integer slot, ArrayList<HashMap<String, String>> command) {
        this.command.put(slot, command);
    }

    /**
     * GUI 클릭 명령어를 설정합니다
     *
     * @param slot 명령어가 발동될 슬롯
     * @param index 명령어 발동 순번
     * @param command 명령어
     */
    public void setCommand(Integer slot, Integer index, String command) {
        HashMap<String, String> commandHashMap = new HashMap<>();
        commandHashMap.put("command", command);
        if (this.command.containsKey(slot) && index >= 0) {
            if (index >= this.command.get(slot).size()) {
                this.command.get(slot).add(commandHashMap);
            } else {
                this.command.get(slot).set(index, commandHashMap);
            }
        } else {
            setCommand(slot, command);
        }
    }

    /**
     * GUI 명령어를 삭제합니다.
     *
     * @param slot 명령어가 발동될 슬롯
     */
    public void removeCommand(Integer slot) {
        command.remove(slot);
    }

    /**
     * GUI 명령어를 삭제합니다.
     *
     * @param slot 명령어가 발동될 슬롯
     */
    public void removeCommand(Integer slot, int index) {
        if (command.containsKey(slot) && index >= 0 && index < command.get(slot).size()) {
            command.get(slot).remove(index);
        }
    }

    /**
     * 플레이어에게 GUI를 보여줍니다.
     * 
     * @param player 플레이어
     */
    public void showGUI(Player player, Boolean editMode) {
        // toOpen 새로 생성해서 displayName 적용 -> 기존 Inventory 객체에 setTitle()이 불가능함..
        Inventory toOpen;

        // 인벤토리 이벤트 감지용으로 더미 컬러코드 추가
        if (editMode) {
            toOpen = Bukkit.createInventory(null, inventory.getSize(), displayName + ChatColor.DARK_RED);
        } else {
            toOpen = Bukkit.createInventory(null, inventory.getSize(), displayName + ChatColor.DARK_BLUE);
        }
        toOpen.setContents(inventory.getContents());

        player.openInventory(toOpen);
    }

    /**
     * 객체를 맵으로 변형해줍니다. 자동 호출됩니다.
     *
     * @return 객체의 정보가 담긴 맵
     */
    public Map<String, Object> serialize() {
        HashMap<String, Object> result = new LinkedHashMap();
        result.put("name", this.getName());
        result.put("display_name", this.displayName);
        result.put("inventory_contents", this.inventory.getContents());
        result.put("inventory_line", this.inventory.getSize() / 9);
        result.put("command", this.command);

        return result;
    }

    /**
     * 맵을 객체로 재변환해줍니다. 자동 호출됩니다.
     *
     * @param map 변환할 맵
     * @return GUI 객체
     */
    public static GUI deserialize(Map<String, Object> map) {
        String name;
        String displayName;
        ArrayList<ItemStack> inventoryContents;
        Integer inventoryLine;
        Inventory inventory;
        HashMap<Integer, ArrayList<HashMap<String, String>>> command;

        if (map.containsKey("name")) {
            name = (String) map.get("name");
        } else {
            name = "GUi name is broken. Please report to author.";
            Bukkit.getLogger().log(Level.SEVERE, "[LanGUI] Error occurred while deserializing GUI!");
        }

        if (map.containsKey("display_name")) {
            displayName = (String) map.get("display_name");
        } else {
            displayName = name;
        }

        if (map.containsKey("inventory_contents")) {
            inventoryContents = (ArrayList<ItemStack>) map.get("inventory_contents");
        } else {
            inventoryContents = new ArrayList<>();
        }

        if (map.containsKey("inventory_line")) {
            inventoryLine = (Integer) map.get("inventory_line");
        } else {
            inventoryLine = 1;
        }

        if (map.containsKey("command")) {
            command = (HashMap<Integer, ArrayList<HashMap<String, String>>>) map.get("command");
        } else {
            command = new HashMap<>();
        }

        inventory = Bukkit.createInventory(null, inventoryLine*9);
        inventory.setContents(inventoryContents.toArray(new ItemStack[0]));

        return new GUI(name, displayName, inventory, command);
    }
}
