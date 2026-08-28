# Build Plan — Index Gestorum

## Context

`TitlesMod` has ~9 months of design work and zero lines of code. The design is unusually
complete: 103 titles, 39 effect primitives, a three-layer balance model, a 48-task roadmap,
and 30 recorded decisions — all in `design/data/*.csv`, which the README declares the source
of truth.

What is missing is the bridge from design to build: a project that compiles, a package layout
that honours the port discipline the design already committed to, a verification strategy, and
resolution of the handful of blockers that would stop work on day one. This plan is that bridge.
It keeps the existing roadmap's phase numbering so it maps onto `08-Roadmap.csv` rather than
replacing it.

Intended outcome: a playable v1.0.0 on Minecraft 1.20.1 / Forge 47.x — ~12 titles, the equip
and budget model, the Titles screen, the scarcity ledger — that drops cleanly into Cisco's
Dragonfyre, Soulrend and Beyond Depth, and is architected so the eventual NeoForge port rewrites
adapters rather than the mod.

### Three blockers found while planning

1. **The mod id `titles` is unusable.** [Titles by Aurilux](https://www.curseforge.com/minecraft/mc-mods/titles)
   declares `mod_id=titles` for `minecraft_version=1.20.1` on Forge, has 8.6M downloads and ships
   in 411 modpacks. Two Forge mods with the same modid is a hard crash at load. Confirmed from
   [its `gradle.properties`](https://github.com/Aurilux/Titles), not the store slug.
   *Resolved: the mod is now **Index Gestorum**.*
2. **No JDK 17 on this machine** — only 23 and 24 in `C:\Program Files\Java`. The Overview sheet's
   "you already have 17 installed so nothing to do" is stale. *Resolved in Phase 0 below.*
3. **`the_unrivaled` had no implementable condition** without a duel subsystem that appears
   nowhere in the roadmap. *Resolved: re-spec to a deathless-streak record.*

---

## Decisions made this session

| # | Decision | Consequence |
|---|---|---|
| Q11, Q8 | Mod is **Index Gestorum**, mod id `index_gestorum` | Renames every namespace, path and doc. Do it before line one of code. |
| Q15 | **No duel system.** `the_unrivaled` becomes a deathless-streak record | Deletes a whole subsystem. Contested stays a supported scarcity type. |
| Q4, Q9, Q6 | **v1 data model stays flat** — no title levels, no synergies, no PvP scaling | Codec-backed records make all three additive schema changes later, not rewrites. |
| Risk order | **Spike before building** | Phase 0.5 answers Q19 with evidence before Phases 1–3 assume the answer. |
| Content | **Re-spec broken titles** rather than cut them | See the cleanup checklist. |

---

## Identity

| Field | Value |
|---|---|
| Display name | Index Gestorum |
| Mod id / namespace | `index_gestorum` |
| Java package root | `com.pelley.indexgestorum` |
| Datapack path | `data/index_gestorum/title/*.json` |
| Command root | `/index_gestorum`, alias `/ig` |
| Jar naming | `indexgestorum-1.20.1-0.1.0.jar` |

Verified free on Modrinth and CurseForge. Nearest neighbour is an unrelated emotes mod, "Gestus".
Reserve both project slugs early — before publishing, not at Phase 7.

---

## Phase 0 — Setup

**Toolchain.** Starting versions; confirm each at setup rather than trusting them.

- Minecraft `1.20.1`, Forge `47.1.3` compiled against, `versionRange="[47,)"` declared.
  Cisco's ships `47.4.13`, so compiling against an early 47.x keeps the whole line valid.
- ForgeGradle `6.0.+`, Gradle wrapper `8.8`. **Gradle 9.x will not work** — `9.0.0` is already
  in your wrapper cache, do not let the wizard pick it. `8.14.2` is also cached and worth trying
  if 8.8 gives trouble.
- Mappings: official channel + Parchment `2023.09.03-1.20.1` via the librarian plugin.
- Java 17, both for the toolchain **and the Gradle daemon**.

**The JDK 17 step — nothing for you to do.** Two layers, and only one is solved automatically:
Gradle's toolchain auto-provisioning downloads a compile JDK on its own, but the daemon launches
on whatever `java` is on PATH, which here is 24. ForgeGradle 6 is Java 17-era and manipulates
bytecode with old ASM; running its daemon on 24 produces obscure failures. So:

- Download the Adoptium 17 **zip** (no installer, no admin, no PATH change) to `C:\Users\danie\jdks\jdk-17`.
- Point at it with `org.gradle.java.home` in `~/.gradle/gradle.properties` — the *user* file, not
  the repo's, so the checkout stays portable for anyone else.
- Nothing else on the machine is touched. JDK 23/24 stay the default for everything else.

**Tasks**

| Task | Deliverable |
|---|---|
| Generate the ForgeGradle 6 project; confirm `runClient` launches | Empty mod loads in game |
| Repo hygiene: LICENSE, `mods.toml`, `pack.mcmeta`, updated README | Publishable-shaped repo |
| Pick a licence | Blocks Phase 7. MIT if you want pack authors to fork freely; ARR if you don't. |
| Design-data rename sweep `titles` → `index_gestorum` | CSVs, README, codex regenerated |

---

## Phase 0.5 — Attribute spike (NEW, ~1 day, throwaway)

The single highest-risk unknown in the whole design (Q19) cannot be resolved on paper, and
12 of 23 `ATTR_` effects are marked VERIFY. Both are cheap to answer now and expensive to
discover in Phase 5.

Build a deliberately disposable debug mod:

1. `/ig debug attrdump` — writes the entire `ForgeRegistries.ATTRIBUTES` registry to a file.
2. `/ig debug applyattr <attribute> <operation> <value>` — applies one modifier to the caller.

Run it in four environments and record what happens:

| Environment | Question it answers |
|---|---|
| Vanilla 1.20.1 + Forge | Which of the six `forge:` attributes actually exist; does `max_absorption`? |
| Cisco's Dragonfyre | Does Better Combat honour `attack_damage`? Does `attributeslib` register the 21 expected attributes? |
| Soulrend | **Does Epic Fight bypass vanilla attack attributes?** (Q19) |
| Beyond Depth | Read its manifest, close out the provisional rows on the Integration sheet |

**Deliverable:** `04-Effect-Catalog.csv` with every VERIFY cell replaced by a real answer, and a
written answer to Q19.

**Prerequisite on you:** the reference file records that Cisco's Dragonfyre was uninstalled after
its manifest was captured. This phase needs Cisco's, Soulrend and Beyond Depth installed, or at
minimum their zips downloaded for the manifests. That's the one thing I can't do for you.

**If Epic Fight does bypass attack attributes:** Soulrend needs a different lever, and combat
titles get re-specced toward defensive and utility effects for that pack. Finding this out here
costs a day. Finding it out in Phase 7 costs a re-balance of the whole combat category.

---

## Architecture

The design's port discipline dictates the layout: `core` must not import Forge, so the port
rewrites adapters instead of the mod. That constraint also buys plain JUnit tests over the
balance maths without a game running — a large practical win, not just a porting one.

```
com.pelley.indexgestorum
├─ IndexGestorum.java              @Mod entry, MODID constant
├─ core/                           ← ZERO net.minecraftforge imports below this line
│  ├─ title/     Title (record + Codec), Rarity, Scarcity, TitleId
│  ├─ effect/    TitleEffect (sealed) + EffectType registry + codecs
│  ├─ condition/ TitleCondition (sealed) + condition codecs
│  ├─ player/    TitleState (record + Codec)   ← the thing that survives the port
│  ├─ ledger/    ScarcityLedger (record + Codec)
│  ├─ balance/   BudgetCalculator, AttributeCapClamp
│  └─ net/       packets as records with handle(player)
├─ platform/                       the 6 interfaces, no more
│  └─ Registrar, Networker, PlayerStore, WorldStore, EventBridge, AttributeBridge
├─ forge/                          the ONLY package importing net.minecraftforge
├─ client/                         screens, HUD, keybind
└─ compat/attributeslib/           guarded, never referenced as a field type
```

**Non-negotiables carried from the design sheets:**

- Codecs for everything serialized. Never hand-write NBT. Never touch item NBT.
- **One** `AttributeModifier` per attribute per player, recomputed on every equipped-set change
  and *replacing* the previous one — not one modifier per title. Makes cap clamping plain
  arithmetic, makes unequip a single `removeModifier`, and means a crash can't leave orphaned
  buffs stacking.
- Modifier UUIDs derived via `UUID.nameUUIDFromBytes` from a ResourceLocation-shaped id in the
  title JSON, so content files survive the port untouched.
- **Never call `player.getAttributeValue()` to make a decision.** Sum our own contributions,
  clamp that sum, apply. This is what makes the mod safe in packs that already grant attributes.
- Everything server-authoritative. The client renders and never computes eligibility.
- Conditions key on **tags**, never hardcoded ids.

**Localization — a gap in the current design.** No sheet mentions lang keys; Display Name,
Flavor Text and Hint are treated as inline strings. Fix it in the schema from the start by typing
those three fields as `Component` via the vanilla component codec, so a title JSON can carry
either a literal or `{"translate": "..."}`. Costs nothing now; retrofitting means touching every
content file and every screen.

---

## Phases 1–7

Sequencing follows `08-Roadmap.csv`. Acceptance criteria are what "done" means, not just the
deliverable.

| Phase | Scope | Acceptance |
|---|---|---|
| **1 — Core** | Platform layer, codec-backed Title registry, effect system (ATTR_ family first), player capability, grant pipeline, commands, equip model, per-attribute caps | Titles load from JSON and log on startup; `/ig grant` works; state survives death, dimension change and relog; over-budget equips are refused **server-side** with a message |
| **2 — Progression** | Vanilla-stat conditions, event conditions, 200-tick sampler, candidate-set optimisation | Stone Breaker and Undead Bane earn themselves with no command; no measurable TPS cost at 20 players |
| **3 — UI** | Sync packets, Titles screen, budget meter, effect formatter, earn toast | Playable core loop; effect text is generated from effect data so tooltips cannot drift from behaviour |
| **4 — Scarcity** | Append-only ledger, synchronous check-then-claim, Contested transfer, ledger UI | Two players claiming in the same tick cannot double-grant; ledger survives restart |
| **5 — Content** | Starter roll, 12 titles as JSON, balance pass | Every new player starts with a title; one title per effect family shipped |
| **6 — Polish** | `TOOL_TIER_HAND` hooks, secret hints, config, HUD/nameplate, `attributeslib` compat | Mod loads and plays correctly with **every** integration absent — test that case explicitly, it's the common one |
| **7 — Release** | SP smoke test, dedicated-server test, compat pass, docs, publish | v1.0.0 on Modrinth and CurseForge |

**Phase 4 is where the technical risk concentrates.** The design's own answer is the right one:
the server tick is single-threaded, so check-then-claim in one synchronous block is genuinely
race-free. The failure mode is doing it async for no reason.

**Phase 6 keeps exactly two compat classes.** The Integration sheet's conclusion is correct and
worth defending: `attributeslib` and a clean scriptable `/ig grant` for FTB Quests. Everything
else is datapack JSON. Resist adding a third — each one is a port liability and JSON isn't.

---

## Content pipeline — a decision that needs making

The CSVs are the source of truth for *design*; JSON becomes the source of truth for *runtime*
at Phase 5. Those can silently diverge, and the repo already has three examples of exactly that
failure (two stale HTML pages and the workbook's colour map).

Generating JSON from CSV isn't possible as the data stands — the Magnitude column is prose
(`"+4.0 max health"`, `"8% double drop and +1 luck"`), not machine-readable.

**Recommendation:** don't build a fake generator. Hand-author the JSON, and add a validator to
`design/` in the same spirit as `build-codex.ps1`:

- every title id in `02-Titles.csv` has a JSON file, and vice versa
- every effect type referenced exists in `04-Effect-Catalog.csv`
- every rarity and scarcity value is in the enum
- power budget in JSON matches the CSV

Run it in CI and before every release. This catches the `ATTR_TEMPT_RANGE` class of bug
automatically instead of at load time.

---

## Testing

| Layer | What | Why it's possible |
|---|---|---|
| **JUnit** | Budget maths, cap clamping, codec round-trips, starter-roll weighting | `core` has no Forge imports — this is the payoff of the platform layer |
| **Forge GameTest** | grant → equip → attribute applied → unequip → removed; death/relog persistence; ledger claim under contention | Automatable, catches the orphaned-modifier bug |
| **Manual, SP** | Full playthrough; scarce titles against a one-player ledger | Integrated server means SP and MP share a code path |
| **Manual, dedicated** | Two clients; sync; contested transfer; ledger across restart | The mod is multiplayer-first |
| **Modpack drop-in** | Cisco's, Soulrend, Beyond Depth — **and standalone with nothing installed** | The standalone case is the one most users hit |

**Performance budget:** no measurable TPS cost at 20 players. Enforced by the design's own
evaluation model — titles register trigger families, only matching players re-check, earned
titles leave the candidate set permanently, one 200-tick sweep for samplers. Never stream over
all titles on every damage event; cache the equipped effect list per player.

**The orphaned `max_health` modifier is the classic way this genre corrupts saves.** Remove
modifiers on unequip *and* on logout-save, and cover both with a GameTest.

---

## Design-data cleanup

Do this alongside Phase 0, before the CSVs become JSON. Items 1–4 need your sign-off; the
rest are mechanical.

1. **`the_unrivaled`** → "Hold the longest deathless streak ever recorded on this server."
   Implementation: sample `Stats.TIME_SINCE_DEATH` on the 200-tick sweep, keep a personal-best
   counter in the player record, and also sample on `LivingDeathEvent` *before* the stat resets.
   Ledger stores holder plus best value. Keep the existing effects (gold tab-list name, +10%
   damage, 3 exempt). **Answers Q15 by deleting it.**
2. **`light_footed`** — currently `ATTR_SNEAKING_SPEED`, which the catalog cuts for v1, so it
   would ship doing nothing. Proposal: add a general **conditional-attribute wrapper** (an
   attribute effect gated on a state predicate) and implement this as movement_speed while
   crouching. Vanilla applies its sneak multiplier to final movement speed, so this works without
   a mixin — and the same primitive resolves `ATTR_SUBMERGED_MINING` and several
   below-Y/in-dimension titles. One primitive, several problems solved.
3. **`shepherd`** — `ATTR_TEMPT_RANGE` was never defined in the catalog at all, and 1.20.1 has no
   analogue (`TemptGoal` range is hardcoded). Proposal: drop the tempt-range half, keep
   "-30% breed cooldown" as CUSTOM via `BabyEntitySpawnEvent` resetting parent age. Effect summary
   becomes "Animals breed faster." Title still does something real and the flavour survives.
4. **`vein_seeker`** — the tweak queue flags it with the reason unrecorded. Likely the
   `ON_MINE_PROC` interaction with Silk Touch and Fortune. Decide or clear the entry.
5. `obsidian_willed` — drop the cut `ATTR_EXPLOSION_KB_RESIST`; the `-25% explosion damage` half
   stands on its own.
6. "All 9 scarce titles" is stale — there are 10 non-Unlimited titles.
7. "Secret" is documented as a flag orthogonal to rarity, but four titles use `Secret` as their
   *Category*. Pick one meaning.
8. `build-workbook.ps1`'s rarity colours disagree with `03-Rarity-and-Scarcity.csv` on Rare,
   Legendary and Mythic, and it doesn't know the Unique tier. Self-reported at HEAD, still unfixed.
9. `titles-reference.html` and `titles-records.html` are pre-retarget (Aug 20), inline frozen
   copies of the data, aren't regenerated by either script, and contradict current design.
   Delete them — the codex supersedes both.
10. Re-cost the 12 effects that became event hooks, per the catalog's own note.
11. Overview: "you already have 17 installed" is false.

---

## Risk register

| Risk | Severity | Mitigation |
|---|---|---|
| Epic Fight bypasses vanilla attack attributes | **High** | Phase 0.5 spike answers it before anything depends on it |
| Scarcity claim race / crash mid-claim | High | Synchronous check-then-claim on the server tick; append-only ledger; GameTest under contention |
| Orphaned attribute modifiers corrupt saves | High | One modifier per attribute, replaced not accumulated; remove on unequip and logout |
| Port cost balloons | Medium | Platform layer, codecs, derived modifier ids, no item NBT — all from day one |
| Content drift between CSV and JSON | Medium | Validator script in CI |
| Scope creep past the 12-title MVP | Medium | Ship 12 before writing 60; the roadmap already says so |
| FOMO/grief from Limited titles | Medium (design) | Never revoked; scarce titles capped at 5 points of raw stats; prestige not power |

---

## Still open

Not blocking, but they'll surface:

- **Q3** — titles visible to other players? Drives whether this is a personal sheet or a social
  system. Phase 6 needs an answer.
- **Q5** — vanilla-plus or overhaul? The 40-point budget assumes vanilla-plus. Answer before the
  Phase 5 balance pass.
- **Q10** — per-world or global scarcity on a multi-world server? Decides where the SavedData
  lives. Answer before Phase 4.
- **Q12** — the Balance Model's meta-titles (Collector/Archivist/Chronicler) arguably already
  answer this; the Open Questions sheet hasn't been updated to say so.

---

## Verification

End-to-end, in order:

1. `./gradlew runClient` — mod loads, no errors, `/ig` is present.
2. `/ig debug attrdump` — the attribute registry matches the Effect Catalog.
3. `/ig grant stone_breaker` → open the screen (K) → equip → confirm the health/damage change is
   real → unequip → confirm it's gone.
4. Die. Relog. Change dimension. Confirm earned and equipped sets survive all three.
5. Equip past 40 points — confirm the server refuses and messages you, and that the client never
   applied it optimistically.
6. Mine 2000 stone without any command — confirm Stone Breaker earns itself and toasts.
7. `./gradlew runServer` with two clients — claim a Unique title on both simultaneously; confirm
   exactly one wins. Restart; confirm the ledger persisted.
8. Drop the jar into Cisco's Dragonfyre and Soulrend — confirm attribute effects are real there.
9. Run with **no** integration mods present — confirm nothing breaks.
10. `./gradlew test` and the GameTest suite green.
