# Build Plan — Index Gestorum

A LitRPG titles mod for **Minecraft 1.20.1 / Forge 47.x**. The design is done and lives in
`design/data/*.csv`. This plan is the build order: each step is a thing that works when it's
finished, and each one is only useful if the one before it is done.

Forge API reference for 1.20.1: `design/forge-1.20.1.md`.

| Field | Value |
|---|---|
| Mod name | Index Gestorum |
| Mod id | `index_gestorum` |
| Package | `com.pelley.indexgestorum` |
| Command | `/index_gestorum`, alias `/ig` |

---

## 0 — Machine setup *(Daniel)*

Get JDK 17 on the machine and make Gradle use it. 1.20.1 will not build on 23 or 24.

**Done when:** `./gradlew --version` reports Java 17.

---

## 1 — Base mod

Generate a Forge 1.20.1 project. Correct mod name, id and package. No features.

**Done when:** `./gradlew runClient` opens Minecraft, the mods list shows Index Gestorum,
and the log has a line we printed.

---

## 2 — Title screen wireframe

A screen bound to a key. Hardcoded fake titles — a list, a detail pane, equipped slots,
a budget meter. No real data behind it. This is where the layout gets argued with early,
while it's cheap to change.

**Done when:** press the key in game, the screen opens and looks like the thing we want.

---

## 3 — Event detection proof of concept

Kill a creeper, get a chat message. That's it. It proves we can hook a game event, read who
did it and what died, and act on it — which is the whole earning mechanism in miniature.

**Done when:** killing a creeper prints to chat; killing a zombie doesn't.

---

## 4 — Title ledger

The core. Per-player state that tracks stats and conditions, decides when a title is earned,
and remembers it.

- A title definition loaded from JSON (id, name, condition, effects, cost).
- A player record: earned titles, equipped titles.
- Conditions checked against events (step 3) and against vanilla stats on a slow timer.
- Effects applied on equip, removed on unequip.
- Saved and restored.

**Done when:** mine some stone with no commands typed, earn a title, equip it, feel the
effect, unequip it, feel it go away — then die, relog, change dimension, and the state is
still right.

---

## 5 — Wire the screen to the ledger

Replace step 2's fake data with real data from step 4. Screen shows what's earned, what's
locked and how close it is, what's equipped and what the budget allows. Equipping goes
through the server. A toast when a title is earned.

**Done when:** the whole loop is playable without typing a command.

---

## 6 — Content

Author the first batch of titles as JSON from the design CSVs. Balance pass. Every new
player starts with one.

**Done when:** ~12 titles ship and each effect family has at least one title using it.

---

## 7 — Scarcity

Unique and limited titles: a world-level ledger of who holds what, claimed once, first come
first served.

**Done when:** two players claiming the same unique title at the same moment — exactly one
gets it, and it survives a server restart.

---

## 8 — Release

Single-player pass, dedicated-server pass, then drop the jar into the target modpacks and
into a world with nothing else installed.

**Done when:** v1.0.0 is published.
