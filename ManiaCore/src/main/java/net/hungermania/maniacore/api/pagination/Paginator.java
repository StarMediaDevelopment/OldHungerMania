package net.hungermania.maniacore.api.pagination;

import net.hungermania.maniacore.api.user.User;
import net.hungermania.maniacore.api.util.ManiaUtils;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;

import java.util.*;
import java.util.Map.Entry;

public class Paginator<T extends IElement> {
    
    private final SortedMap<Integer, Page<T>> pages;
    private String header = "";
    private String footer = "";
    
    public static final String DEFAULT_HEADER = "&6List of " + DefaultVariables.TYPE.getValue() + " &e({pagenumber}/{totalpages})";
    public static final String DEFAULT_FOOTER = "&6Type /" + DefaultVariables.COMMAND.getValue() + " {nextpage} for more";
    
    public Paginator(SortedMap<Integer, Page<T>> pages) {
        this.pages = new TreeMap<>(pages);
    }
    
    public Paginator() {
        this.pages = new TreeMap<>();
    }
    
    public Collection<Page<T>> getPages() {
        return pages.values();
    }
    
    public void setHeader(String header) {
        this.header = header;
    }
    
    public void setFooter(String footer) {
        this.footer = footer;
    }
    
    public String getHeader() {
        return header;
    }
    
    public String getFooter() {
        return footer;
    }
    
    public void display(User user, int pageNumber, String... args) {
        Page<T> page = pages.get(pageNumber - 1);
        if (page == null) {
            user.sendMessage("§cThere is nothing to display.");
            return;
        }
        String header = this.header.replace("{pagenumber}", pageNumber + "");
        header = header.replace("{totalpages}", pages.size() + "");
        user.sendMessage(ManiaManiaUtils.color(header));
        for (Entry<Integer, T> element : page.getElements().entrySet()) {
            if (!StringUtils.isEmpty(element.getValue().formatLine(args))) {
                user.sendMessage(ChatColor.translateAlternateColorCodes('&', element.getValue().formatLine(args)));
            } else {
                user.sendMessage(element.getValue().formatLineAsTextComponent(args));
            }
        }
        if (!(pageNumber == pages.size())) {
            String footer = this.footer.replace("{nextpage}", (pageNumber + 1) + "");
            user.sendMessage(ManiaManiaUtils.color(footer));
        }
    }
    
    public void display(User user, String page, String... args) {
        int pageNumber;
        try {
            pageNumber = Integer.parseInt(page);
        } catch (NumberFormatException e) {
            user.sendMessage(ManiaManiaUtils.color("&cThe value for the page number is not a valid number."));
            return;
        }
        this.display(user, pageNumber, args);
    }
}