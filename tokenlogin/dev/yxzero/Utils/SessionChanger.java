package dev.yxzero.Utils;

import net.minecraft.client.Minecraft;
import net.minecraft.util.Session;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public class SessionChanger {

    public static void setSession(Session session) {
        try {
            Field sessionField = ReflectionHelper.findField(Minecraft.class, "session", "field_71449_j");
            sessionField.setAccessible(true);

            // Remove final modifier if present
            Field modifiersField = Field.class.getDeclaredField("modifiers");
            modifiersField.setAccessible(true);
            modifiersField.setInt(sessionField, sessionField.getModifiers() & ~Modifier.FINAL);

            sessionField.set(Minecraft.getMinecraft(), session);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
