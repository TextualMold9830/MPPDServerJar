package com.watabou.pixeldungeon.items.armor;

import com.google.gson.JsonObject;
import com.nikita22007.multiplayer.utils.Log;
import com.watabou.pixeldungeon.actors.hero.Hero;
import com.watabou.pixeldungeon.items.Item;
import com.watabou.pixeldungeon.items.weapon.Weapon;
import com.watabou.pixeldungeon.items.weapon.melee.MeleeWeapon;
import com.watabou.pixeldungeon.network.NetworkPacket;
import com.watabou.pixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.pixeldungeon.utils.Utils;
import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Objects;

public class DebuggerArmor extends Armor {
    static final String AC_EAT = "EAT";

    {
        name = "ration of food";
        image(ItemSpriteSheet.RATION);
        inscribe();
        setGlowing(null);
    }
    public DebuggerArmor() {
        super( 1 );
        identify();
    }
    public String desc() {
        return "This lightweight armor offers bug protection.";
    }

    @Override
    public int DR() {
        return 30;
    }

    @Override
    public int typicalDR() {
        return 29;
    }
    @Override
    public ArrayList<String> actions(Hero hero ) {
        ArrayList<String> actions = super.actions( hero );
        actions.remove(AC_EQUIP);
        actions.add( isEquipped( hero ) ? AC_UNEQUIP : AC_EAT );
        return actions;
    }

    @Override
    public String info(Hero hero) {
        return
                "Nothing fancy here: dried meat, " +
                        "some biscuits - things like that.";
    }

    @Override
    public void execute(Hero hero, String action) {
        super.execute(hero, action);
        if (action.equals("EAT")){
            doEquip(hero);
        }
    }

    @Override
    public String name() {
        return "ration of food";
    }

    @NotNull
    protected JSONObject itemUI(@NotNull Hero owner) throws JSONException {
        Objects.requireNonNull(owner);
        JSONObject ui = new JSONObject();
        ui.put("top_right", new JSONObject());
        ui.put("top_left", new JSONObject());

        return ui;
    }

    @Override
    public int maxDurability(int lvl) {
        return 1;
    }

    @Override
    public int durability() {
        return 1;
    }

    @Override
    public boolean showDurabilityBar() {
        return false;
    }
}
