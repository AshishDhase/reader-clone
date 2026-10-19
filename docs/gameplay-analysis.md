# Gameplay research and independent specification

Date: 2026-10-19. This document separates publisher-described behavior from the independently designed Reading Adventure. No original Android runtime session was observed. Specifications below are intended behavior; implementation and test reports determine which requirements are verified.

## Publisher-described behavior

Originator describes lowercase word puzzles followed by sentence puzzles, with animated letters and words illustrating meaning. The educational focus is sight-word recognition and contextual usage. Six words are offered free, with more sold as packs. The design avoids scores, failure states and time pressure. Purchased words require connectivity and storage for acquisition. Those statements do not establish that every installed word works offline. [Publisher's Android listing](https://play.google.com/store/apps/details?id=com.originatorkids.EndlessReader&hl=en); [Originator's publisher feed](https://originatorkids.com/feed/atom/index.html).

## Requested observations: evidence ledger

| Topic | What is established | What remains unobserved |
|---|---|---|
| Word selection/progression | Packs are described | Selection layout, unlocking rules, saved progress, review scheduling |
| Letter dragging | Interactive letter puzzles described | Actual drag thresholds, finger offset, cancellation and pointer ownership |
| Matching | Lowercase spelling activity described | Tolerance, repeated-letter interchangeability and ordering rules |
| Character triggers | Meaning reinforced through animation | State transitions and trigger-to-animation timing |
| Correct/incorrect feedback | No failure pressure described | Drop feedback, correction motion and retry behavior |
| Word pronunciation | Not directly established by inspected publisher text | Voice source, repeat behavior, interruptions |
| Sentence pronunciation | Sentence puzzle described | Narration sequencing and highlight timing |
| Audio/animation sync | Not established | Audio clock, event timeline, callbacks, lip motion |
| Touch gestures | Not established | Single/multitouch, tap alternatives, pressure, gestures |
| Difficulty | Multiple packs described | Pedagogical order and adaptive difficulty |
| Child interface | Self-paced play described | Parent gate, accessibility targets, accidental navigation |
| Offline | Network needed for purchased-word acquisition | First launch and complete-play behavior without connectivity |

This ledger is intentionally conservative. iOS-specific behavior, store reviews, promotional screenshots and common practices in similar games are not substituted for measured Android behavior.

## Independent product goals

Reading Adventure teaches recognition, letter order and word use to children aged 3–6 through manipulation and immediate supportive responses. It uses its own vocabulary sentences, character geometry, colors, motion parameters and sounds. There are no accounts, advertisements, purchases, tracking, online content dependencies or external child-facing links.

Learning uses three stages: hear/see the word; assemble its letters; hear and see a short contextual sentence. A companion's movements reward exploration without implying that every word can be represented with a literal picture. Common irregular words should be taught through whole-word repetition and sentence context; letter cues must not be described as sufficient decoding instruction for every word.

## Session and state model

Intended states:

```text
Word selection → Introduction → Letter play → Completion → Sentence → Next/replay
                         ↘ Settings/pause → Restore current play state
```

1. **Selection:** show large word cards, level navigation and learned markers. Resume a current activity or choose an available word. Progression suggests a next challenge without monetized locks.
2. **Introduction:** display the completed word in lowercase and play its pronunciation. Bring movable letters into the tray. Input may begin without waiting for speech; interacting cancels obsolete introductory audio.
3. **Letter play:** preserve a stable outline/slot for each position and movable letter tokens. Each token has a unique ID independent of its character. The model owns placement; rendering is a projection of that state.
4. **Completion:** trigger once when every slot is occupied, lock already placed letters, celebrate briefly and pronounce the word. Save completion idempotently.
5. **Sentence:** present an original short sentence containing the learned word. Speak it and animate the independent companion. Expose replay and next buttons. A full sentence drag puzzle is a separately scoped enhancement, not required to claim the MVP's sentence-narration requirement.
6. **Next/replay:** replay the current word with fresh token positions or select the next suitable word. Never advance automatically while a child is interacting.

The pure model rejects illegal actions: dragging an already placed token, filling an occupied slot, placing a different character, counting the same word completion twice or accepting stale audio callbacks from another word.

## Touch and matching specification

Use a single captured pointer for each drag. Record the finger-to-token offset at press and retain it through movement to avoid jumping. Ignore additional pointers until release/cancel. Map physical coordinates to logical playfield coordinates consistently after orientation changes.

Hit targets should be at least 48 dp; favor 64 dp or more for primary child controls. Letter shapes and matching targets remain visually distinct from the background. Pointer movement lifts and slightly enlarges the token; a matching open slot may glow. On release, choose the closest eligible slot inside a generous radius. Commit only when its expected lowercase character equals the token character and the slot is unoccupied. Repeated letters are interchangeable by character, while token IDs prevent duplication.

Correct release commits model placement once and animates the token to its slot. Wrong/outside release returns it gently to its tray position and keeps the activity playable, with no score loss. Cancellation returns the token safely without feedback implying an error. Tap-to-select/tap-to-place is a useful accessibility option when present and must obey the same matching rules.

Acceptance cases: outside drop, mismatched character, occupied slot, duplicate letter tokens, rapid repeated release, cancel during drag, orientation change during drag, next-word navigation during animation, and overlapping touch pointers. Hit-test policy must be covered by unit tests rather than only by visually successful dragging.

## Animation specification

Original companions are drawn from code-native shapes so every visual is bundled and can scale cleanly. Use a single frame clock with elapsed time, independent of screen refresh rate. Each letter can have position, scale X/Y, rotation and state-specific timing; each companion can have idle, attentive, encouraging and celebrating states.

| Event | Intended motion | Independent design target |
|---|---|---|
| Idle | Breathing/sway, occasional blink | Low amplitude, distinct phase per character |
| Pointer down | Lift, stretch, small rotation | Immediate visual response |
| Drag | Follow pointer with retained offset | No main-thread I/O |
| Correct drop | Ease to slot, brief squash/stretch | Approximately 200–400 ms |
| Wrong drop | Gentle return | Approximately 200–350 ms; no punitive shake |
| Final match | Bounce/wave, capped confetti | Approximately 1–2 seconds |
| Narration | Companion mouth/attention movement | Driven by speech state, not inferred from arbitrary word duration |
| Pause/resume | Freeze or reset transient motion safely | No large delta-time leap |

These timings are design choices, not recovered values. Limit particle counts and reuse simple geometry. Prefer transient draw-state updates to full screen recomposition every frame. Target a 16.7 ms frame budget at 60 Hz, but claim 60 FPS only after measurement on the target device. Test at both logical orientations and at the device's actual refresh-rate setting.

## Speech, sound and synchronization

Provide an audio interface with separate requests for letter cues, words, sentences, reinforcement, interaction and companion effects. The implementation may initially use Android TextToSpeech and original synthesized effects; recorded licensed/original assets can later implement the same interface.

Each narration request gets a monotonically changing generation or utterance identifier. Publish states such as unavailable, ready, speaking, stopped and error. Start the speaking animation on the engine's actual start callback and end it on done/error/stop. Ignore callbacks for obsolete identifiers. Queue word then sentence in a controlled sequence; a replay replaces obsolete narration. Do not mix several TTS voices or queue an unbounded stream while dragging.

Letter names and phonemes differ. Generic TTS text such as a letter or an English approximation cannot guarantee an isolated phoneme. Store explicit phonetic cues as educational hints, report this limitation, and make future recorded phonemes replaceable without changing game logic. Validate pronunciation with an adult reviewer before claiming phonics quality. Sentence word highlighting requires range callbacks or recorded alignment metadata; generic speech-active animation alone is not word-level synchronization.

Choose installed English voices that declare no network requirement. Handle missing local speech data with visible parent guidance and continue silent play; do not silently switch to a network voice. A TTS-only app cannot promise spoken offline play on every fresh device without verifying offline voice data. If full offline speech at first install is required independently of device configuration, bundle original or appropriately licensed recordings. Sound volume is an app-level preference and must affect both speech and effects. Stop audio when backgrounded and release the speech engine when its owner is destroyed.

## Content and progression

Bundle at least 100 age-appropriate words with unique IDs, lowercase text, level, pronunciation cue, original example sentence, animation configuration and optional character association. Use local JSON with explicit schema/version and pack metadata. Validate duplicate IDs, unsupported symbols, missing sentences, difficulty bounds and sentence word inclusion. Load and validate off the main thread before starting play.

Suggested progression begins with short familiar words and expands to longer words and common function words. Word length alone is not a complete difficulty metric; irregularity and sentence familiarity matter. Track unique completed IDs locally, propose the next uncompleted word and allow revisiting learned material. Repeated play does not inflate unique progress. Persist completion atomically; restore it after process restart. Adding a pack must preserve existing IDs and progress. Do not lock content behind payment or an account.

## Child and parent navigation

Keep home, repeat and next controls consistent. Prioritize symbols with meaningful accessibility labels and minimal reading burden. Present parent settings behind a challenge intended for adults; it is an accidental-access deterrent, not a security boundary. Settings include volume and any supported learning/accessibility options. Do not present email, stores, browser links or purchase controls in the child flow.

Portrait and landscape should retain the current word and placement progress, relayout token positions, cancel stale drags and avoid UI under cutouts/system bars. Respect readable contrast and avoid relying on color alone for success. No timer, ranking, loss animation or forced speed requirement.

## Verification contract

Unit tests must exercise matching, occupied slots, repeated letters, completion idempotence, progression/persistence serialization, narration stale-callback rejection, and bounded animation state. Instrumented UI tests should exercise navigation, parent gate behavior, semantics and at least one letter-placement interaction. A host JVM pass cannot establish Android touch behavior or TTS output.

On the approved Galaxy S23 Ultra installation, verify cold launch, a full word, wrong drop, repeat, next, sentence, orientation, background/resume and airplane-mode play. Collect crash logs, frame statistics and memory before/after repeated sessions. Separate measured evidence from acceptance targets. Report missing voice data, unexecuted device tests, or unmeasured performance explicitly rather than declaring them passed.
