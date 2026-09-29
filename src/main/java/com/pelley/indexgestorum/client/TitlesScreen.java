package com.pelley.indexgestorum.client;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

public class TitlesScreen extends Screen
{
    // Placeholder data until the screen is wired to real titles (build plan step 5).
    // Text is copied from design/data/02-Titles.csv. hint is null unless the title is secret.
    private record SampleTitle(String name, Rarity rarity, String flavor, String bonus, String howToEarn, String hint) {}

    private static final List<SampleTitle> ALL_TITLES = List.of(
            new SampleTitle("Novice", Rarity.COMMON, "Everyone starts somewhere.",
                    "Small all-round nudge for new characters (+2.0 max health)",
                    "Granted on first join if the random roll gives nothing better", null),
            new SampleTitle("Wanderer", Rarity.COMMON, "The road was there before you were.",
                    "Slightly faster on foot (+4%)",
                    "Possible random starter title", null),
            new SampleTitle("Tinkerer", Rarity.COMMON, "Fix it twice and it never breaks.",
                    "Tools last a little longer (-10% durability consumption)",
                    "Possible random starter title", null),
            new SampleTitle("Forager", Rarity.COMMON, "The forest sets a table for those who look.",
                    "Food restores slightly more saturation (+15% saturation from food)",
                    "Possible random starter title", null),
            new SampleTitle("Stone Breaker", Rarity.COMMON, "Knuckles like gravel.",
                    "Bare hands break stone-tier blocks at stone pickaxe speed and they drop properly",
                    "Break 10 stone-family blocks with an empty hand", null),
            new SampleTitle("Woodcutter", Rarity.COMMON, "The forest is just a queue.",
                    "Axes bite deeper (+25% axe break speed)",
                    "Chop 500 logs", null),
            new SampleTitle("Deepslate Delver", Rarity.UNCOMMON, "The stone gets meaner the deeper you go.",
                    "Faster mining below sea level (+15% while below y=0)",
                    "Mine 500 deepslate", null),
            new SampleTitle("Spelunker", Rarity.UNCOMMON, "Daylight is a rumor down here.",
                    "Faint sight in total darkness (Night Vision while below y=0)",
                    "Spend 10 in-game days below y=-40", null),
            new SampleTitle("Iron Knuckles", Rarity.RARE, "You stopped noticing the blood.",
                    "Bare hands reach iron tier",
                    "Break 40 blocks by hand after earning Stone Breaker", null),
            new SampleTitle("Vein Seeker", Rarity.RARE, "You can hear where the metal sleeps.",
                    "Chance at extra ore drops (8% double drop and +1 luck)",
                    "Mine 250 ore blocks of any type", null),
            new SampleTitle("Obsidian Willed", Rarity.RARE, "Slow to break. Slower to bend.",
                    "Resistant to explosions (-25% explosion damage)",
                    "Mine 64 obsidian", null),
            new SampleTitle("Quarrymaster", Rarity.EPIC, "The mountain lost.",
                    "Mining mastery (+20% speed and +2 efficiency). Utility only - no combat power.",
                    "Mine 40000 blocks total", null),
            new SampleTitle("Dragonslayer", Rarity.LEGENDARY, "It was big. You were stubborn.",
                    "Broad combat mastery (+10% damage and +12 max health)",
                    "Kill the Ender Dragon", null),
            new SampleTitle("Berserker Unlimited", Rarity.MYTHIC, "Two hearts is plenty. You have not needed more in a long time.",
                    "Permanently faster and hitting harder (+15% damage and +10% movement speed)",
                    "Kill 100 hostile mobs while at or below 2 hearts",
                    "Something in you stopped flinching a long time ago."),
            new SampleTitle("First Dragonslayer", Rarity.UNIQUE, "There is only ever one first.",
                    "Never take fall damage, plus a small combat edge (+5% damage)",
                    "Be the first player on this world to kill the Ender Dragon", null),
            new SampleTitle("Monster Slayer", Rarity.UNIQUE, "Nothing in the dark is new to you anymore.",
                    "+20% damage vs all hostile mobs. Does nothing in PvP.",
                    "Be one of the first 10 players to kill 150 of every standard hostile mob",
                    "Eradication of these fiends is the only solution..."),
            new SampleTitle("The Immortal", Rarity.MYTHIC, "Death has stopped filing the paperwork.",
                    "One free cheat of death per in-game day (totem-style revive and +2 max health)",
                    "Survive 150 in-game days without a single death", null),
            new SampleTitle("Grand Alchemist", Rarity.LEGENDARY, "You stopped reading the recipes years ago.",
                    "Your brews come out a tier stronger (+1 amplifier on self-brewed potions)",
                    "Brew every potion type in the game", null),
            new SampleTitle("First Light", Rarity.LEGENDARY, "Someone had to turn on the lights. Fifty of you were here to see it.",
                    "Golden nameplate and a numbered founder entry in the ledger (+2 max health and +1 luck)",
                    "Be one of the first 50 players ever to join this world", null),
            new SampleTitle("The Unrivaled", Rarity.UNIQUE, "Someone is always counting.",
                    "Gold name on the tab list (+10% damage). Passes to whoever beats your win count.",
                    "Hold the most wins in agreed player-vs-player duels on the server", null),
            new SampleTitle("Pacifist", Rarity.LEGENDARY, "You just walked past all of it.",
                    "Hostile mobs are slow to notice you (-40% mob detection range and +8 max health)",
                    "Reach 15 in-game days having killed no mob or player",
                    "Fifteen days is a long time to keep your hands clean."),
            new SampleTitle("The Drowned King", Rarity.UNIQUE, "The temple has a new tenant.",
                    "Permanent water breathing (+50% swim speed)",
                    "Kill an Elder Guardian while under Mining Fatigue III and drowning",
                    "The temple has a throne. Its rules are drowning ones."),
            new SampleTitle("Warden Touched", Rarity.UNIQUE, "It let you go. Nine times you thought that was luck.",
                    "Sculk sensors and shriekers never detect you (+15% damage from stealth)",
                    "Escape a Warden after it has fully aggroed on you 10 times",
                    "It let you go. It keeps letting you go."));

