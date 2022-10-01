package gameapi.scripts;

import gameapi.scripts.AdvancedItem;

import java.beans.EventHandler;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

//To do
public class AdvancedScript {
    private String identifier;
    private LinkedHashMap<String, Object> room_properties = new LinkedHashMap<>();
    private LinkedHashMap<String, Object> player_properties = new LinkedHashMap<>();
    private List<AdvancedItem> weapons = new ArrayList<>();
    private List<EventHandler> eventHandlers = new ArrayList<>();

    public void parseScript(){

    }
}
