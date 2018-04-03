package ru.denull.BugPatch.mod;

import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.ReflectionHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChunkCoordinates;
import net.minecraftforge.event.entity.player.PlayerSleepInBedEvent;
import net.minecraftforge.event.world.BlockEvent;
import ru.denull.BugPatch.coremod.BugPatchClassTransformer;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Created by Vincent on 7/9/2015.
 */
public class ClientEvents {
	public Logger logger = LogManager.getLogger("BugPatch");
	
    @SubscribeEvent
    public void blockBreak(BlockEvent.BreakEvent evt) {
        PlayerControllerMP controller = Minecraft.getMinecraft().playerController;
        Method m = ReflectionHelper.findMethod(PlayerControllerMP.class, controller, new String[] {"syncCurrentPlayItem", "func_78750_k", "k"});
        if (m != null) {
            try {
                m.setAccessible(true);
                m.invoke(controller);
            } catch (ReflectiveOperationException ex) {
                logger.warn("Tooldesyncfix failed to resync");
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void playerSleepInBed(PlayerSleepInBedEvent evt) {
    		logger.info("Trying to sleep at " + evt.x + "," + evt.y + "," + evt.z);
    		if (evt.y <= 0) {
    			int correctY = 256 + evt.y;
    			logger.info("Player is trying to sleep at y=" + evt.y + ". This is probably caused by overflow, fixing to " + correctY + ".");
    			
    			if (correctY <= 0) {
    				logger.error("Fixed y=" + correctY + " is still <= 0! This should not happen, ignoring.");
    			} else {
    				evt.result = EntityPlayer.EnumStatus.OTHER_PROBLEM;
    				//EntityPlayer p = evt.entityPlayer;
    				try {
					Method m = EntityPlayer.class.getMethod("func_71018_a", int.class, int.class, int.class);
					logger.info("Method sleepInBed found");
					try {
						m.invoke(evt.entityPlayer, evt.x, correctY, evt.z);
					} catch (IllegalAccessException e) {
						logger.error("Illegal access");
						e.printStackTrace();
					} catch (IllegalArgumentException e) {
						logger.error("Illegal argument");
						e.printStackTrace();
					} catch (InvocationTargetException e) {
						logger.error("Invocation target");
						e.printStackTrace();
					}
				} catch (NoSuchMethodException e) {
					logger.error("Not found sleepInBed method");
					e.printStackTrace();
				} catch (SecurityException e) {
					logger.error("Security exception");
					e.printStackTrace();
				}
    			}
    		}
    }
    
}
