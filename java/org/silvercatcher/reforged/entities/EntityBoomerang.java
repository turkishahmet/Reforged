package org.silvercatcher.reforged.entities;

import org.silvercatcher.reforged.items.weapons.ItemBoomerang;

import net.minecraft.entity.DataWatcher.WatchableObject;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.item.Item.ToolMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

public class EntityBoomerang extends EntityThrowable {
	
	
	public EntityBoomerang(World worldIn) {
		
		super(worldIn);
	}
	
	
	public EntityBoomerang(World worldIn, EntityLivingBase throwerIn, ItemStack stack) {
		
		super(worldIn, throwerIn);

		setItemStack(stack);
	}
	
	@Override
	protected void entityInit() {
	
		super.entityInit();
		
		// id 5 = ItemStack of Boomerang, type 5 = Itemstack
		dataWatcher.addObjectByDataType(5, 5);

	}

	public ItemStack getItemStack() {
		
		return dataWatcher.getWatchableObjectItemStack(5);
	}
	
	public void setItemStack(ItemStack stack) {
		
		if(stack == null || !(stack.getItem() instanceof ItemBoomerang)) {
			throw new IllegalArgumentException("Invalid Itemstack!");
		}
		dataWatcher.updateObject(5, stack);
	}
	
	private void printDatawatcher() {
		
		System.out.println("##########");
		for(Object o : dataWatcher.getAllWatched()) {
			WatchableObject wo = (WatchableObject) o;
			System.out.println(wo.getDataValueId() + ": (" + wo.getObjectType() + ") " + wo.getObject());
		}
		System.out.println("++++++++++++++++");
	}
	
	public ToolMaterial getMaterial() {

		return ((ItemBoomerang) getItemStack().getItem()).getMaterial();
	}

	private float getImpactDamage() {
		
		return getMaterial().getDamageVsEntity()  + 3;
	}

	@Override
	protected void onImpact(MovingObjectPosition target) {
			
		//Target is entity or block?
		if(target.entityHit == null) {
			//It's a block
			if(!worldObj.isRemote) {
				entityDropItem(getItemStack(), 0.5f);
			}
			setDead();
		} else {
			//It's an entity
			target.entityHit.attackEntityFrom(DamageSource.causeThrownDamage(
					target.entityHit, getThrower()), getImpactDamage());
			ItemStack stack = getItemStack();
			if(stack.attemptDamageItem(1, rand)) {
				setDead();
			} else {
				setItemStack(stack);
			}
		}
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound tagCompound) {
		
		super.writeEntityToNBT(tagCompound);
		
		if(getItemStack() != null) {
			tagCompound.setTag("item", getItemStack().writeToNBT(new NBTTagCompound()));
		}
	}
	
	@Override
	public void readEntityFromNBT(NBTTagCompound tagCompund) {
		
		super.readEntityFromNBT(tagCompund);
		
		setItemStack(ItemStack.loadItemStackFromNBT(tagCompund.getCompoundTag("item")));
	}
}
