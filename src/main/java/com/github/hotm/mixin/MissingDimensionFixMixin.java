package com.github.hotm.mixin;

import com.github.hotm.mixinapi.DimensionAdditions;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.CompoundList;
import net.minecraft.datafixer.fix.MissingDimensionFix;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(MissingDimensionFix.class)
public abstract class MissingDimensionFixMixin extends DataFix {
    public MissingDimensionFixMixin(Schema outputSchema, boolean changesType) {
        super(outputSchema, changesType);
    }

    @ModifyVariable(method = "makeRule", name = "compoundListType", at = @At(value = "INVOKE",
            target = "Lcom/mojang/datafixers/DSL;compoundList(Lcom/mojang/datafixers/types/Type;Lcom/mojang/datafixers/types/Type;)Lcom/mojang/datafixers/types/templates/CompoundList$CompoundListType;",
            shift = At.Shift.BY,
            by = 2,
            remap = false))
    private CompoundList.CompoundListType<String, ?> onMakeRuleEditCompoundListType(
            CompoundList.CompoundListType<String, ?> compoundListType) {
        Schema schema = getInputSchema();

        return DimensionAdditions.injectChunkGeneratorTypes(compoundListType, schema);
    }
}
