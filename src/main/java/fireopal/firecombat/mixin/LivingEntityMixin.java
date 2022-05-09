package fireopal.firecombat.mixin;

import java.util.UUID;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {
	private static final Multimap<EntityAttribute, EntityAttributeModifier> ATTRIBUTE_ADDITON;
    private static final UUID ATTACK_SPEED_MODIFIER_ID = UUID.fromString("FA233E1C-4180-4865-B01B-000000000000");
	
	static {
		ImmutableMultimap.Builder<EntityAttribute, EntityAttributeModifier> builder = ImmutableMultimap.builder();
		builder.put(
			EntityAttributes.GENERIC_ATTACK_SPEED, 
			new EntityAttributeModifier(
				ATTACK_SPEED_MODIFIER_ID, 
				"Weapon modifier", 256.0, 
				EntityAttributeModifier.Operation.ADDITION
			)
		);

		ATTRIBUTE_ADDITON = builder.build();
	}

	@Inject(at = @At("HEAD"), method = "onAttacking")
	private void onAttacking(Entity target, CallbackInfo ci) {
		if (target instanceof LivingEntity && ((LivingEntity)(Object)this).getAttributes().hasAttribute(EntityAttributes.GENERIC_ATTACK_SPEED)) {
			double speed = ((LivingEntity)(Object)this).getAttributeValue(EntityAttributes.GENERIC_ATTACK_SPEED) - 256.0;

			System.out.println("Attack Speed: " + speed);
			
			((LivingEntity) target).hurtTime = (int) (
				47 * Math.pow(2, -speed)
			);

			System.out.println("HurtTime: " + ((LivingEntity) target).hurtTime);
		}
	}

	@Shadow @Final
	private AttributeContainer attributes;

	@Inject(at = @At("RETURN"), method = "baseTick", cancellable = true)
	private void baseTick(CallbackInfo ci) {
		attributes.addTemporaryModifiers(ATTRIBUTE_ADDITON);
	}

	@Inject(at = @At("HEAD"), method = "damage", cancellable = true)
	private void damage(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
		if (Math.abs(((LivingEntity)(Object)this).hurtTime) > 0) {
			cir.setReturnValue(false);
		} 
	}
}