    private static final List<SampleTitle> EQUIPPED = List.of(
            ALL_TITLES.get(12), ALL_TITLES.get(9), ALL_TITLES.get(7), ALL_TITLES.get(0));

    // Unique and Limited titles: always active, and they use no slot and no budget.
    private static final List<SampleTitle> ALWAYS_ACTIVE = List.of(
            ALL_TITLES.get(14), ALL_TITLES.get(15), ALL_TITLES.get(11), ALL_TITLES.get(16), ALL_TITLES.get(17),
            ALL_TITLES.get(18), ALL_TITLES.get(19), ALL_TITLES.get(20), ALL_TITLES.get(21), ALL_TITLES.get(22));

    // Theme: translucent dark blue frame with darker insets, light blue accents.
    private static final int FRAME_BORDER = 0xE00A1A2E;
    private static final int FRAME_FILL = 0xB0183A63;
    private static final int FRAME_LIGHT = 0xC05D93BA;
    private static final int FRAME_SHADOW = 0xC00C2038;
    private static final int INSET_BORDER = 0xD02C5A80;
    private static final int INSET_FILL = 0xB00E1E33;
    private static final int INSET_SHELF_FILL = 0xB00E2F3A;
    private static final int INSET_LIGHT = 0xA05D93BA;
    private static final int INSET_SHADOW = 0xD0060F1C;
    private static final int ACCENT = 0xA8D8F0;
    private static final int HEADING_TEXT = ACCENT;
    private static final int MUTED_TEXT = 0x6F8FA8;


