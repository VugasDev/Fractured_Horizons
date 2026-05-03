# Worldgen-Planung – Apex / Fractured Horizons

Status: Arbeitsdokument  
Stand: 2026-05-02

## Zielbild

Diese Datei beschreibt die gewünschte Zielvision für die Overworld-Generierung des Modpacks. Sie dient als zentrale Planungs- und Feinjustierungsdatei für die spätere technische Umsetzung als NeoForge-Mod mit konfigurierbaren World-Presets, anstatt kritische Overrides über Datapacks zu erzwingen. Grundlage sind die bereits definierten Konzepte für das Modpack und die geplante Apex-Worldgen mit vertikal stark ausgedehnter Welt, distanzbasierter Eskalation und mehreren klar unterscheidbaren Weltformen. [file:1][file:3]

## Leitidee

Die Welt soll eine großskalige, vertikale und horizontale Abenteuerkarte sein, die Create, Aeronautics, Exploration und technologische Progression unterstützt. Das Terrain soll nicht nur schön aussehen, sondern bewusst als Gameplay-System funktionieren: sichere Kernzone für frühen Basisbau, klar spürbare Distanz-Eskalation, extreme vertikale Räume für Flugmaschinen und kontrollierbare Parameter für Balancing und Performance. [file:1][file:3][file:5]

## Kernanforderungen

### Pflichtanforderungen

- Drei getrennte World Types / Presets. [file:1]
- **Shattered Lands** als Hauptvision. [file:1]
- **Skybound Only** als reine Inselwelt. [file:1]
- **Mainland Only** als durchgehende Landmasse ohne Shattered-Zerfall. [file:1]
- Kein globales Hijacking normaler Vanilla-Welten. [file:3]
- Kein durchgehender Boden in Skybound Only. [file:1]
- Alle relevanten Terrain-Parameter per Config einstellbar. [file:1]
- Presets müssen technisch unabhängig voneinander funktionieren. [file:1]
- Umsetzung als Mod statt als fragiles Datapack-Override-System. [file:3]

### Ziel für die Benutzererfahrung

- Presets sollen im Welterstellungsmenü klar auswählbar sein. [file:1]
- Unterschiede zwischen den Presets sollen sofort erkennbar und konsistent sein. [file:1]
- Die Welt soll für Aeronautics, große Sichtweiten und Langstreckenreisen funktionieren. [file:1][file:3]
- Die Terrainform soll einer klaren Designlogik folgen und nicht wie zufällig abgeschnittenes Vanilla-Terrain wirken. [file:1]

## Weltphilosophie

Die Overworld ist kein neutraler Container, sondern ein Progressionsraum. Nähe zum Spawn bedeutet Stabilität, Aufbau und Sicherheit, während größere Distanz Risiko, Leere, Vertikalität und Expedition signalisiert. [file:1][file:5]

Die Worldgen soll gleichzeitig Orientierung, geografisches Gameplay-Gating und spektakuläre Räume für Luftschiffe und Fernsicht liefern. Besonders wichtig ist dabei, dass die Formen der Welt lesbar bleiben: Übergänge sollen geplant wirken und nicht wie technische Artefakte. [file:1][file:3]

## Globale Zielparameter

Diese Werte beschreiben das aktuelle Wunschbild und bleiben später konfigurierbar.

| Parameter | Zielwert | Notiz |
|---|---:|---|
| Minimum Y | -128 [file:1] | Tiefer Untergrund für Dungeons und späte Ressourcen [file:1][file:7] |
| Terrain Max Y | 512 [file:1] | Hohe Gebirge und große Landmassen im Kernbereich [file:1] |
| Build Limit | 768 [file:1] | Viel Luftraum für Aeronautics und große Bauten [file:1][file:3] |
| Gesamthöhe | 896 [file:1] | Vertikaler Gesamtrahmen der Welt [file:1] |
| Sea Level | 64 [file:1] | Referenzwert, optional pro Preset überschreibbar [file:1] |
| Fernziel-Distanz | 32.000+ Blöcke [file:1] | Langstrecken-Exploration und Outer-Rim-Erlebnis [file:1] |

## Die drei World Types

### 1. Shattered Lands

Dies ist das Hauptpreset und das vorgesehene Kernerlebnis des Modpacks. Es kombiniert stabiles Mainland, eine gezielte Buffer-/Transition-Zone und schließlich einen vollständig aufgelösten Outer Rim mit Inselarchipelen. [file:1]

#### Designziel

