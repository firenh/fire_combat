package fireopal.firecombat.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

@Mixin(ItemStack.class)
public class ItemStackMixin {
    private static final double PHI = (1 + Math.sqrt(5)) / 2;

    @Inject(at = @At("RETURN"), method = "postHit")
    private void postHit(LivingEntity target, PlayerEntity attacker, CallbackInfo ci) {
        EntityAttributeModifier[] attackSpeedArr = ((ItemStack)(Object)this).getItem()
            .getAttributeModifiers(EquipmentSlot.MAINHAND)
            .get(EntityAttributes.GENERIC_ATTACK_SPEED)
            .toArray(new EntityAttributeModifier[0]);

        if (attackSpeedArr.length >= 1) {
            target.hurtTime = (int) (target.maxHurtTime * Math.pow(Math.abs(attackSpeedArr[0].getValue() / 2.2), PHI));
        }
    }
}
