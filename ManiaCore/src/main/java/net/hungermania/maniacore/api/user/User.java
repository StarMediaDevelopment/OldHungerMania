package net.hungermania.maniacore.api.user;

import lombok.Getter;
import lombok.Setter;
import net.hungermania.maniacore.api.ManiaCore;
import net.hungermania.maniacore.api.channel.Channel;
import net.hungermania.maniacore.api.leveling.Level;
import net.hungermania.maniacore.api.nickname.Nickname;
import net.hungermania.maniacore.api.ranks.Rank;
import net.hungermania.maniacore.api.records.IgnoreInfoRecord;
import net.hungermania.maniacore.api.skin.Skin;
import net.hungermania.maniacore.api.stats.Stat;
import net.hungermania.maniacore.api.stats.Statistic;
import net.hungermania.maniacore.api.stats.Stats;
import net.hungermania.maniacore.api.user.toggle.Toggle;
import net.hungermania.maniacore.api.user.toggle.Toggles;
import net.hungermania.manialib.data.model.IRecord;
import net.hungermania.manialib.util.Pair;
import net.md_5.bungee.api.chat.BaseComponent;

import java.util.*;

@Getter
@Setter
public class User implements IRecord {
    private static final UUID FIRESTAR311 = UUID.fromString("3f7891ce-5a73-4d52-a2ba-299839053fdc");
    private static final UUID ASSASSINPLAYS = UUID.fromString("c292df56-5baa-4a11-87a3-cba08ce5f7a6");
    protected static final Set<Stats> FAKED_STATS = new HashSet<>(Arrays.asList(Stats.EXPERIENCE, Stats.COINS, Stats.HG_CHESTS_FOUND, Stats.HG_SCORE, Stats.HG_KILLS, Stats.HG_WINS, Stats.HG_DEATHMATCHES, Stats.HG_DEATHS, Stats.HG_MUTANT_DEATHS, Stats.HG_GAMES, Stats.HG_HIGHEST_KILL_STREAK, Stats.HG_MUTANT_KILLS, Stats.HG_WINSTREAK));

    protected int id = 0;
    protected UUID uniqueId;
    protected String name;
    protected Channel channel = Channel.GLOBAL; //TODO Type handler (Probably just a default enum handler
    protected Set<IgnoreInfo> ignoredPlayers = new HashSet<>(); //TODO Set type handlers
    protected Rank rank = Rank.DEFAULT; //TODO Type handler
    protected Nickname nickname;

    protected Map<String, Statistic> stats = new HashMap<>();
    protected Map<Toggles, Toggle> toggles = new HashMap<>();

    protected Map<String, Statistic> fakeStats = new HashMap<>();

    public User(UUID uniqueId) {
        this.uniqueId = uniqueId;
        this.nickname = new Nickname(uniqueId);
    }

    public void applyNickname() {
        generateFakeStats();
    }

    public Nickname getNickname() {
        if (nickname == null) {
            nickname = new Nickname(this.uniqueId);
        }
        return nickname;
    }

    public void resetNickname() {
        this.fakeStats.clear();
    }

    public User(UUID uniqueId, String name) {
        this.uniqueId = uniqueId;
        this.name = name;
        this.nickname = new Nickname(uniqueId);
    }

    public User(Map<String, String> jedisData) {
        this.id = Integer.parseInt(jedisData.get("id"));
        this.uniqueId = UUID.fromString(jedisData.get("uniqueId"));
        this.name = jedisData.get("name");
        this.rank = Rank.valueOf(jedisData.get("rank"));
        this.nickname = new Nickname(uniqueId);
    }

    public boolean isOnline() {
        return false;
    }

    public void setStats(Map<String, Statistic> stats) {
        if (stats == null) {
            this.stats = new HashMap<>();
        } else {
            this.stats = stats;
        }
    }

