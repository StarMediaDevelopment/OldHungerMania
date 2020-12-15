package net.hungermania.hungergames.mutations;

import net.hungermania.hungergames.mutations.types.*;
import net.hungermania.maniacore.api.MutationType;

import java.util.HashMap;
import java.util.Map;

public final class Mutations {
    public static final Mutation PIG_ZOMBIE = new PigZombie();
    public static final Mutation CREEPER = new Creeper();
    public static final Mutation CHICKEN = new Chicken();
    public static final Mutation ENDERMAN = new Enderman();
    public static final Mutation SKELETON = new Skeleton();
    public static final Mutation ZOMBIE = new Zombie();
    
    public static final Map<MutationType, Mutation> MUTATIONS = new HashMap<>();
    static {
        MUTATIONS.put(MutationType.PIG_ZOMBIE, PIG_ZOMBIE);
        MUTATIONS.put(MutationType.CREEPER, CREEPER);
        //MUTATIONS.put(MutationType.CHICKEN, CHICKEN);
        MUTATIONS.put(MutationType.ZOMBIE, ZOMBIE);
        MUTATIONS.put(MutationType.ENDERMAN, ENDERMAN);
        MUTATIONS.put(MutationType.SKELETON, SKELETON);
    }
}
