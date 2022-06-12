package com.github.lantice3720.command;

import com.github.lantice3720.Fx;
import com.github.lantice3720.GUI;
import com.github.lantice3720.TyverGUI;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.*;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;

public class GUICommand implements CommandExecutor, TabCompleter {

    Plugin plugin;

    public GUICommand(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player player;
        // 입력자 설정
        if (sender instanceof Player) {
            player = (Player) sender;
        } else if (args.length == 3 && args[0].equals("open")) {
            player = Bukkit.getPlayer(args[2]);
        } else {
            // cmd 입력
            return false;
        }

        if (args.length == 0) { // 매개변수 없음
            return false;
        }
        // 매개변수 1개 이상
        switch (args[0]) {
            case "open": // gui open
                // 매개변수 개수 예외처리
                if (args.length == 1) {
                    // 매개변수 1개
                    return false;
                } else if (args.length == 3) {
                    // gui open <guiName> <player>
                    player = Bukkit.getPlayer(args[2]);
                } else if (args.length != 2) {
                    // 매개변수 수가 3개 초과
                    return false;
                }

                // gui open <guiName>
                GUI toShow = TyverGUI.guiHashMap.get(args[1]);

                if (toShow == null) {
                    sender.sendMessage("GUI 를 찾을 수 없습니다!");
                    return true;
                }
                toShow.showGUI(player, false);
                return true;
            case "close":
                if (args.length == 2 && Bukkit.getPlayer(args[1]) != null) {
                    player = Bukkit.getPlayer(args[1]);
                    player.closeInventory();
                    sender.sendMessage(player.getName() + " 에게 열려있는 인벤토리를 모두 닫았습니다.");
                    return true;
                }
                return false;
            case "set":
                // 매개변수 개수 2개이상 체크
                if (args.length >= 2) {
                    GUI toSet = TyverGUI.guiHashMap.get(args[1]);
                    if (args.length == 2) {
                        // gui set <name> 으로, gui 편집모드 시작
                        toSet.showGUI(player, true);
                        return true;
                    } else if (args.length == 4 && args[2].equals("displayname")) {
                        // gui set <name> displayname <displayname>
                        toSet.setDisplayName(args[3]);
                        return true;
                    } else if (args.length >= 5 && args[2].equals("command") && Fx.isNumeric(args[3]) && toSet.getInventory().getSize() > Integer.parseInt(args[3])) {
                        // gui set <name> command <slot> <command>
                        Integer slot = Integer.valueOf(args[3]);
                        int index = 0;

                        if (toSet.getCommand(slot) == null) {
                            HashMap<String, String> commandMap = new HashMap<>();
                            ArrayList<HashMap<String, String>> commandArray = new ArrayList<>();
                            commandMap.put("command", null);
                            commandArray.add(commandMap);
                            toSet.getCommand().put(slot, commandArray);
                        }

                        int i;
                        for (i = 4; i < args.length; i++) {
                            if (!args[i].startsWith("-")) {
                                break;
                            }
                            if (args[i].startsWith("-index(") && args[i].endsWith(")") && Fx.isNumeric(args[i].substring(7, args[i].length() - 1))) {
                                index = Integer.parseInt(args[i].substring(7, args[i].length() - 1));

                                if (index < 0) {
                                    Bukkit.getLogger().log(Level.WARNING, "index couldn't be negative! command user: " + sender.getName());
                                    sender.sendMessage("인덱스는 음수가 될 수 없습니다.");
                                    return true;
                                }
                            } else if (args[i].equals("-remove")) {
                                // 커맨드 삭제
                                toSet.removeCommand(slot, index);

                                sender.sendMessage("성공적으로 GUI 명령어를 삭제했습니다!");
                                return true;
                            } else if (args[i].equals("-console")) {
                                // 콘솔/유저 실행자 토글
                                if (!toSet.getCommand(slot).get(index).containsKey("runAs") || toSet.getCommand(slot).get(index).get("runAs").equals("clicker")) {
                                    toSet.getCommand(slot).get(index).put("runAs", "console");
                                } else if (toSet.getCommand(slot).get(index).get("runAs").equals("console")) {
                                    toSet.getCommand(slot).get(index).put("runAs", "player");
                                } else {
                                    toSet.getCommand(slot).get(index).put("runAs", "player");
                                    Bukkit.getLogger().log(Level.WARNING, "Wrong value in runAs. set to player.");
                                }
                            } else if (args[i].equals("-get")) {
                                if (toSet.getCommand(slot).size() == 0) {
                                    sender.sendMessage("명령어가 없습니다!");
                                    return true;
                                }
                                StringBuilder stringBuilder = new StringBuilder(toSet.getName() + " 슬롯 " + slot + " 의 명령어\n");
                                for(int indexI = 0; indexI < toSet.getCommand(slot).size(); indexI++) {
                                    stringBuilder.append(indexI).append(". ").append(toSet.getCommand(slot).get(indexI).get("command")).append("\n");
                                }

                                sender.sendMessage(stringBuilder.toString());
                                return true;
                            }
                        }

                        StringBuilder stringBuilder = new StringBuilder();
                        for (; i < args.length; i++) {
                            // 매개변수 iter 돌리면서 append
                            stringBuilder.append(args[i]);
                            if (i != args.length - 1) {
                                stringBuilder.append(" ");
                            }
                        }
                        toSet.setCommand(slot, index, stringBuilder.toString());

                        sender.sendMessage("성공적으로 GUI 명령어를 등록했습니다: " + stringBuilder);
                        return true;
                    }


                }
                return false;
            case "list":
                // 매개변수가 여러개일 경우 예외처리
                if (args.length != 1) {
                    return false;
                }

                TextComponent listBuilder = new TextComponent("GUI " + TyverGUI.guiHashMap.size() + "개\n");

                // GUI 목록 상태 확인
                if (TyverGUI.guiHashMap.isEmpty()) {
                    sender.sendMessage("GUI 목록이 비어있습니다!");
                    return true;
                }
                // iter 돌리면서 컴포넌트 생성
                int loopCount = 1;
                for (GUI iter : TyverGUI.guiHashMap.values()) {
                    TextComponent listValue = new TextComponent(iter.getName());
                    listValue.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(iter.getDisplayName()).create()));
                    listValue.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/gui open " + iter.getName()));
                    // 쉼표 추가
                    if (loopCount < TyverGUI.guiHashMap.size()) {
                        listValue.addExtra(", ");
                    }

                    listBuilder.addExtra(listValue);
                    loopCount++;
                }