    public Statistic getFakedStat(Stat stat) {
        if (fakeStats.isEmpty()) {
            generateFakeStats();
        }
        Statistic s = null;
        try {
            s = fakeStats.get(stat.getName());
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
        if (s != null) {
            if (!this.fakeStats.containsKey(stat.getName())) {
                this.fakeStats.put(stat.getName(), s);
            }
        } else {
            if (FAKED_STATS.contains(stat)) {
                generateFakeStats();
                s = fakeStats.get(stat.getName());
            }
        }
        return s;
    }

    private void generateFakeStats() {
        Random random = new Random();
        for (Stats fakedStat : FAKED_STATS) {
            if (fakedStat == Stats.HG_SCORE) {
                Statistic value = fakedStat.create(uniqueId);
                value.setValue(random.nextInt(90) + 10);
                this.fakeStats.put(fakedStat.getName(), value);
            } else {
                Statistic stat = getStat(fakedStat);
                Statistic value = fakedStat.create(uniqueId);
                if (stat.getAsInt() != 0) {
                    value.setValue(random.nextInt(stat.getAsInt()));
                } else {
                    value.setValue(0);
                }
                this.fakeStats.put(value.getName(), value);
            }
        }
    }

    public void setToggles(Map<Toggles, Toggle> toggles) {
        if (toggles == null) {
            this.toggles = new HashMap<>();
        } else {
            this.toggles = toggles;
        }
    }

    public Toggle getToggle(Toggles type) {
        return this.toggles.get(type);
    }

    public void incrementStat(Stat stat) {
        Statistic statistic = this.stats.getOrDefault(stat.getName(), stat.create(this.getUniqueId()));
        statistic.increment();
        if (!this.stats.containsKey(stat.getName())) {
            this.stats.put(stat.getName(), statistic);
        }
        if (this.fakeStats.containsKey(stat.getName())) {
            this.fakeStats.get(stat.getName()).increment();
        }
    }

    public User(int id, UUID uniqueId, String name, Rank rank, Channel channel) {
        this.id = id;
        this.uniqueId = uniqueId;
        this.name = name;
        if (rank == null) {
            this.rank = Rank.DEFAULT;
        } else {
            this.rank = rank;
        }
        this.channel = channel;
    }

    public void addNetworkExperience(int exp) {
        Statistic stat = getStat(Stats.EXPERIENCE);
        Level current = ManiaCore.getInstance().getLevelManager().getLevel(stat.getAsInt());
        stat.setValue((stat.getAsInt() + exp) + "");
        Level newLevel = ManiaCore.getInstance().getLevelManager().getLevel(stat.getAsInt());
        if (current.getNumber() < newLevel.getNumber()) {
            sendMessage("&a&lLevel Up! " + current.getNumber() + " -> " + newLevel.getNumber());
            sendMessage("  &7&oThis message is temporary");
        }
    }

    public void setIgnoredPlayers(Set<IgnoreInfo> ignoredPlayers) {
        this.ignoredPlayers = ignoredPlayers;
    }

    public Channel getChannel() {
        return (channel != null) ? channel : Channel.GLOBAL;
    }

    public Set<IgnoreInfo> getIgnoredPlayers() {
        return new HashSet<>(ignoredPlayers);
    }

    public Pair<Integer, String> addCoins(int coins, boolean coinMultiplier) {
        Statistic stat = getStat(Stats.COINS);
        double multiplier = 1;
        String multiplierString = "";
        if (coinMultiplier) {
            if (rank.getCoinMultiplier() > 1) {
                multiplier = rank.getCoinMultiplier();
                multiplierString = rank.getBaseColor() + "&lx" + rank.getCoinMultiplier() + " " + rank.getName().toUpperCase() + " BONUS";
            }
        }
        int totalCoins = (int) Math.round(coins * multiplier);
        stat.setValue((stat.getAsInt() + totalCoins) + "");
        return new Pair<>(coins, multiplierString);
    }

    public IgnoreResult addIgnoredPlayer(User user) {
        for (IgnoreInfo ignoredPlayer : this.ignoredPlayers) {
            if (ignoredPlayer.getIgnored().equals(user.getUniqueId())) {
                return IgnoreResult.ALREADY_ADDED;
            }
        }

        if (user.hasPermission(Rank.HELPER)) {
            return IgnoreResult.PLAYER_IS_STAFF;
        }

        IgnoreInfo ignoreInfo = new IgnoreInfo(this.getUniqueId(), user.getUniqueId(), System.currentTimeMillis(), user.getName());
        this.ignoredPlayers.add(ignoreInfo);
        ManiaCore.getInstance().getDatabase().pushRecord(new IgnoreInfoRecord(ignoreInfo));
        return IgnoreResult.SUCCESS;
    }

    public IgnoreResult removeIgnoredPlayer(User user) {
        boolean ignored = false;
        for (IgnoreInfo ignoredPlayer : this.ignoredPlayers) {
            if (ignoredPlayer.getIgnored().equals(user.getUniqueId())) {
                ignored = true;
                break;
            }
        }

        if (!ignored) {
            return IgnoreResult.NOT_IGNORED;
        }

        IgnoreInfo ignoreInfo = null;
        for (IgnoreInfo ignoredPlayer : this.ignoredPlayers) {
            if (ignoredPlayer.getIgnored().equals(user.getUniqueId())) {
                ignoreInfo = ignoredPlayer;
                break;
            }
        }
        if (ignoreInfo != null) {
            this.ignoredPlayers.remove(ignoreInfo);
            boolean status = ManiaCore.getInstance().getDatabase().deleteRecord(new IgnoreInfoRecord(ignoreInfo));
            if (status) {
                return IgnoreResult.SUCCESS;
            } else {
                return IgnoreResult.DATABASE_ERROR;
            }
        }

        return IgnoreResult.NOT_IGNORED;
    }

    public void sendMessage(BaseComponent baseComponent) {
    }

    public void sendMessage(String s) {
    }

    public boolean hasPermission(String permission) {
        return false;
    }

    public boolean hasPermission(Rank rank) {
        return getRank().ordinal() <= rank.ordinal();
    }

    public String getDisplayName() {
        Rank rank;
        String name;
        if (nickname.isActive()) {
            rank = nickname.getRank();
            name = nickname.getName();
        } else {
            rank = getRank();
            name = getName();
        }

        String displayName = rank.getPrefix() + rank.getBaseColor();
        if (rank != Rank.DEFAULT) {
            displayName += " ";
        }
        displayName += name;

        return displayName;
    }

    public String getColoredName() {
        if (getNickname().isActive()) {
            return nickname.getRank().getBaseColor() + nickname.getName();
        }
        return getRank().getBaseColor() + getName();
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        User user = (User) o;
        return Objects.equals(uniqueId, user.uniqueId);
    }

    public int hashCode() {
        return Objects.hash(uniqueId);
    }

    public Rank getRank() {
        if (getUniqueId().equals(FIRESTAR311)) {
            this.rank = Rank.ROOT;
        } else if (getUniqueId().equals(ASSASSINPLAYS)) {
            this.rank = Rank.OWNER;
        }

        return rank;
    }

    public void addIgnoredInfo(IgnoreInfo toObject) {
        this.ignoredPlayers.add(toObject);
    }

    public void incrementOnlineTime() {
        Statistic stat = getStat(Stats.ONLINE_TIME);
        stat.increment();

        if (stat.getAsInt() % 600 == 0) {
            int multiplier = 1;
            if (hasPermission(Rank.SCAVENGER)) {
                multiplier = 2;
            }
            if (hasPermission(Rank.MEDIA)) {
                multiplier = 3;
            }
            if (hasPermission(Rank.HELPER)) {
                multiplier = 4;
            }
            if (hasPermission(Rank.ROOT)) {
                multiplier = 5;
            }

            int exp = 10 * multiplier;
            addNetworkExperience(exp);
            sendMessage("&e&l>> &7&o+" + exp + " XP - +10m of online time");
        }
    }

    public Statistic getStat(Stat stat) {
        Statistic s = null;
        try {
            s = stats.getOrDefault(stat.getName(), stat.create(this.uniqueId));
        } catch (IllegalStateException e) {
        }
        if (s != null) {
            if (!this.stats.containsKey(stat.getName())) {
                this.stats.put(stat.getName(), s);
            }
        }
        return s;
    }

    public void setStat(Stat stat, int value) {
        Statistic s = stats.getOrDefault(stat.getName(), stat.create(this.uniqueId));
        s.setValue(value + "");
        if (!this.stats.containsKey(stat.getName())) {
            this.stats.put(stat.getName(), s);
        }
    }

    public void setStat(Stat stat, String value) {
        Statistic s = stats.getOrDefault(stat.getName(), stat.create(this.uniqueId));
        s.setValue(value);
        if (!this.stats.containsKey(stat.getName())) {
            this.stats.put(stat.getName(), s);
        }
    }

    public Level getLevel() {
        return ManiaCore.getInstance().getLevelManager().getLevel(this.getStat(Stats.EXPERIENCE).getAsInt());
    }

    public boolean isIgnoring(UUID target) {
        for (IgnoreInfo ignoredPlayer : this.ignoredPlayers) {
            if (ignoredPlayer.getPlayer().equals(target)) {
                return true;
            }
        }
        return false;
    }

    public Skin getSkin() {
        return ManiaCore.getInstance().getSkinManager().getSkin(getUniqueId());
    }

    public String toString() {
        return "User{" +
                "uniqueId=" + uniqueId +
                ", name='" + name + '\'' +
                '}';
    }
}