Die Welt beginnt als feste, zusammenhängende Landmasse und verliert mit wachsender Distanz schrittweise ihre Stabilität. Nicht nur der Untergrund reißt auf, sondern auch die maximal erzeugte Terrainhöhe fällt allmählich ab, bis der Übergang in den Outer Rim ohne harte Bergbarrieren funktioniert. [file:1]

#### Spielererfahrung

- Early Game auf festem Mainland mit tragfähigem Untergrund. [file:1][file:5]
- Mid Game in einer zunehmend fragmentierten Übergangszone. [file:1]
- Late Game in einer stark ausgedünnten Inselwelt mit Void und sehr weiten Flugstrecken. [file:1][file:3]
- Ideales Zielbild für Expeditionen, Luftschiffe und Endgame-Reisen. [file:1][file:3]

#### Zonenlogik

##### Zone A – Mainland Core

- zusammenhängendes Terrain mit durchgehendem Untergrund. [file:1]
- hier soll ausdrücklich ein vollständiger, tragender Boden vorhanden sein. [file:1]
- dieser Bereich ist die stabile Kernwelt für Start, Basisbau, Industrie und klassische Infrastruktur. [file:1][file:5]
- hohe Berge und monumentale Formen sind hier erwünscht. [file:1]
- der Untergrund soll in dieser Zone ebenfalls stabil und vollständig sein; kein frühzeitiges Aufreißen, kein Void-Effekt. [file:1]

##### Zone B – Transition / Buffer Zone

- ab hier beginnt das Terrain nach und nach aufzureißen. [file:1]
- der Untergrund verliert schrittweise seine Geschlossenheit; feste Bodenanteile brechen zunehmend weg. [file:1]
- gleichzeitig soll die maximale Geländehöhe progressiv absinken, nicht per Hardcut, sondern über einen weichen Distanzverlauf. [file:1]
- Ziel ist ein breiter Ring aus topografisch flacherem Land, eher in Richtung Plains-Höhenprofil, damit hohe Gebirge den Übergang in die Buffer-Zone nicht blockieren. [file:1]
- große Berge sollen also nicht abrupt abgeschnitten werden, sondern nach außen hin natürlicher auslaufen, abflachen und sich förmlich nach oben wegkrümmen bzw. weggecarvt anfühlen. [file:1]

##### Zone C – Outer Rim

- hier gibt es keinen durchgehenden Boden mehr. [file:1]
- der Mainland-Untergrund endet vollständig; stattdessen existieren nur noch Inselcluster. [file:1]
- Void und große Leerräume sind hier das gewünschte Grundgefühl. [file:1]
- diese Zone ist klar vom Mainland getrennt und soll sich wie eine andere Weltphase anfühlen. [file:1]

#### Höhenlogik in Shattered Lands

Ein zentrales Merkmal dieses Presets ist nicht nur das Aufreißen des Bodens, sondern auch das kontrollierte Abfallen der maximal generierten Terrainhöhe in Richtung Outer Rim. Das bedeutet: Je weiter man sich vom Mainland entfernt, desto seltener und flacher wird normales zusammenhängendes Terrain. [file:1]

Dieses Absenken dient mehreren Zielen:

- Der Übergang von massivem Gebirge zu fragmentierter Welt wirkt natürlicher. [file:1]
- Der Spieler bekommt vor dem Outer Rim einen gut lesbaren, flacheren Ring statt blockierender Bergketten. [file:1]
- Luftschiff- und Flugrouten in Richtung Buffer- und Outer-Rim-Zone bleiben zugänglich. [file:1][file:3]

#### Insel-Layer

Die Insel-Layer sollen gleichzeitig existieren, aber nicht gleichmäßig überall verteilt sein. Entscheidend ist eine distanzabhängige Staffelung in Häufigkeit, Höhe, Größe und Leere zwischen den Inselgruppen. [file:1]

##### Low-Orbit Layer

- erste Inseln nahe am Übergang zum Mainland. [file:1]
- vergleichsweise niedrig gelegen. [file:1]
- häufiger als spätere High-End-Inseln, aber dennoch mit merklichem Abstand dazwischen. [file:1]
- dient als erste Stufe der Inselwelt. [file:1]

##### Mid / Cloud Layer

- weiter außen steigt die Wahrscheinlichkeit, dass Inseln höher liegen. [file:1]
- gleichzeitig sollen die Räume zwischen Inseln spürbar größer werden. [file:1]
- Inseln dürfen hier tendenziell größer und spektakulärer ausfallen. [file:1]

##### High-Apex Layer

