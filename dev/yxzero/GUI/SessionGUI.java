package dev.yxzero.GUI;

import dev.yxzero.TokenLogin;
import dev.yxzero.Utils.APIUtils;
import dev.yxzero.Utils.SessionChanger;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;
import net.minecraft.util.Session;
import org.lwjgl.input.Keyboard;

import java.awt.*;
import java.io.IOException;

public class SessionGUI extends GuiScreen {

    private final GuiScreen previousScreen;
    private String status = "Session";

    private GuiTextField sessionField;
    private ScaledResolution sr;

    public SessionGUI(GuiScreen previousScreen) {
        this.previousScreen = previousScreen;
    }

    @Override
    public void initGui() {
        Keyboard.enableRepeatEvents(true);
        this.sr = new ScaledResolution(this.mc);

        this.sessionField = new GuiTextField(1, this.mc.fontRendererObj,
                sr.getScaledWidth() / 2 - 100, sr.getScaledHeight() / 2, 200, 20);
        this.sessionField.setMaxStringLength(32767);
        this.sessionField.setFocused(true);

        this.buttonList.add(new GuiButton(1400, sr.getScaledWidth() / 2 - 100, sr.getScaledHeight() / 2 + 25, 97, 20, "Login"));
        this.buttonList.add(new GuiButton(1500, sr.getScaledWidth() / 2 + 3, sr.getScaledHeight() / 2 + 25, 97, 20, "Restore"));
        this.buttonList.add(new GuiButton(1600, sr.getScaledWidth() / 2 - 100, sr.getScaledHeight() / 2 + 50, 200, 20, "Back"));

        super.initGui();
    }

    @Override
    public void onGuiClosed() {
        Keyboard.enableRepeatEvents(false);
        super.onGuiClosed();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        int textWidth = this.mc.fontRendererObj.getStringWidth(this.status);
        this.mc.fontRendererObj.drawString(
                this.status,
                sr.getScaledWidth() / 2 - textWidth / 2,
                sr.getScaledHeight() / 2 - 30,
                Color.WHITE.getRGB()
        );
        this.sessionField.drawTextBox();
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        switch (button.id) {
            case 1400:
                loginWithToken();
                break;
            case 1500:
                SessionChanger.setSession(TokenLogin.originalSession);
                this.status = "§2Restored session";
                break;
            case 1600:
                this.mc.displayGuiScreen(this.previousScreen);
                break;
        }
        super.actionPerformed(button);
    }

    private void loginWithToken() {
        new Thread(() -> {
            try {
                String token = this.sessionField.getText();
                String[] playerInfo = APIUtils.getProfileInfo(token);
                Session newSession = new Session(playerInfo[0], playerInfo[1], token, "mojang");
                SessionChanger.setSession(newSession);
                this.status = "§2Logged in as " + playerInfo[0];
            } catch (Exception e) {
                this.status = "§4Invalid token";
                e.printStackTrace();
            }
        }).start();
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        this.sessionField.textboxKeyTyped(typedChar, keyCode);
        if (keyCode == Keyboard.KEY_ESCAPE) {
            this.mc.displayGuiScreen(previousScreen);
        } else {
            super.keyTyped(typedChar, keyCode);
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        this.sessionField.mouseClicked(mouseX, mouseY, mouseButton);
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }
}
