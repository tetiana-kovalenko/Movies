# Verification

## Build commands

```sh
./gradlew :app:assembleDebug --offline
./gradlew :sdk:lintDebug :sdk:admob:lintDebug :sdk:launch:lintDebug --offline
./gradlew :checkQuality --offline
./gradlew :verifyProject --offline
```

Remove `--offline` when dependency/toolchain downloads are needed. `checkQuality` runs production Android lint. `verifyProject` also builds debug and unsigned release APKs. Reports are written under each module's `build/reports`. Builds require no configured OMDb key; real searches do.

## Manual scenarios

Search: short-query guidance, clearing input, delayed valid search, cancellation on replacement, initial loading, backend/network failures and page retry, empty successful results, pagination as the feed end approaches, retained cards during append failure, keyboard dismissal, poster fallback, and large fonts. Verify prompt/loading/error/empty/results previews use supplied state.

Navigation: Search/Favorites tab behavior, snapshot details from both sources, system/toolbar Back, retained Search state, entry-scoped Details movie after recreation, and scrolling at large font sizes.

Favorites: add/remove from each feature, observed membership synchronization, persistence after restart, empty collection, disabled controls before membership arrives, duplicate-tap suppression while writing, write-error retry, and read-error dismissal.

Native ads: use Google sample units. Confirm two load attempts on Search startup regardless of movie count. With filled ads, lists of 0, 1, or 2 movies must contain no ad rows; lists of 3 or more movies may show ads after the first and second movies. Check either slot filling alone: the remaining ad keeps its movie anchor. Confirm eligibility and mixed-row preparation run in SearchViewModel using the SDK policy, with the screen rendering supplied paged rows. Confirm late ad arrivals preserve loaded movies and do not repeat movie requests; retrying a failed page preserves its cursor, unique movies, and ad placements. Check moving between short and eligible result lists without reloading ads, omitted unfilled/unreachable placements, no additional requests on query changes, view reattachment after scrolling, preserved ad assets on theme changes, and cleanup when the Search ViewModel is cleared. Exercise a late load callback after close and existing expiry behavior on reattachment.

Interstitial: start with controlled local advertising history. The first cold launch is ineligible; the second is eligible. Confirm that preparation never blocks navigation, an early transition does not cancel preparation, and a later Details transition can offer the ready ad. Verify no-fill/load failure, resumed-host rejection, expiration, rapid repeated transitions, cancellation during an accepted overlay, and recording only after SDK presentation confirmation. Once recorded, subsequent launches stay ineligible. Do not change persisted keys or backup exclusions to exercise these scenarios.

## Recorded verification

Build verification covers production Android lint and debug/unsigned release APKs. Native display eligibility was checked at movie counts 0–4: 0–2 hide ads, 3–4 permit them, and placement indexes remain unchanged.

Device behavior, memory usage, frame times, fill rate and long-running expiry have not been remeasured. The manual scenarios above remain separate from build/static-analysis results.
