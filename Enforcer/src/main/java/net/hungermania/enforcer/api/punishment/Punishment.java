package net.hungermania.enforcer.api.punishment;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Builder
public class Punishment {
    public enum Type {
        BAN, MUTE, WARN, KICK, UNKNOWN
    }

    public enum Visibility {
        PUBLIC, STAFF_ONLY, SILENT;

        public static final Visibility DEFAULT_VISIBILITY = STAFF_ONLY;
    }

    @Setter protected int id = -1;
    protected String actor = "", target = "", reason = "", pardonReason = "", server = "";
    protected Type type = Type.UNKNOWN;
    protected long date = -1, pardonDate = -1, length = -1;
    protected Visibility visibility = Visibility.DEFAULT_VISIBILITY, pardonVisibility = Visibility.DEFAULT_VISIBILITY;
    protected boolean active = false, offline = false, acknowledged = false;

    protected Punishment() {
    }
}
