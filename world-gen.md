# World Generation Design — "Apex-Height" & "Shattered Lands"

> **Status**: Konzept — 2026-04-29
> Ziel: Eine vertikal (896 Blöcke) und horizontal (32.000+ Blöcke) massiv ausgedehnte Welt.

---

## Technische Parameter (Oberwelt)

| Parameter | Wert | Grund |
|-----------|------|-------|
| **Minimum Y** | -128 | Tieferer Untergrund für Dungeons & Mekanism |
| **Total Height** | 896 | Massiver Spielraum für Aeronautik |
| **Terrain Max Y** | ~512 | Massive Gebirgszüge via Tectonic |
| **Build Limit** | 768 | Viel Luftraum über den Gipfeln |
| **Sea Level** | 64 | Standard-Höhe |

---

## Die Zonen-Einteilung & Geologische Kernvision

### Geologische Kernvision: "Mantle Descent" & Void Buffer

Die Welt beginnt im Apex Mainland als scheinbar normale Oberwelt. Oberhalb der tiefen Schichten bleibt das Terrain klassisch und zusammenhängend, mit regulärem Landmassengefühl für frühen Basisbau, Erkundung und Infrastruktur. 

Es bietet gewöhnliches Terrain bis in die tiefen Schichten; unterhalb von Y=-64 bis etwa Y=-96 beginnt der Übergang in die Mantle-Zone. Diese zusätzliche Mantle-Zone ist ein Blackstone- und Magma-dominierter Layer, der geologisch wie ein Vorhof zum Nether wirkt. Diese Schicht dient als visuelle und spielerische Andeutung eines heißen Weltkerns, was einen mantelartigen, nethernahen Tiefenbiom andeutet, und ist der bevorzugte Bereich für seltene hitzeverwandte Ressourcen wie Ancient Debris.

Mit wachsender Distanz vom Spawn verändert sich nicht nur die Oberfläche, sondern auch die Stabilität des Untergrunds. In der Transitionszone wird das Terrain zunehmend flacher, zugleich heben sich Landmassen optisch stärker an und der unterste Abschluss der Welt beginnt zu zerbrechen: Das Bedrock wird nach außen hin schrittweise weggecarvt, sodass immer größere vertikale Öffnungen und Abbruchkanten entstehen.

Kurz vor dem eigentlichen Outer Rim erreicht die Welt eine Bufferzone. Diese Randzone ist kein vollwertiges Inselreich, sondern ein instabiler Vorbereich mit stark ausgedünntem Boden, Fragmentresten und einem klar spürbaren Void-Layer als Trennschicht. Erst hinter dieser Pufferzone beginnt der Shattered Outer Rim mit seinen vollständig entkoppelten, schwebenden Archipelen in mehreren Höhenlagen.

Diese Staffelung ist spielerisch stark, weil sie Progression nicht nur über Loot, sondern über Weltlogik vermittelt: je weiter außen, desto weniger "sicher" verhält sich die Welt als klassische Minecraft-Oberwelt.

### Zonierung konkret

| Zone | Distanz (ca.) | Charakter | Untergrund | Funktion |
|------|---------------|-----------|------------|----------|
| **1. Apex Mainland** | 0 - 10.000 | Normale, massive Overworld mit Hochgebirgen (Tectonic) bis Y=512 | Deepslate geht in Blackstone/Magma-Mantle über (-64 bis -96) | Sicherer Start, Tech-Aufbau, frühe Exploration |
| **2. Transition** | 10.000 - 28.000 | Flacher, instabiler, stärker zerklüftet | Bedrock wird abschnittsweise entfernt, große Tiefenrisse | Gefühl von geologischem Kollaps |
| **3. Bufferzone** | 28.000 - 32.000 | Fragmentierter Randbereich vor dem Absturz | Teilweise Void, Restboden, harte Bruchkanten | Dramatische Vorbereitung auf Outer Rim |
| **4. Shattered Outer Rim** | > 32.000 | Kein durchgehender Boden mehr. Schwebende Inseln in 3 Layern (Low-Orbit, Cloud-Layer, High-Apex) | Reiner Insel-/Void-Raum | Aeronautik, Endgame, Bosse, OP Loot |

