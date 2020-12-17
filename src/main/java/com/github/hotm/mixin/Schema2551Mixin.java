package com.github.hotm.mixin;

import com.github.hotm.mixinapi.DimensionAdditions;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import net.minecraft.datafixer.schema.Schema2551;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Schema2551.class)
public class Schema2551Mixin {
    @Inject(method = "method_28297", at = @At("RETURN"))
    private static void onMethod_28297(Schema schema, CallbackInfoReturnable<TypeTemplate> cir) {
        DimensionAdditions.injectChunkGeneratorSchema(cir.getReturnValue(), schema);
    }
}
