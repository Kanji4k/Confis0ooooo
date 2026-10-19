package client.confis.mixin;

import client.confis.Config;
import net.minecraft.class_124;
import net.minecraft.class_1297;
import net.minecraft.class_2561;
import net.minecraft.class_310;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(class_1297.class)
public class NameBadgeMixin {
    @Inject(method = "getDisplayName", at = @At("RETURN"), cancellable = true, require = 0)
    private void confis$badge(CallbackInfoReturnable<class_2561> cir) {
        if (!Config.get().nameBadge) return;
        class_310 mc = class_310.method_1551();
        if (mc.field_1724 == null || (Object) this != mc.field_1724) return;
        class_2561 original = cir.getReturnValue();
        if (original == null || original.getString().startsWith("C ")) return;
        cir.setReturnValue(class_2561.method_43473()
                .method_10852(class_2561.method_43470("C").method_27695(class_124.field_1067, class_124.field_1068))
                .method_10852(class_2561.method_43470(" "))
                .method_10852(original));
    }
}
