package ru.denull.BugPatch.mod;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.ReflectionHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraftforge.event.world.BlockEvent;
import ru.denull.BugPatch.coremod.BugPatchClassTransformer;

import java.lang.reflect.Method;

/**
 * Created by Vincent on 7/9/2015.
 */
public class ClientEvents {
    @SubscribeEvent
    public void blockBreak(BlockEvent.BreakEvent evt) {
        PlayerControllerMP controller = Minecraft.getMinecraft().playerController;
        Method m = ReflectionHelper.findMethod(PlayerControllerMP.class, controller, new String[] {"syncCurrentPlayItem", "func_78750_k", "k"});
        if (m != null) {
            try {
                m.setAccessible(true);
                m.invoke(controller);
            } catch (ReflectiveOperationException ex) {
                BugPatchClassTransformer.instance.logger.warn("Tooldesyncfix failed to resync");
            }
        }
    }
}