    private static final int SLOT_COUNT = 10;
    private static final int PANEL_WIDTH = 320;
    private static final int PADDING = 8;
    private static final int ROW_HEIGHT = 12;
    private static final int LINE_HEIGHT = 10;
    private static final int HEADER_HEIGHT = 32;
    private static final int SHELF_LABEL_HEIGHT = 14;
    // The shelf grows up to this many rows, then scrolls. Matches the endgame veteran in 10-Balance-Model.csv.
    private static final int MAX_SHELF_ROWS = 6;
    // Panel height before any shelf rows are added.
    private static final int BASE_HEIGHT = HEADER_HEIGHT + SLOT_COUNT * ROW_HEIGHT + SHELF_LABEL_HEIGHT + PADDING;
    private static final int COLUMN_WIDTH = (PANEL_WIDTH - PADDING * 3) / 2;
    private static final int POPUP_WIDTH = 260;
    private static final int POPUP_PADDING = 7;
    private static final int SCREEN_PADDING = 4;

    // Clickable rows from the last frame, so clicks hit exactly what was drawn.
    private record RowHit(int x, int y, int w, SampleTitle title) {}

    private final List<RowHit> rowHits = new ArrayList<>();
    private int left;
    private int top;
    private int scrollOffset;
    private int shelfScrollOffset;
    // The title whose details popup is open, or null.
    private SampleTitle selected;

    // The shelf grows with the number of always-active titles, up to what fits on screen,
    // and scrolls past that.
    private int shelfRows;
    private int listHeight;
    private int visibleRows;
    private int panelHeight;

    // Last drawn popup bounds, for click handling.
    private int popupX;
    private int popupY;
    private int popupH;

    public TitlesScreen()
    {
        super(Component.translatable("screen.index_gestorum.titles"));
    }

    @Override
    protected void init()
    {
        left = (width - PANEL_WIDTH) / 2;
        int maxShelfRows = Mth.clamp((height - BASE_HEIGHT - SCREEN_PADDING) / ROW_HEIGHT, 1, MAX_SHELF_ROWS);
        shelfRows = Mth.clamp(ALWAYS_ACTIVE.size(), 1, maxShelfRows);
        listHeight = SLOT_COUNT * ROW_HEIGHT + SHELF_LABEL_HEIGHT + shelfRows * ROW_HEIGHT;
        visibleRows = listHeight / ROW_HEIGHT;
        panelHeight = BASE_HEIGHT + shelfRows * ROW_HEIGHT;
        top = (height - panelHeight) / 2;
    }

    private int leftColumnX()
    {
        return left + PADDING;
    }

    private int rightColumnX()
    {
        return left + PADDING * 2 + COLUMN_WIDTH;
    }

    private int listTop()
    {
        return top + HEADER_HEIGHT;
    }

    private int shelfTop()
    {
        return listTop() + SLOT_COUNT * ROW_HEIGHT + SHELF_LABEL_HEIGHT;
    }

    private int listWidth()
    {
        return maxScroll() > 0 ? COLUMN_WIDTH - 5 : COLUMN_WIDTH;
    }