---

## Performance & Pre-Generation Strategie

Um Lags beim schnellen Fliegen (Aeronautics) zu vermeiden:

### Pre-Generation (Chunky)
1.  **Core-PreGen**: 10.000er Radius um den Spawn komplett generieren.
2.  **Expedition-Corridors**: 32-Chunk breite vorgenerierte Pfade zu fernen Zielen (32km+).
3.  **Target-Gen**: Ziel-Archipele im 2km-Radius vorab generieren.

### Engine-Optimierung (NeoForge 1.21.1)
1.  **C2ME**: Multi-Core Chunk Management für die komplexen Density Functions.
2.  **Noisium**: Optimierte Noise-Berechnungen für On-the-fly Generierung.
3.  **Krypton Reno**: Netzwerk-Stack Optimierung gegen Rubberbanding.
4.  **Radium Reforged**: Logik-Optimierung (Physics/AI).
5.  **Distant Horizons**: LOD-Rendering für die 32km Fernsicht.

---

## Welt-Presets (Wählbar im Menü)

Um maximale Flexibilität zu bieten, definieren wir vier World Presets in `data/apex/worldgen/world_preset/`:

### 1. Preset: "Apex: Standard Modded"
*   **Beschreibung**: Die klassische Erfahrung mit Terralith & Tectonic.
*   **Höhe**: Standard (-64 bis 320).
*   **Feature**: Keine Shattered Lands.
*   **Zielgruppe**: Spieler, die eine "normale" Modded-Welt wollen.

### 2. Preset: "Apex: The Grand Journey" (Deine Vision)
*   **Beschreibung**: Das volle Paket. Massive Berge und weit entfernte Inseln.
*   **Höhe**: **896 Blöcke** (-128 bis 768).
*   **Feature**: **Shattered Lands ab 32.000 Blöcken**.
*   **Zielgruppe**: Das vorgesehene Modpack-Erlebnis.

### 3. Preset: "Apex: Skybound Only"
*   **Beschreibung**: Eine Welt, die nur aus schwebenden Inseln besteht.
*   **Höhe**: **896 Blöcke**.
*   **Feature**: Nur schwebende Insel-Layer von 0 bis 32.000+ km. Kein durchgehender Boden ("Void" unten).
*   **Zielgruppe**: Hardcore-Aeronauten.

### 4. Preset: "Apex: Eternal Mainland"
*   **Beschreibung**: Vertikale Extreme ohne Insel-Strukturen.
*   **Höhe**: **896 Blöcke**.
*   **Feature**: Massive Tectonic-Berge bis in alle Ewigkeit. Keine Shattered Lands.
*   **Zielgruppe**: Baumeister, die massiven Untergrund bevorzugen.

---

## Implementierungs-Details (KubeJS / Datapack)

### Technische Einordnung
Für die vertikale Basis dieser Vision ist entscheidend, dass benutzerdefinierte Dimensionen `min_y` und `height` in 16er-Schritten erlauben und die Gesamthöhe bis weit über Vanilla hinaus definierbar ist. 

Die eigentliche "nach außen zerfallende" Weltform wird nicht allein über `dimension_type`, sondern über Worldgen-Logik gelöst, etwa per Datapack/KubeJS plus Carver-/Biome-/Noise-Steuerung; NeoForge unterstützt dabei datengetriebene Eingriffe wie das Hinzufügen oder Entfernen von Carvern über Biome Modifier.

...

*   **Datenmenge**: Eine 32km Welt ist riesig.
    *   *Lösung*: Nur Korridore und wichtige Gebiete vorgenerieren, nicht die gesamte Fläche.
*   **Performance**: 896 Blöcke Höhe belasten die CPU.
    *   *Lösung*: **C2ME** und **Noisium** sind Pflicht.
*   **Dunkelheit**: Y=-128 ist tief und finster.
    *   *Lösung*: Apotheosis Nachtsicht-Items als Quest-Belohnung.
