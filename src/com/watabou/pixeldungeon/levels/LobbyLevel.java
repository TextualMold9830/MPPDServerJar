package com.watabou.pixeldungeon.levels;

import com.watabou.pixeldungeon.BuildConfig;
import com.watabou.pixeldungeon.actors.Actor;
import com.watabou.pixeldungeon.items.armor.DebuggerArmor;
import com.watabou.pixeldungeon.items.scrolls.ScrollOfEnchantment;
import com.watabou.pixeldungeon.items.scrolls.ScrollOfMagicMapping;
import com.watabou.pixeldungeon.levels.painters.Painter;

import static com.watabou.pixeldungeon.levels.Terrain.*;

public class LobbyLevel extends DeadEndLevel {
    int cell = -1;

    public LobbyLevel() {
        super(7);
    }

    @Override
    protected boolean build() {
        boolean g = super.build();
        if (!g) {
            return false;
        }

        viewDistance = ((int) (SIZE * 1.5));
        exit = entrance + Level.WIDTH;
        map[exit] = Terrain.EXIT;
        if (BuildConfig.DEBUG) {
            {
                int pos = center - 2-this.WIDTH;
                Painter.set(this, pos,TOXIC_TRAP);
                pos+=1;
                Painter.set(this, pos,FIRE_TRAP);
                pos+=1;
                Painter.set(this, pos,PARALYTIC_TRAP);
                pos+=1;
                Painter.set(this, pos,POISON_TRAP);

                pos = center - 2-this.WIDTH-this.WIDTH;

                Painter.set(this, pos,ALARM_TRAP);
                pos+=1;
                Painter.set(this, pos,LIGHTNING_TRAP);
                pos+=1;
                Painter.set(this, pos,GRIPPING_TRAP);
                pos+=1;
                Painter.set(this, pos,SUMMONING_TRAP);
            }
            ScrollOfEnchantment n = new ScrollOfEnchantment();
            drop(n,center + 1);
            ScrollOfMagicMapping somm = new ScrollOfMagicMapping();
                    somm.quantity(15);
            drop(somm, center + 2);
            drop(new DebuggerArmor(), center + 3);
        }

        return true;
    }

    public Actor respawner() {
        return null;
    }
}
