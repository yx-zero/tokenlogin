package dev.yxzero.GUI;

import dev.yxzero.TokenLogin;
import dev.yxzero.Utils.APIUtils;
import dev.yxzero.Utils.SessionChanger;
import net.minecraft.client.gui.*;
import net.minecraft.util.Session;
import org.lwjgl.input.Keyboard;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

public class ChangerGUI extends GuiScreen {

    private final GuiScreen previousScreen;
    private String status = "";

    private GuiTextField nameField;
    private GuiTextField skinField;
    private ScaledResolution sr;
    private final ArrayList<GuiTextField> textFields = new ArrayList<>();

    public ChangerGUI(GuiScreen previousScreen) {
        this.previousScreen = previousScreen;
    }

    @Override
    public void initGui() {
        Keyboard.enableRepeatEvents(true);
        this.sr = new ScaledResolution(this.mc);

        this.nameField = new GuiTextField(1, this.mc.fontRendererObj, sr.getScaledWidth() / 2 - 100, sr.getScaledHeight() / 2, 97, 20);
        this.nameField.setMaxStringLength(16);
        this.nameField.setFocused(true);

        this.skinField = new GuiTextField(2, this.mc.fontRendererObj, sr.getScaledWidth() / 2 + 3, sr.getScaledHeight() / 2, 97, 20);
        this.skinField.setMaxStringLength(32767);

        this.textFields.add(nameField);
        this.textFields.add(skinField);

        this.buttonList.add(new GuiButton(3100, sr.getScaledWidth() / 2 - 100, sr.getScaledHeight() / 2 + 25, 97, 20, "Change Name"));
        this.buttonList.add(new GuiButton(3200, sr.getScaledWidth() / 2 + 3, sr.getScaledHeight() / 2 + 25, 97, 20, "Change Skin"));
        this.buttonList.add(new GuiButton(3300, sr.getScaledWidth() / 2 - 100, sr.getScaledHeight() / 2 + 50, 200, 20, "Back"));

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
        this.mc.fontRendererObj.drawString(this.status, sr.getScaledWidth() / 2 - mc.fontRendererObj.getStringWidth(status) / 2, sr.getScaledHeight() / 2 - 40, Color.WHITE.getRGB());
        this.mc.fontRendererObj.drawString("Name:", sr.getScaledWidth() / 2 - 99, sr.getScaledHeight() / 2 - 15, Color.WHITE.getRGB());
        this.mc.fontRendererObj.drawString("Skin:", sr.getScaledWidth() / 2 + 4, sr.getScaledHeight() / 2 - 15, Color.WHITE.getRGB());
        this.nameField.drawTextBox();
        this.skinField.drawTextBox();
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        if (button.id == 3100) {
            String newName = this.nameField.getText();
            if (Objects.equals(TokenLogin.originalSession.getToken(), mc.getSession().getToken())) {
                this.status = "§4Prevented you from changing the name of your main account!";
            } else {
                new Thread(() -> {
                    try {
                        int statusCode = APIUtils.changeName(newName, mc.getSession().getToken());
                        if (statusCode == 200) {
                            this.status = "§2Successfully changed name!";
                            SessionChanger.setSession(new Session(newName, mc.getSession().getPlayerID(), mc.getSession().getToken(), "mojang"));
                        } else if (statusCode == 429) {
                            this.status = "§4Error: Too many requests!";
                        } else if (statusCode == 400) {
                            this.status = "§4Error: Invalid name!";
                        } else if (statusCode == 401) {
                            this.status = "§4Error: Invalid token!";
                        } else if (statusCode == 403) {
                            this.status = "§4Error: Name is unavailable/Recently changed";
                        } else {
                            this.status = "§4An unknown error occurred!";
                        }
                    } catch (Exception e) {
                        this.status = "§4An unknown error occurred!";
                        e.printStackTrace();
                    }
                }).start();
            }
        }

        if (button.id == 3200) {
            String newSkin = this.skinField.getText();
            new Thread(() -> {
                try {
                    int statusCode = APIUtils.changeSkin(newSkin, mc.getSession().getToken());
                    if (statusCode == 200) {
                        this.status = "§2Successfully changed skin!";
                    } else if (statusCode == 429) {
                        this.status = "§4Error: Too many requests!";
                    } else if (statusCode == 401) {
                        this.status = "§4Error: Invalid token!";
                    } else {
                        this.status = "§4Error: Invalid skin!";
                    }
                } catch (Exception e) {
                    this.status = "§4An unknown error occurred!";
                    e.printStackTrace();
                }
            }).start();
        }

        if (button.id == 3300) {
            this.mc.displayGuiScreen(this.previousScreen);
        }

        super.actionPerformed(button);
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        this.nameField.textboxKeyTyped(typedChar, keyCode);
        this.skinField.textboxKeyTyped(typedChar, keyCode);

        if (keyCode == Keyboard.KEY_ESCAPE) {
            this.mc.displayGuiScreen(this.previousScreen);
        } else {
            super.keyTyped(typedChar, keyCode);
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        for (GuiTextField text : this.textFields) {
            text.mouseClicked(mouseX, mouseY, mouseButton);
        }
    }
}
