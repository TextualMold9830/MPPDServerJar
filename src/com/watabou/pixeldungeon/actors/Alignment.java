package com.watabou.pixeldungeon.actors;

public class Alignment {
    public static final String ALLY = "ally";
    public static final String ENEMY = "enemy";
    public static final String NEUTRAL = "neutral";
    public static final String FREE_FOR_ALL = "FFA";
    public static boolean isFriendly(String alignment1, String alignment2) {
        boolean isFFA = alignment1.equals(FREE_FOR_ALL) || alignment2.equals(FREE_FOR_ALL);
        boolean isNeutral = alignment1.equals(NEUTRAL) || alignment2.equals(NEUTRAL);
        //Neutral is friend with everyone
        if (isNeutral) {
            return true;
        }
        //FFA is enemy with everyone but neutral
        if (isFFA) {
            return false;
        }
        //Every other alignment is friendly with itself
        if (alignment1.equals(alignment2)) {
            return true;
        }
        //alignments differ, no neutral, results in enemy
        return false;
    }
    public static boolean isHostile(String alignment1, String alignment2){
        return !isFriendly(alignment1, alignment2);
    }
}
