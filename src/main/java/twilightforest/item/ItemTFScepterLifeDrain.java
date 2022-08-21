//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package twilightforest.item;

import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumAction;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ActionResult;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemTFScepterLifeDrain extends ItemTF {
    protected ItemTFScepterLifeDrain(EnumRarity rarity) {
        super(rarity);
        this.maxStackSize = 1;
        this.setMaxDamage(99);
        this.setCreativeTab(TFItems.creativeTab);
    }

    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        player.setActiveHand(hand);
        return ActionResult.newResult(EnumActionResult.SUCCESS, player.getHeldItem(hand));
    }

    public float getXpRepairRatio(ItemStack stack) {
        return 1.0F;
    }

    private static void animateTargetShatter(World world, EntityLivingBase target) {
        int itemId = Item.getIdFromItem(getTargetDropItem(target));

        for(int i = 0; i < 50; ++i) {
            double gaussX = itemRand.nextGaussian() * 0.02D;
            double gaussY = itemRand.nextGaussian() * 0.02D;
            double gaussZ = itemRand.nextGaussian() * 0.02D;
            double gaussFactor = 10.0D;
            world.spawnParticle(EnumParticleTypes.ITEM_CRACK, target.posX + (double)(itemRand.nextFloat() * target.width * 2.0F) - (double)target.width - gaussX * gaussFactor, target.posY + (double)(itemRand.nextFloat() * target.height) - gaussY * gaussFactor, target.posZ + (double)(itemRand.nextFloat() * target.width * 2.0F) - (double)target.width - gaussZ * gaussFactor, gaussX, gaussY, gaussZ, new int[]{itemId});
        }

    }

    private static Item getTargetDropItem(EntityLivingBase target) {
        return Items.ROTTEN_FLESH;
    }

    @Nullable
    private Entity getPlayerLookTarget(World world, EntityLivingBase living) {
        Entity pointedEntity = null;
        double range = 20.0D;
        Vec3d srcVec = new Vec3d(living.posX, living.posY + (double)living.getEyeHeight(), living.posZ);
        Vec3d lookVec = living.getLook(1.0F);
        Vec3d destVec = srcVec.addVector(lookVec.x * range, lookVec.y * range, lookVec.z * range);
        float var9 = 1.0F;
        List<Entity> possibleList = world.getEntitiesWithinAABBExcludingEntity(living, living.getEntityBoundingBox().expand(lookVec.x * range, lookVec.y * range, lookVec.z * range).grow((double)var9, (double)var9, (double)var9));
        double hitDist = 0.0D;
        Iterator var13 = possibleList.iterator();

        while(true) {
            Entity possibleEntity;
            do {
                while(true) {
                    do {
                        if (!var13.hasNext()) {
                            return pointedEntity;
                        }

                        possibleEntity = (Entity)var13.next();
                    } while(!possibleEntity.canBeCollidedWith());

                    float borderSize = possibleEntity.getCollisionBorderSize();
                    AxisAlignedBB collisionBB = possibleEntity.getEntityBoundingBox().grow((double)borderSize, (double)borderSize, (double)borderSize);
                    RayTraceResult interceptPos = collisionBB.calculateIntercept(srcVec, destVec);
                    if (collisionBB.contains(srcVec)) {
                        break;
                    }

                    if (interceptPos != null) {
                        double possibleDist = srcVec.distanceTo(interceptPos.hitVec);
                        if (possibleDist < hitDist || hitDist == 0.0D) {
                            pointedEntity = possibleEntity;
                            hitDist = possibleDist;
                        }
                    }
                }
            } while(!(0.0D < hitDist) && hitDist != 0.0D);

            pointedEntity = possibleEntity;
            hitDist = 0.0D;
        }
    }

    public void onUsingTick(ItemStack stack, EntityLivingBase living, int count) {
        World world = living.world;
        if (stack.getItemDamage() >= this.getMaxDamage(stack)) {
            living.resetActiveHand();
        } else {
            if (count % 5 == 0) {
                Entity pointedEntity = this.getPlayerLookTarget(world, living);
                if (pointedEntity != null && pointedEntity instanceof EntityLivingBase && ((EntityLivingBase) pointedEntity).getHealth()>0) {
                    EntityLivingBase target = (EntityLivingBase)pointedEntity;
                    if (target.getActivePotionEffect(MobEffects.SLOWNESS) == null && !(target.getHealth() < 1.0F)) {
                        this.makeRedMagicTrail(world, living.posX, living.posY + (double)living.getEyeHeight(), living.posZ, target.posX, target.posY + (double)target.getEyeHeight(), target.posZ);
                        living.playSound(SoundEvents.ITEM_FLINTANDSTEEL_USE, 1.0F, (world.rand.nextFloat() - world.rand.nextFloat()) * 0.2F + 1.0F);
                        if (!world.isRemote) {
                            target.attackEntityFrom(DamageSource.causeIndirectMagicDamage(living, living), 1.0F);
                            if (this.getMaxHealth(target) <= this.getMaxHealth(living)) {
                                target.motionX = 0.0D;
                                target.motionY = 0.2D;
                                target.motionZ = 0.0D;
                            }

                            target.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 20, 2));
                        }
                    } else if (target.getHealth() <= 3.0F) {
                        this.makeRedMagicTrail(world, living.posX, living.posY + (double)living.getEyeHeight(), living.posZ, target.posX, target.posY + (double)target.getEyeHeight(), target.posZ);
                        if (target instanceof EntityLiving) {
                            ((EntityLiving)target).spawnExplosionParticle();
                        }

                        target.playSound(SoundEvents.ENTITY_GENERIC_BIG_FALL, 1.0F, ((itemRand.nextFloat() - itemRand.nextFloat()) * 0.7F + 1.0F) * 2.0F);
                        animateTargetShatter(world, target);
                        if (!world.isRemote) {
                            target.setDead();
                            target.onDeath(DamageSource.causeIndirectMagicDamage(living, living));
                        }

                        living.resetActiveHand();
                    } else if (!world.isRemote) {
                        target.attackEntityFrom(DamageSource.causeIndirectMagicDamage(living, living), 3.0F);
                        if (this.getMaxHealth(target) <= this.getMaxHealth(living)) {
                            target.motionX = 0.0D;
                            target.motionY = 0.2D;
                            target.motionZ = 0.0D;
                        }

                        target.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 20, 2));
                        if (count % 10 == 0) {
                            living.heal(1.0F);
                            if (living instanceof EntityPlayer) {
                                ((EntityPlayer)living).getFoodStats().addStats(1, 0.1F);
                            }
                        }
                    }

                    if (!world.isRemote) {
                        stack.damageItem(1, living);
                    }
                }
            }

        }
    }

    private float getMaxHealth(EntityLivingBase target) {
        return (float)target.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).getBaseValue();
    }

    private void makeRedMagicTrail(World world, double srcX, double srcY, double srcZ, double destX, double destY, double destZ) {
        int particles = 32;

        for(int i = 0; i < particles; ++i) {
            double trailFactor = (double)i / ((double)particles - 1.0D);
            float f = 1.0F;
            float f1 = 0.5F;
            float f2 = 0.5F;
            double tx = srcX + (destX - srcX) * trailFactor + world.rand.nextGaussian() * 0.005D;
            double ty = srcY + (destY - srcY) * trailFactor + world.rand.nextGaussian() * 0.005D;
            double tz = srcZ + (destZ - srcZ) * trailFactor + world.rand.nextGaussian() * 0.005D;
            world.spawnParticle(EnumParticleTypes.SPELL_MOB, tx, ty, tz, (double)f, (double)f1, (double)f2, new int[0]);
        }

    }

    public int getMaxItemUseDuration(ItemStack stack) {
        return 72000;
    }

    public EnumAction getItemUseAction(ItemStack stack) {
        return EnumAction.BOW;
    }

    public boolean canContinueUsing(ItemStack oldStack, ItemStack newStack) {
        return oldStack.getItem() == newStack.getItem();
    }

    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return slotChanged || newStack.getItem() != oldStack.getItem();
    }

    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag flags) {
        super.addInformation(stack, world, tooltip, flags);
        tooltip.add(I18n.format("twilightforest.scepter_charges", new Object[]{stack.getMaxDamage() - stack.getItemDamage()}));
    }
}
