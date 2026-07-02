package hjsonpp;

import arc.Core;
import arc.Events;
import arc.struct.Seq;
import arc.util.Log;
import hjsonpp.expand.blocks.energy.AdjustableBeamNode;
import hjsonpp.expand.blocks.crafting.*;
import hjsonpp.expand.blocks.defense.*;
import hjsonpp.expand.blocks.environment.*;
import hjsonpp.expand.blocks.storage.*;
import hjsonpp.expand.customAbilities.*;
import hjsonpp.expand.graphics.g3d.*;
import hjsonpp.expand.wproc.*;
import mindustry.game.EventType;
import mindustry.Vars;
import mindustry.game.EventType;
import mindustry.mod.*;

import static arc.Core.app;
import static mindustry.Vars.mods;
import static mindustry.Vars.ui;

public class HjsonPlusPlusMod extends Mod{

    String[] modTurnOffBlacklist = {"as","thezsia1", "deepsea"};

    public HjsonPlusPlusMod(){
        Events.on(EventType.FileTreeInitEvent.class, e ->
                app.post(HjsonppShaders::load)
        );
        ClassMap.classes.put("AdvancedConsumeGenerator", AdvancedConsumeGenerator.class);
        ClassMap.classes.put("AdvancedHeaterGenerator", AdvancedHeaterGenerator.class);
        ClassMap.classes.put("TileGenerator", TileGenerator.class);
        ClassMap.classes.put("AdvancedCoreBlock", AdvancedCoreBlock.class);
        ClassMap.classes.put("GeneratorCoreBlock", GeneratorCoreBlock.class);
        ClassMap.classes.put("ChanceCrafter", ChanceCrafter.class);
        ClassMap.classes.put("AccelItemTurret", AccelItemTurret.class);
        ClassMap.classes.put("OverHeatTurret", OverHeatTurret.class);
        ClassMap.classes.put("RestorableWall", RestorableWall.class);
        ClassMap.classes.put("AdjustableShieldWall", AdjustableShieldWall.class);
        ClassMap.classes.put("AdjustableBeamNode", AdjustableBeamNode.class);
        ClassMap.classes.put("TiledFloor", TiledFloor.class);
        ClassMap.classes.put("DrawTeam", hjsonpp.expand.DrawTeam.class);
        ClassMap.classes.put("EffectWeapon", hjsonpp.expand.EffectWeapon.class);
        ClassMap.classes.put("CustomEffects", hjsonpp.expand.CustomEffects.class);
        ClassMap.classes.put("BlackHoleBulletType", hjsonpp.expand.BlackHoleBulletType.class);
        ClassMap.classes.put("ModeTurret", ModeTurret.class);
        ClassMap.classes.put("MultiRecipeCrafter", MultiRecipeCrafter.class);
        ClassMap.classes.put("OverloadAbility", OverloadAbility.class);
        ClassMap.classes.put("RingMesh", RingMesh.class);
    }

    @Override
    public void init(){
        super.init();
        CustomStyles.load();
    }

    @Override
    public void loadContent(){
        HjsonppLogic.init();
        Events.on(EventType.ClientLoadEvent.class, e->{
            boolean r = false;
            Seq<String> modNames = new Seq<>();
            for(String n : modTurnOffBlacklist){
                Mods.LoadedMod m  = mods.getMod(n);
                if(m != null && m.enabled()) {
                    r = true;
                    modNames.add(m.name + ";");
                    mods.setEnabled(m, false);
                }
            }
            if(r){
                ui.showOkText("No access","All blacklist mods are disabled\n"+ modNames, ()->{
                    Core.app.exit();
                });
            }
        });
    }
}
