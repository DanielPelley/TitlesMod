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

- **Minecraft 1.20.1** on **Forge 47.x**. Not a preference — every mod this one is built
  to integrate with is Forge 1.20.1 only, and that is the one constraint that can't be
  solved in code.
- **JDK 17** — 1.20.1 will not build on a newer toolchain
- Official (Mojang) mappings + Parchment for parameter names. 1.20.1 is obfuscated.
- Gradle + ForgeGradle 6

### Port plan

Ship 1.20.1 first, then make **one** jump straight to whatever NeoForge version is current
and stable at that point. No intermediate versions — nobody plays 1.20.4 or 1.21.3, and
porting five times multiplies the worst breaking-change era in modding history by five.

That jump crosses a loader change *and* five years of API churn at once, so the 1.20.1 code
is written for it from day one:

- **Codecs for everything serialized.** Never hand-write NBT.
- **Player state lives in a plain record**; the Forge Capability is only a holder. The same
  record drops into a NeoForge Data Attachment later.
- **Packets are records** with a codec and a `handle(player)` method. The `SimpleChannel`
  registration is throwaway glue.
- **Attribute modifiers are keyed by a ResourceLocation-shaped id** in the title JSON, with
  the 1.20.1 UUID derived from it — so content files never need touching.
- **Never touch item NBT.** 1.20.5 replaced it with data components.
- **All Forge calls sit behind a ~6-interface platform layer.** The port rewrites the
  adapters, not the mod.

Two long-lived branches, cherry-picked forward. No shared `main` that merges into both.

## Decisions locked in

- **10 equip slots**, capped by a **40-point power budget** (the budget, not the slot
  count, is what actually limits power)
- **Unique / Limited / Contested titles stack on top** — slot-exempt and budget-exempt,
  but capped at **5 points of raw stats each**; their rarity is expressed as a signature
  ability and public prestige, not big numbers
- **Hard per-attribute caps** as a second safety net
- **Multiplayer-first, single-player identical** — all logic server-authoritative, which
  the integrated server gives us for free
- **Forge on 1.20.1, NeoForge after the port** — one loader at a time, never two at once

## Next decisions

See the **Open Questions** sheet — **16** (which mods, exactly?) now blocks the most,
followed by 3, 5, 6, 9 and 13.
