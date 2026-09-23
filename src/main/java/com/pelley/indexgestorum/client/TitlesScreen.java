package com.pelley.indexgestorum.client;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;

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
                    "Eradication of these fiends is the only solution..."));

    private static final List<SampleTitle> EQUIPPED = List.of(
            ALL_TITLES.get(12), ALL_TITLES.get(9), ALL_TITLES.get(7), ALL_TITLES.get(0));

    // Unique and Limited titles: always active, and they use no slot and no budget.
    private static final List<SampleTitle> ALWAYS_ACTIVE = List.of(ALL_TITLES.get(14), ALL_TITLES.get(15), ALL_TITLES.get(11));

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
    private static final int DETAIL_HEIGHT = 52;
    // Panel height before any shelf rows are added.
    private static final int BASE_HEIGHT = HEADER_HEIGHT + SLOT_COUNT * ROW_HEIGHT + SHELF_LABEL_HEIGHT + 4 + DETAIL_HEIGHT + PADDING;
    private static final int COLUMN_WIDTH = (PANEL_WIDTH - PADDING * 3) / 2;

    // Clickable rows from the last frame, so clicks hit exactly what was drawn.
    private record RowHit(int x, int y, int w, SampleTitle title) {}

    private final List<RowHit> rowHits = new ArrayList<>();
    private int left;
    private int top;
    private int scrollOffset;
    private SampleTitle selected;

    // The shelf grows with the number of always-active titles, up to what fits on screen.
    private int shelfRows;
    private int listHeight;
    private int visibleRows;
    private int panelHeight;

    public TitlesScreen()
    {
        super(Component.translatable("screen.index_gestorum.titles"));
    }

    @Override
    protected void init()
    {
        left = (width - PANEL_WIDTH) / 2;
        int maxShelfRows = Math.max(1, (height - BASE_HEIGHT) / ROW_HEIGHT);
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

    private int listWidth()
    {
        return maxScroll() > 0 ? COLUMN_WIDTH - 5 : COLUMN_WIDTH;
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick)
    {
        renderBackground(graphics);
        rowHits.clear();

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
                drawTitleRow(graphics, EQUIPPED.get(i), leftColumnX(), y, COLUMN_WIDTH, mouseX, mouseY);
            }
            else
            {
                graphics.drawString(font, Component.translatable("screen.index_gestorum.empty_slot"), leftColumnX() + 13, y + 2, MUTED_TEXT, false);
            }
        }

        int shelfLabelY = listTop() + SLOT_COUNT * ROW_HEIGHT + 4;
        graphics.drawString(font, Component.translatable("screen.index_gestorum.always_active"), leftColumnX() + 1, shelfLabelY, HEADING_TEXT, false);
        int shelfTop = listTop() + SLOT_COUNT * ROW_HEIGHT + SHELF_LABEL_HEIGHT;
        // If there are more titles than fit, the last row says how many are hidden.
        int hidden = ALWAYS_ACTIVE.size() - shelfRows;
        int shown = hidden > 0 ? shelfRows - 1 : ALWAYS_ACTIVE.size();
        for (int i = 0; i < shelfRows; i++)
        {
            int y = shelfTop + i * ROW_HEIGHT;
            drawRowBox(graphics, leftColumnX(), y, COLUMN_WIDTH, INSET_SHELF_FILL);
            if (i < shown)
            {
                drawTitleRow(graphics, ALWAYS_ACTIVE.get(i), leftColumnX(), y, COLUMN_WIDTH, mouseX, mouseY);
            }
            else if (hidden > 0)
            {
                graphics.drawString(font, Component.translatable("screen.index_gestorum.shelf_more", hidden + 1), leftColumnX() + 13, y + 2, MUTED_TEXT, false);
            }
            else
            {
                graphics.drawString(font, Component.translatable("screen.index_gestorum.shelf_empty"), leftColumnX() + 13, y + 2, MUTED_TEXT, false);
            }
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
            drawTitleRow(graphics, t, rightColumnX(), y, listWidth(), mouseX, mouseY);
        }
        if (maxScroll > 0) drawScrollbar(graphics, rightColumnX() + COLUMN_WIDTH - 5, maxScroll);

        drawDetails(graphics);

        super.render(graphics, mouseX, mouseY, partialTick);

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

    private void drawDetails(GuiGraphics graphics)
    {
        int x = left + PADDING;
        int y = listTop() + listHeight + 4;
        int w = PANEL_WIDTH - PADDING * 2;
        drawBevel(graphics, x, y, w, DETAIL_HEIGHT, INSET_BORDER, INSET_FILL, INSET_SHADOW, INSET_LIGHT);

        int textX = x + 6;
        int textW = w - 12;
        if (selected == null)
        {
            graphics.drawCenteredString(font, Component.translatable("screen.index_gestorum.select_prompt"),
                    x + w / 2, y + (DETAIL_HEIGHT - 8) / 2, MUTED_TEXT);
            return;
        }

        Rarity rarity = selected.rarity();
        rarity.drawPip(graphics, textX, y + 6);
        graphics.drawString(font, selected.name(), textX + 9, y + 5, rarity.color());
        graphics.drawString(font, rarity.displayName(), x + w - 6 - font.width(rarity.displayName()), y + 5, rarity.color());
        graphics.hLine(x + 4, x + w - 5, y + 15, INSET_BORDER);

        List<FormattedCharSequence> lines = new ArrayList<>();
        lines.addAll(font.split(Component.literal(selected.flavor()).withStyle(ChatFormatting.WHITE, ChatFormatting.ITALIC), textW));
        lines.addAll(font.split(labelled("screen.index_gestorum.bonus", selected.bonus()), textW));
        if (selected.hint() != null)
        {
            lines.addAll(font.split(labelled("screen.index_gestorum.hint", selected.hint()), textW));
        }
        else
        {
            lines.addAll(font.split(labelled("screen.index_gestorum.how_to_earn", selected.howToEarn()), textW));
        }

        int lineY = y + 19;
        int maxLines = (DETAIL_HEIGHT - 21) / LINE_HEIGHT;
        for (int i = 0; i < lines.size() && i < maxLines; i++)
        {
            graphics.drawString(font, lines.get(i), textX, lineY, 0xFFFFFF);
            lineY += LINE_HEIGHT;
        }
    }

    private Component labelled(String labelKey, String text)
    {
        return Component.translatable(labelKey).withStyle(s -> s.withColor(ACCENT))
                .append(Component.literal(" " + text).withStyle(ChatFormatting.WHITE));
    }

    private void drawScrollbar(GuiGraphics graphics, int x, int maxScroll)
    {
        int trackTop = listTop() + 2;
        int trackHeight = visibleRows * ROW_HEIGHT - 4;
        int thumbHeight = Math.max(10, trackHeight * visibleRows / ALL_TITLES.size());
        int thumbTop = trackTop + (trackHeight - thumbHeight) * scrollOffset / maxScroll;
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

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button)
    {
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
        if (mouseX >= rightColumnX())
        {
            scrollOffset = Mth.clamp(scrollOffset - (int) Math.signum(delta), 0, maxScroll());
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, delta);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers)
    {
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
