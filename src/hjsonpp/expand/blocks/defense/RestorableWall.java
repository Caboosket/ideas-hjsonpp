package hjsonpp.expand.blocks.defense;

import arc.graphics.Color;
import arc.util.Nullable;
import arc.util.Time;
import hjsonpp.expand.meta.AdditionalStats;
import mindustry.content.Blocks;
import mindustry.content.Fx;
import mindustry.entities.Effect;
import mindustry.graphics.Pal;
import mindustry.world.blocks.defense.Wall;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatUnit;

public class RestorableWall extends Wall{
    public boolean continuous = false;
    // reload between healing
    public float healReload = 1f;
    // how much wall heals in percent
    public float healPercent = 7f;
    //  how much wall heals in HP (takes precedence over healPercent)
    public float healAmount = Float.NEGATIVE_INFINITY;

    public Effect healEffect = Fx.healBlockFull;

    public Color healColor = Pal.heal;

    public float effectInterval = -1;

    public RestorableWall(String name){
        super(name);
    }

    @Override
    public void setStats(){
        super.setStats();
        if(healAmount == Float.NEGATIVE_INFINITY) {
            stats.add(AdditionalStats.healPercent, healPercent);
        } else {
            stats.add(AdditionalStats.healAmount, healAmount);
        }
        if(!continuous) stats.add(Stat.reload, 60f / healReload, StatUnit.perSecond);
    }

    public class RestorableWallBuild extends WallBuild {
        public float charge = 0;

        @Override
        public void updateTile(){
            if(!continuous){
                charge += edelta();
                if((charge >= healReload) && health() < maxHealth() && canConsume()) {
                    charge = 0f;
                    if(healAmount == Float.NEGATIVE_INFINITY) {
                        heal(maxHealth() / 100 * healPercent);
                    } else  {
                        heal(healAmount);
                    }
                    healEffect.at(x, y, block.size, healColor, block);
                    recentlyHealed();
                }
            } else {
                if(health() < maxHealth() && canConsume()){
                    charge += edelta();
                    if(charge >= effectInterval || effectInterval == -1) {
                        charge = 0;
                        healEffect.at(x, y, block.size, healColor, block);
                    }
                    if(healAmount == Float.NEGATIVE_INFINITY) {
                        heal(maxHealth() / 100 * healPercent * efficiency);
                    } else  {
                        heal(healAmount * efficiency);
                    }
                    recentlyHealed();
                }
            }
        }
    }
}