- ganz außen und weiter entfernt vom Mainland sollen besonders hohe Inseln dominanter werden. [file:1]
- diese sollen seltener, größer und weiter voneinander getrennt sein. [file:1]
- gedacht für Endgame-Orte, besondere Strukturen, Bosse oder sehr abgelegene Ziele. [file:1]

#### Dichte- und Distanzregel für Inseln

- Je näher am Mainland, desto eher erscheinen niedrigere Inseln. [file:1]
- Je weiter vom Mainland entfernt, desto länger können Phasen völliger Leere sein. [file:1]
- Mit zunehmender Distanz nimmt die Chance auf höher gelegene und größer dimensionierte Inseln zu. [file:1]
- Das Gefühl soll nicht „ständig Inseln überall“ sein, sondern „viel Luftraum, dann punktuell markante Ziele“. [file:1]

### 2. Skybound Only

Dieses Preset soll eine kompromisslose Inselwelt sein. Es darf sich nicht wie ein abgeschnittenes Mainland anfühlen, sondern muss von Anfang an als echter Inselraum erzeugt werden. [file:1]

#### Pflichtmerkmale

- kein Mainland. [file:1]
- kein durchgehender Boden. [file:1]
- Void nach unten offen. [file:1]
- Inseln als Primärform der Welt, nicht als nachträglich freigeschnittene Reste. [file:1]
- geeignet für maximale Aeronautics-Fantasie und riskante Luftnavigation. [file:1][file:3]

#### Zielgefühl

- starke Leere zwischen den Inseln. [file:1]
- Flug, Brückenbau und Absturzvermeidung sind Kern des Erlebnisses. [file:1][file:3]
- optional kann eine sichere Spawnlogik nötig sein, damit das Preset nicht in Softlocks endet. [file:1]

### 3. Mainland Only

Dieses Preset ist das Gegenmodell zu Skybound Only und verzichtet vollständig auf den Shattered-Zerfall. [file:1]

#### Pflichtmerkmale

- zusammenhängende Landmasse. [file:1]
- keine Insel-Outer-Rim-Phase. [file:1]
- keine progressive Auflösung in Void-Archipele. [file:1]
- geeignet für Spieler, die extreme Höhen und große Kontinente wollen, aber keine zerrissene Inselwelt. [file:1]

#### Zielgefühl

- monumentale, dauerhaft tragende Welt. [file:1]
- ideal für große Bodenbasen, Schienen, Technik und klassische Expansion. [file:1][file:5]
- vertrauter als Shattered Lands, aber spektakulärer als Vanilla. [file:1]

## Terrain-Logik

### Distanz als Hauptsteuerung

Distanz vom Weltursprung oder Spawn ist die wichtigste Steuerachse. Über diese Achse werden Untergrundstabilität, Terrain-Maxhöhe, Übergangsintensität, Inselwahrscheinlichkeit und Insel-Layer-Gewichtung geregelt. [file:1]

Mögliche Distanzparameter:

- `mainlandRadius`
- `transitionStart`
- `transitionEnd`
- `outerRimStart`
- `fullIslandRadius`
- `heightFalloffStart`
- `heightFalloffEnd`

### Höhe als zweite Achse

Höhe regelt die vertikale Staffelung der Welt und ist eng mit Distanz gekoppelt. Das betrifft sowohl die Gebirgsoberkante im Mainland als auch die Layer-Verteilung der Inseln. [file:1]

Mögliche Höhenparameter:

- `minY`
- `terrainMaxYCore`
- `terrainMaxYTransition`
- `terrainMaxYOuter`
- `buildLimit`
- `seaLevel`
- `layer1MinY / layer1MaxY`
- `layer2MinY / layer2MaxY`
- `layer3MinY / layer3MaxY`

### Progressiver Height Falloff

Die maximale Terrainhöhe soll in Shattered Lands über Distanz weich absinken. Das ist ein Schlüsselaspekt der Vision und darf nicht als simples Abschneiden bestehender Berge umgesetzt werden. [file:1]

Zielbild:

- im Kern hohe Berge und monumentale Formen
- in der Buffer-Zone zunehmend abgeflachtes Top-Terrain
- vor dem Outer Rim ein eher plainsartiger Ring mit deutlich geringerer Höhenamplitude
- danach Auflösung in Inselwelt ohne zusammenhängenden Boden

## Untergrund-Logik

Die Untergrundlogik ist pro Zone unterschiedlich und muss dies auch klar widerspiegeln.

### Shattered Lands

- Zone A: durchgehender Untergrund. [file:1]
- Zone B: Untergrund reißt zunehmend auf. [file:1]
- Zone C: kein durchgehender Untergrund mehr, nur Inselsysteme. [file:1]

