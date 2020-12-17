package com.github.hotm.mixin;

import com.github.hotm.mixinapi.DimensionAdditions;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TaggedChoice;
import net.minecraft.datafixer.fix.MissingDimensionFix;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(MissingDimensionFix.class)
public abstract class MissingDimensionFixMixin extends DataFix {
    public MissingDimensionFixMixin(Schema outputSchema, boolean changesType) {
        super(outputSchema, changesType);
    }

    @ModifyVariable(method = "makeRule", name = "taggedChoiceType", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/datafixer/schema/IdentifierNormalizingSchema;getIdentifierType()Lcom/mojang/datafixers/types/Type;",
            remap = false))
    private TaggedChoice.TaggedChoiceType<String> onMakeRuleEditTaggedChoiceType(
            TaggedChoice.TaggedChoiceType<String> taggedChoiceType) {
        Schema schema = getInputSchema();

        return DimensionAdditions.injectChunkGeneratorTypes(taggedChoiceType, schema);
    }
}