                sender.sendMessage(listBuilder);
                return true;
            case "create":
                // gui create
                if (args.length == 1) {
                    return false;
                }

                // gui create <name>
                // 겹치는 이름 확인
                if (!TyverGUI.guiHashMap.isEmpty()) {
                    if (TyverGUI.guiHashMap.containsKey(args[1])) {
                        return false;
                    }
                }

                if (args.length == 3) { // gui create <name> <slots>
                    // slot 예외처리
                    if (!Fx.isNumeric(args[2]) || Integer.parseInt(args[2]) > 6) {
                        return false;
                    }

                    GUI newGUI = new GUI(args[1], Integer.parseInt(args[2]));
                    TyverGUI.guiHashMap.put(newGUI.getName(), newGUI);
                    sender.sendMessage("성공적으로 GUI를 생성했습니다: \"" + args[1] + "\"");
                    return true;
                } else if (args.length > 3) {
                    // 매개변수 4개 이상일 시 에러
                    return false;
                }

                // gui create <name> 뒤 매개변수 없음
                GUI newGUI = new GUI(args[1]);
                TyverGUI.guiHashMap.put(newGUI.getName(), newGUI);
                sender.sendMessage("성공적으로 GUI를 생성했습니다: \"" + args[1] + "\"");
                return true;
            case "delete":
                if (args.length == 2 && TyverGUI.guiHashMap.get(args[1]) != null) {
                    // 메세지 구성
                    TextComponent message = new TextComponent("정말로 '" + args[1] + "' 을(를) 삭제할까요? 이 작업은 취소가 불가능합니다.  ");
                    TextComponent confirmButton = new TextComponent("[확인]");
                    confirmButton.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("GUI 삭제")));
                    confirmButton.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/gui delete " + args[1] + " force"));
                    confirmButton.setColor(ChatColor.GREEN);

                    message.addExtra(confirmButton);
                    sender.sendMessage(message);
                    return true;
                } else if (args.length == 3 && args[2].equals("force") && TyverGUI.guiHashMap.get(args[1]) != null) {
                    TyverGUI.guiHashMap.remove(args[1]);
                    sender.sendMessage(args[1] + " 을(를) 성공적으로 삭제했습니다.");
                    return true;
                }
                return false;
            default: // args[0] 이 알 수 없는 문자열
                return false;
        }
    }

    // 탭 컴플리트
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        ArrayList<String> toReturn = new ArrayList<>();
        if (args.length == 1) {
            // gui만 입력한 경우
            toReturn.add("create");
            toReturn.add("delete");
            toReturn.add("open");
            toReturn.add("close");
            toReturn.add("set");
            toReturn.add("list");
        } else {
            switch (args[0]) {
                case "create":
                    // gui create
                    break;
                case "delete":
                    // gui delete
                    // GUI 목록 리턴
                    if (args.length == 2) toReturn.addAll(TyverGUI.guiHashMap.keySet());
                    break;
                case "open":
                    // gui open
                    if (args.length == 2) {
                        // gui open
                        // GUI 목록 리턴
                        toReturn.addAll(TyverGUI.guiHashMap.keySet());
                    } else if (args.length == 2 && TyverGUI.guiHashMap.get(args[1]) != null) {
                        // gui open <guiName>
                        for (Player player : Bukkit.getOnlinePlayers()) {
                            // 플레이어 목록 리턴
                            toReturn.add(player.getName());
                        }
                    }
                    break;
                case "close":
                    // gui close
                    if (args.length == 2) {
                        // 플레이어 목록 리턴
                        for (Player player : Bukkit.getOnlinePlayers()) {
                            // 플레이어 목록 리턴
                            toReturn.add(player.getName());
                        }
                    }
                    break;
                case "set":
                    // gui set
                    if (args.length == 2) {
                        // gui set
                        // GUI 목록 리턴
                        toReturn.addAll(TyverGUI.guiHashMap.keySet());
                    } else if (args.length == 3 && TyverGUI.guiHashMap.get(args[1]) != null) {
                        // gui set <guiName>
                        // displayname 과 comamnd 리턴
                        toReturn.add("displayname");
                        toReturn.add("command");
                    } else if (args.length == 4 && args[2].equals("command") && TyverGUI.guiHashMap.get(args[1]) != null) {
                        // gui set <guiName> command
                        Integer i = 0;
                        for (i = 0; i < TyverGUI.guiHashMap.get(args[1]).getInventory().getSize(); i++) {
                            toReturn.add(String.valueOf(i));
                        }
                    } else if (args.length >= 5 && args[2].equals("command") && TyverGUI.guiHashMap.get(args[1]) != null) {
                        if (!(args[args.length-1].startsWith("-") || args[args.length-1].isEmpty())) {
                            return toReturn;
                        }
                        // gui set <guiName> command <slot>
                        // remove 와 console 옵션 리턴
                        toReturn.add("-remove");
                        toReturn.add("-console");
                        toReturn.add("-index()");
                        toReturn.add("-get");
                        for (int i = 4; i < args.length; i++) {
                            toReturn.remove(args[i]);
                            if (args[i].startsWith("-index(") && args[i].endsWith(")")) {
                                toReturn.remove("-index()");
                            }
                        }
                    }
                    break;
                default:
                    break;
            }
        }
        return toReturn;
    }
}
