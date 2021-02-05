package net.hungermania.maniacore.api.chat;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.hungermania.maniacore.plugin.ManiaPlugin;
import net.hungermania.manialib.util.Priority;

@Getter
@AllArgsConstructor
public class ChatProvider {
    
    private ManiaPlugin maniaPlugin;
    private ChatHandler handler;
    private Priority priority;
}