    private int shelfWidth()
    {
        return maxShelfScroll() > 0 ? COLUMN_WIDTH - 5 : COLUMN_WIDTH;
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick)
    {
        renderBackground(graphics);
        rowHits.clear();

        // While the popup is open, the lists underneath don't react to the mouse.
        int rowMouseX = selected == null ? mouseX : -1;
        int rowMouseY = selected == null ? mouseY : -1;

        drawBevel(graphics, left, top, PANEL_WIDTH, panelHeight, FRAME_BORDER, FRAME_FILL, FRAME_LIGHT, FRAME_SHADOW);
        graphics.drawString(font, title, (width - font.width(title)) / 2, top + 7, HEADING_TEXT, false);

        int headerY = top + 20;
        graphics.drawString(font, Component.translatable("screen.index_gestorum.equipped"), leftColumnX() + 1, headerY, HEADING_TEXT, false);
        graphics.drawString(font, Component.translatable("screen.index_gestorum.all_titles"), rightColumnX() + 1, headerY, HEADING_TEXT, false);

        // Left: equip slots, then the always-active shelf.
        for (int i = 0; i < SLOT_COUNT; i++)
        {
            int y = listTop() + i * ROW_HEIGHT;
            drawRowBox(graphics, leftColumnX(), y, COLUMN_WIDTH, INSET_FILL);
            if (i < EQUIPPED.size())
            {
                drawTitleRow(graphics, EQUIPPED.get(i), leftColumnX(), y, COLUMN_WIDTH, rowMouseX, rowMouseY);
            }
            else
            {
                graphics.drawString(font, Component.translatable("screen.index_gestorum.empty_slot"), leftColumnX() + 13, y + 2, MUTED_TEXT, false);
            }
        }

        int shelfLabelY = listTop() + SLOT_COUNT * ROW_HEIGHT + 4;
        graphics.drawString(font, Component.translatable("screen.index_gestorum.always_active"), leftColumnX() + 1, shelfLabelY, HEADING_TEXT, false);
        int maxShelfScroll = maxShelfScroll();
        shelfScrollOffset = Mth.clamp(shelfScrollOffset, 0, maxShelfScroll);
        for (int i = 0; i < shelfRows; i++)
        {
            int y = shelfTop() + i * ROW_HEIGHT;
            drawRowBox(graphics, leftColumnX(), y, shelfWidth(), INSET_SHELF_FILL);
            int index = i + shelfScrollOffset;
            if (index < ALWAYS_ACTIVE.size())
            {
                drawTitleRow(graphics, ALWAYS_ACTIVE.get(index), leftColumnX(), y, shelfWidth(), rowMouseX, rowMouseY);
            }
            else if (ALWAYS_ACTIVE.isEmpty())
            {
                graphics.drawString(font, Component.translatable("screen.index_gestorum.shelf_empty"), leftColumnX() + 13, y + 2, MUTED_TEXT, false);
            }
        }
        if (maxShelfScroll > 0)
        {
            drawScrollbar(graphics, leftColumnX() + COLUMN_WIDTH - 3, shelfTop(), shelfRows * ROW_HEIGHT,
                    shelfRows, ALWAYS_ACTIVE.size(), shelfScrollOffset, maxShelfScroll);
        }

        // Right: every title, scrollable.
        drawBevel(graphics, rightColumnX(), listTop(), COLUMN_WIDTH, visibleRows * ROW_HEIGHT, INSET_BORDER, INSET_FILL, INSET_SHADOW, INSET_LIGHT);
        int maxScroll = maxScroll();
        scrollOffset = Mth.clamp(scrollOffset, 0, maxScroll);
        for (int i = 0; i < visibleRows && i + scrollOffset < ALL_TITLES.size(); i++)
        {
            SampleTitle t = ALL_TITLES.get(i + scrollOffset);
            int y = listTop() + i * ROW_HEIGHT;
            if (EQUIPPED.contains(t) || ALWAYS_ACTIVE.contains(t))
            {
                graphics.fill(rightColumnX() + 2, y + 2, rightColumnX() + listWidth() - 1, y + ROW_HEIGHT - 1, 0x3055FF55);
            }
            drawTitleRow(graphics, t, rightColumnX(), y, listWidth(), rowMouseX, rowMouseY);
        }
        if (maxScroll > 0)
        {
            drawScrollbar(graphics, rightColumnX() + COLUMN_WIDTH - 5, listTop() + 2, visibleRows * ROW_HEIGHT - 4,
                    visibleRows, ALL_TITLES.size(), scrollOffset, maxScroll);
        }

        super.render(graphics, mouseX, mouseY, partialTick);

        if (selected != null)
        {
            drawPopup(graphics);
            return;
        }

        SampleTitle hovered = hitAt(mouseX, mouseY);
        if (hovered != null)
        {
            Rarity rarity = hovered.rarity();
            graphics.renderTooltip(font, Component.literal(rarity.displayName()).withStyle(s -> s.withColor(rarity.color())), mouseX, mouseY);
        }
    }

