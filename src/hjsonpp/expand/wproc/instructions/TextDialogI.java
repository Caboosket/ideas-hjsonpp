package hjsonpp.expand.wproc.instructions;

import hjsonpp.expand.wproc.CustomUI;
import mindustry.logic.*;
import mindustry.type.UnitType;

public class TextDialogI implements LExecutor.LInstruction{
    public LVar text, unitIconName, duration, useBundle, uiTemplate;

    public TextDialogI(LVar text, LVar unitIconName, LVar duration, LVar useBundle, LVar uiTemplate){
        this.text = text;
        this.unitIconName = unitIconName;
        this.duration = duration;
        this.useBundle = useBundle;
        this.uiTemplate = uiTemplate;
    }

    public TextDialogI(){}

    @Override
    public void run(LExecutor exec){
        if(unitIconName.obj() instanceof UnitType icon){
            CustomUI.textDialog(text.obj().toString(), icon.name, duration.numf(), useBundle.bool(), uiTemplate.obj().toString());
        }
    }
}