### Skybound Only

- nie durchgehender Untergrund. [file:1]
- nur Inseln und Void. [file:1]

### Mainland Only

- dauerhaft tragender Untergrund. [file:1]
- keine Auflösung in Shattered-/Void-Zonen. [file:1]

## Inselsystem

Die Inseln sind ein eigenes Worldgen-Subsystem und nicht bloß Restformen eines zerschnittenen Mainlands. Sie sollen bewusst mit großen Zwischenräumen, klaren Layern und distanzabhängiger Eskalation arbeiten. [file:1]

### Gestaltungsziele

- große Leerräume zwischen Inselgruppen. [file:1]
- nahe am Mainland eher niedrige Inseln. [file:1]
- weiter außen höhere, größere und seltenere Inseln. [file:1]
- nicht zu viele Inseln gleichzeitig sichtbar; Leere ist Teil der Dramaturgie. [file:1]

### Einstellbare Inselparameter

- `islandsEnabled`
- `islandMinSize`
- `islandMaxSize`
- `islandMinDistance`
- `islandMaxDistance`
- `islandGapMultiplierByDistance`
- `islandSizeMultiplierByDistance`
- `islandHeightBiasByDistance`
- `lowOrbitWeight`
- `midLayerWeight`
- `highApexWeight`
- `islandClusterChance`
- `islandUndersideSharpness`
- `islandTopFlattening`

## Biome- und Strukturphilosophie

Biome und Strukturen sollen die Terrainlogik unterstützen und niemals den Zonencharakter untergraben. Insbesondere die Buffer-Zone soll eher offene, flachere Oberflächen erlauben, damit der Übergangsring spielerisch und visuell funktioniert. [file:1]

Mögliche Regeln:

- Kernbiome mit stärkerem Relief im Mainland. [file:1]
- flachere, offenere Biome im Übergangsring. [file:1]
- Insel-Biome in Outer Rim und Skybound nicht wie normales Mainland wirken lassen. [file:1]
- Strukturen höhen- und zonensensibel unterdrücken oder bevorzugen. [file:1]

## Progressionsbezug

Die Worldgen trägt aktiv die Progression des Modpacks. Der sichere Kernbereich unterstützt den Einstieg, die Buffer-Zone bereitet Expeditionen vor, und die hochgelegenen sowie weit entfernten Inselräume liefern spätere Ziele für Luftschiffe, Endgame und besondere Orte. [file:1][file:3][file:5]

Die vertikale Weltstruktur unterstützt zugleich die geplante Ressourcenstaffelung, bei der tiefere Zonen, späte Bereiche und spezielle Räume unterschiedliche Bedeutung für Erze und Fortschritt haben. [file:7]

## Performance-Ziele

Die Welt ist groß und vertikal extrem. Deshalb müssen Inselhäufigkeit, Distanzräume und Layer-Verteilung kontrollierbar bleiben. Große Leerräume zwischen Inseln sind nicht nur atmosphärisch gewollt, sondern helfen auch, das äußere Gebiet nicht mit unnötiger Dichte zu überladen. [file:1]

Wichtige Grundsätze:

- klare Hauptzonen statt chaotischer Mikrovariation. [file:1]
- kontrollierbare Inseldichte und Layer-Gewichtung. [file:1]
- geeignet für Pre-Generation und Fernsichtsysteme. [file:1][file:3]
- spektakulär, aber nicht unlesbar oder technisch unnötig teuer. [file:1]

## Technische Zielarchitektur

Eine eigene NeoForge-Mod übernimmt Registrierung der World Presets, generator-spezifische Settings, zonenbasierte Terrainlogik, Height-Falloff, Insel-Layer und Config-Anbindung. Die Presets müssen wirklich unterschiedlich arbeiten und dürfen intern nicht über versteckte Fallbacks wieder zusammenfallen. [file:1][file:3]

Nicht-Ziele:

- keine fragile Komplettlösung über Datapack-Overrides allein. [file:3]
- keine globale Veränderung normaler Vanilla-Welten. [file:3]
- keine harten Berg-Cuts als billige Übergangslösung. [file:1]
- keine Inselwelt, die nur wie abgeschnittenes Terrain wirkt. [file:1]

## Config-Plan

### Oberste Ebene

- `worldType`
- `enableCustomDimensionType`
- `enableIslands`
- `enableMainland`
- `enableTransitionZone`
- `enableHeightFalloff`
- `enableOuterRim`
- `voidBelowTerrain`

### Distanz