    // Raised when light is top-left, sunken when light is bottom-right.
    private void drawBevel(GuiGraphics graphics, int x, int y, int w, int h, int border, int fill, int topLeft, int bottomRight)
    {
        // Outline rather than a full fill, so translucent fills don't stack on the border color.
        graphics.renderOutline(x, y, w, h, border);
        graphics.fill(x + 1, y + 1, x + w - 1, y + h - 1, fill);
        graphics.hLine(x + 1, x + w - 2, y + 1, topLeft);
        graphics.vLine(x + 1, y + 1, y + h - 1, topLeft);
        graphics.hLine(x + 1, x + w - 2, y + h - 2, bottomRight);
        graphics.vLine(x + w - 2, y + 1, y + h - 1, bottomRight);
    }

    private void drawRowBox(GuiGraphics graphics, int x, int y, int w, int fill)
    {
        drawBevel(graphics, x, y, w, ROW_HEIGHT, INSET_BORDER, fill, INSET_SHADOW, INSET_LIGHT);
    }

    private void drawTitleRow(GuiGraphics graphics, SampleTitle t, int x, int y, int w, int mouseX, int mouseY)
    {
        rowHits.add(new RowHit(x, y, w, t));
        if (isOver(mouseX, mouseY, x, y, w)) graphics.fill(x + 2, y + 2, x + w - 2, y + ROW_HEIGHT - 2, 0x30FFFFFF);
        if (t == selected) graphics.renderOutline(x + 1, y + 1, w - 2, ROW_HEIGHT - 2, 0xFFFFFFFF);
        t.rarity().drawPip(graphics, x + 4, y + 3);
        String name = font.plainSubstrByWidth(t.name(), w - 16);
        graphics.drawString(font, name, x + 13, y + 2, t.rarity().color());
    }

    // A section is a light blue label on its own line with white text wrapped under it.
    private record Section(Component label, List<FormattedCharSequence> lines) {}

    private void drawPopup(GuiGraphics graphics)
    {
        int textW = POPUP_WIDTH - POPUP_PADDING * 2;
        List<FormattedCharSequence> flavor = font.split(
                Component.literal(selected.flavor()).withStyle(ChatFormatting.WHITE, ChatFormatting.ITALIC), textW);
        List<Section> sections = new ArrayList<>();
        sections.add(section("screen.index_gestorum.bonus", selected.bonus(), textW));
        if (selected.hint() != null)
        {
            sections.add(section("screen.index_gestorum.hint", selected.hint(), textW));
        }
        else
        {
            sections.add(section("screen.index_gestorum.how_to_earn", selected.howToEarn(), textW));
        }

        // Height: header, divider, flavor, each section with a gap before it, then the footer.
        int contentH = 10 + 6 + flavor.size() * LINE_HEIGHT;
        for (Section s : sections) contentH += 6 + LINE_HEIGHT + s.lines().size() * LINE_HEIGHT;
        contentH += 6 + LINE_HEIGHT;
        popupH = contentH + POPUP_PADDING * 2;
        popupX = left + (PANEL_WIDTH - POPUP_WIDTH) / 2;
        popupY = Math.max(4, top + (panelHeight - popupH) / 2);

        graphics.pose().pushPose();
        // Above the rows and their text.
        graphics.pose().translate(0, 0, 400);

        // Dim the lists behind the popup.
        graphics.fill(left + 1, top + 1, left + PANEL_WIDTH - 1, top + panelHeight - 1, 0x90000000);
        drawBevel(graphics, popupX, popupY, POPUP_WIDTH, popupH, FRAME_BORDER, 0xF0142E4E, FRAME_LIGHT, FRAME_SHADOW);

        int x = popupX + POPUP_PADDING;
        int y = popupY + POPUP_PADDING;
        Rarity rarity = selected.rarity();
        rarity.drawPip(graphics, x, y + 1);
        graphics.drawString(font, selected.name(), x + 9, y, rarity.color());
        graphics.drawString(font, rarity.displayName(), popupX + POPUP_WIDTH - POPUP_PADDING - font.width(rarity.displayName()), y, rarity.color());
        y += 10;
        graphics.hLine(x, popupX + POPUP_WIDTH - POPUP_PADDING - 1, y + 1, INSET_BORDER);
        y += 6;

        for (FormattedCharSequence line : flavor)
        {
            graphics.drawString(font, line, x, y, 0xFFFFFF);
            y += LINE_HEIGHT;
        }
        for (Section s : sections)
        {
            y += 6;
            graphics.drawString(font, s.label(), x, y, ACCENT);
            y += LINE_HEIGHT;
            for (FormattedCharSequence line : s.lines())
            {
                graphics.drawString(font, line, x, y, 0xFFFFFF);
                y += LINE_HEIGHT;
            }
        }

        y += 6;
        Component footer = Component.translatable("screen.index_gestorum.close_prompt");
        graphics.drawString(font, footer, popupX + (POPUP_WIDTH - font.width(footer)) / 2, y, MUTED_TEXT, false);

        graphics.pose().popPose();
    }

