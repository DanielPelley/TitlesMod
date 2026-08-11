# Titles — a LitRPG progression mod for Minecraft

Players earn persistent **Titles** for in-game accomplishments. Titles grant passive stat
bonuses and unique powers, come in rarities, and some are limited-quantity or unique per world.

**Status:** design phase. No code yet.

## Design docs

| File | What it is |
| --- | --- |
| `design/TitlesMod-Design.xlsx` | The design workbook — 9 sheets covering titles, effects, balance, UI, roadmap |
| `design/data/*.csv` | Source of truth for the workbook (pipe-delimited, so commas are safe in prose) |
| `design/build-workbook.ps1` | Regenerates the `.xlsx` from the CSVs. Requires Excel. |

Edit the CSVs, then:

```powershell
.\design\build-workbook.ps1
```

## Target platform

- **Minecraft 26.1** with NeoForge `26.1.2.93` (stable). 26.2 exists but NeoForge is
  beta-only there (`26.2.0.41-beta`) — upgrade once it stabilizes.
- **JDK 25** required by the NeoForge toolchain for this version line
- Mojang official mappings — 26.1+ is fully unobfuscated, no mapping layer
- Gradle + ModDevGradle

## Decisions locked in

- **10 equip slots**, capped by a **40-point power budget** (the budget, not the slot
  count, is what actually limits power)
- **Unique / Limited / Contested titles stack on top** — slot-exempt and budget-exempt,
  but capped at **5 points of raw stats each**; their rarity is expressed as a signature
  ability and public prestige, not big numbers
- **Hard per-attribute caps** as a second safety net
- **Multiplayer-first, single-player identical** — all logic server-authoritative, which
  the integrated server gives us for free
- **NeoForge only**

## Next decisions

See the **Open Questions** sheet — 3, 5, 6, 9 and 13 matter most now.
