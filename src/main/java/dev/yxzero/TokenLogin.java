package dev.yxzero;

import dev.yxzero.GUI.ChangerGUI;
import dev.yxzero.GUI.SessionGUI;
import dev.yxzero.Utils.APIUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiMultiplayer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.Session;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.Display;

import java.awt.*;
import java.io.IOException;
import java.text.ParseException;

@Mod(modid = TokenLogin.MODID, version = TokenLogin.VERSION)
public class TokenLogin {

    public static final String MODID = "TokenLogin";
    public static final String VERSION = "2.1";

    // Minecraft instance
    private static final Minecraft mc = Minecraft.getMinecraft();

    // Store original session in case of recovery
    public static final Session originalSession = mc.getSession();

    // Login status indicators
    public static String onlineStatus = "§4Offline";
    public static String isSessionValid = "§2Valid";

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(this);
        Display.setTitle("TokenLogin 2.1");
    }

    @SubscribeEvent
    public void onInitGuiPost(GuiScreenEvent.InitGuiEvent.Post event) throws IOException, ParseException {
        if (event.gui instanceof GuiMultiplayer) {
            // Add buttons
            event.buttonList.add(new GuiButton(2100, event.gui.width - 90, 5, 80, 20, "Login"));
            event.buttonList.add(new GuiButton(2200, event.gui.width - 180, 5, 80, 20, "Changer"));

            // Async session checking
            new Thread(() -> {
                try {
                    boolean valid = APIUtils.validateSession(mc.getSession().getToken());
                    boolean online = APIUtils.checkOnline(mc.getSession().getUsername());

                    isSessionValid = valid ? "§2Valid" : "§4Invalid";
                    onlineStatus = online ? "§2Online" : "§4Offline";

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }).start();
        }
    }

    @SubscribeEvent
    public void onDrawScreenPost(GuiScreenEvent.DrawScreenEvent.Post event) {
        if (event.gui instanceof GuiMultiplayer) {
            String text = String.format("§fUser: %s  §f|  %s  §f|  %s",
                    mc.getSession().getUsername(), onlineStatus, isSessionValid);

            mc.fontRendererObj.drawString(text, 5, 10, Color.RED.getRGB());
        }
    }

    @SubscribeEvent
    public void onActionPerformedPre(GuiScreenEvent.ActionPerformedEvent.Pre event) {
        if (event.gui instanceof GuiMultiplayer) {
            if (event.button.id == 2100) {
                mc.displayGuiScreen(new SessionGUI(event.gui));
            } else if (event.button.id == 2200) {
                mc.displayGuiScreen(new ChangerGUI(event.gui));
            }
        }
    }
}