    private Section section(String labelKey, String text, int width)
    {
        return new Section(Component.translatable(labelKey),
                font.split(Component.literal(text).withStyle(ChatFormatting.WHITE), width));
    }

    private void drawScrollbar(GuiGraphics graphics, int x, int trackTop, int trackHeight, int visible, int total, int offset, int maxOffset)
    {
        int thumbHeight = Math.max(6, trackHeight * visible / total);
        int thumbTop = trackTop + (trackHeight - thumbHeight) * offset / maxOffset;
        graphics.fill(x, trackTop, x + 3, trackTop + trackHeight, INSET_SHADOW);
        graphics.fill(x, thumbTop, x + 3, thumbTop + thumbHeight, 0xFF000000 | ACCENT);
    }

    private boolean isOver(double mouseX, double mouseY, int x, int y, int w)
    {
        return mouseX >= x && mouseX < x + w && mouseY >= y && mouseY < y + ROW_HEIGHT;
    }

    private SampleTitle hitAt(double mouseX, double mouseY)
    {
        for (RowHit hit : rowHits)
        {
            if (isOver(mouseX, mouseY, hit.x(), hit.y(), hit.w())) return hit.title();
        }
        return null;
    }

    private int maxScroll()
    {
        return Math.max(0, ALL_TITLES.size() - visibleRows);
    }

    private int maxShelfScroll()
    {
        return Math.max(0, ALWAYS_ACTIVE.size() - shelfRows);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button)
    {
        if (selected != null)
        {
            // Clicking outside the popup closes it; clicks inside do nothing.
            boolean inside = mouseX >= popupX && mouseX < popupX + POPUP_WIDTH && mouseY >= popupY && mouseY < popupY + popupH;
            if (!inside) selected = null;
            return true;
        }
        if (button == 0)
        {
            SampleTitle clicked = hitAt(mouseX, mouseY);
            if (clicked != null)
            {
                selected = clicked;
                return true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta)
    {
        if (selected != null) return true;
        int step = (int) Math.signum(delta);
        if (mouseX >= rightColumnX())
        {
            scrollOffset = Mth.clamp(scrollOffset - step, 0, maxScroll());
            return true;
        }
        if (mouseY >= shelfTop())
        {
            shelfScrollOffset = Mth.clamp(shelfScrollOffset - step, 0, maxShelfScroll());
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, delta);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers)
    {
        // Esc closes the popup first, then the screen.
        if (selected != null && keyCode == GLFW.GLFW_KEY_ESCAPE)
        {
            selected = null;
            return true;
        }
        // Same key that opens the screen also closes it.
        if (ClientEvents.OPEN_TITLES.matches(keyCode, scanCode))
        {
            onClose();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean isPauseScreen()
    {
        return false;
    }
}