- `mainlandRadius`
- `transitionStart`
- `transitionEnd`
- `outerRimStart`
- `fullIslandRadius`
- `heightFalloffStart`
- `heightFalloffEnd`

### Höhe

- `minY`
- `seaLevel`
- `terrainMaxYCore`
- `terrainMaxYTransitionMin`
- `terrainMaxYOuter`
- `buildLimit`
- `layer1MinY`
- `layer1MaxY`
- `layer2MinY`
- `layer2MaxY`
- `layer3MinY`
- `layer3MaxY`

### Inselparameter

- `islandMinSize`
- `islandMaxSize`
- `islandMinDistance`
- `islandMaxDistance`
- `islandGapMultiplierByDistance`
- `islandSizeMultiplierByDistance`
- `islandHeightBiasByDistance`
- `lowOrbitWeightNearMainland`
- `highApexWeightFarOuterRim`
- `islandClusterChance`
- `islandUndersideSharpness`
- `islandTopFlattening`

### Terrainform

- `densityThreshold`
- `mainlandDensity`
- `transitionCarveStrength`
- `terrainHeightFalloffStrength`
- `ridgeStrength`
- `erosionStrength`
- `undersideFalloff`
- `edgeFalloff`
- `noiseScale`

### Sicherheit und Strukturen

- `suppressGroundStructuresInVoid`
- `preferFlatBiomesInTransition`
- `forceSafeSpawn`
- `forceSafeSpawnIsland`
- `spawnPlatformRadius`
- `allowVoidRecoveryMechanic`

## Aktuelle Entscheidungsstände

### Bereits klar

- Shattered Lands ist das Hauptpreset. [file:1]
- Zone A braucht einen vollständigen tragenden Untergrund. [file:1]
- Zone B soll sowohl unten aufreißen als auch oben progressiv abflachen. [file:1]
- Zone C soll keinen durchgehenden Boden mehr besitzen. [file:1]
- Insel-Layer laufen gleichzeitig, aber mit distanzabhängiger Gewichtung. [file:1]
- Je weiter außen, desto länger können Insel-freie Leerräume sein. [file:1]
- Je weiter außen, desto größer und höher dürfen Inseln werden. [file:1]
- Übergänge sollen weich und natürlich aussehen, nicht abgeschnitten. [file:1]

### Noch offen

- exakte Radiuswerte für Zone A, B und C
- exakte Height-Falloff-Kurve
- genaue Gewichte für Low-Orbit, Mid und High-Apex nach Distanz
- Spawn-Sicherheitslogik in Skybound Only
- Biome-Regeln im Übergangsring
- Strukturregeln pro Zone und Layer

## Feintuning-Checkliste

### Shattered Lands
- Fühlt sich Zone A wirklich massiv und sicher an?
- Fällt in Zone B die Maximalhöhe weich genug ab?
- Entsteht ein natürlicher, flacher Übergangsring statt blockierender Berge?
- Ist Zone C wirklich frei von zusammenhängendem Boden?

### Inselsystem
- Sind nahe Inseln niedrig genug?
- Sind äußere Inseln hoch, groß und selten genug?
- Gibt es genug Leere zwischen den Zielen?
- Wirken die Inseln wie ein echtes System statt wie zufällige Reste?

### Gameplay
- Unterstützt die Welt frühe Basisstandorte?
- Fördert sie Luftfahrt und Expedition?
- Trägt sie die Progression sinnvoll mit?

### Technik
- Greifen Presets unabhängig?
- Kommen Distanz- und Höhenparameter wirklich im Generator an?
- Nutzt das Terrain echte Höhenabnahme statt Hardcuts?
- Erzeugt Skybound Only echte Inselwelt statt abgeschnittenes Mainland?

## Arbeitsnotizen

### Wunschbild in einem Satz

Eine riesige, vertikale Overworld mit drei klar unterscheidbaren Weltmodi, in der Distanz und Höhe als bewusstes Gameplay-System genutzt werden und der Weg vom stabilen Mainland über einen abgeflachten, aufreißenden Übergangsring bis in eine extrem luftige Inselwelt natürlich, lesbar und aeronautisch spielbar verläuft. [file:1][file:3]

### Kurzprofil der Presets

- **Shattered Lands** = stabiles Mainland, dann aufreißende und abflachende Transition, danach Outer Rim nur mit Inselclustern. [file:1]
- **Skybound Only** = kompromisslose Inselwelt ohne zusammenhängenden Boden. [file:1]
- **Mainland Only** = monumentale zusammenhängende Landmasse ohne Shattered-Zerfall. [file:1]

