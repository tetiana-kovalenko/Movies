# Architecture

Movies groups functionality by feature and keeps storage, networking, and presentation beside their owning contracts. The application provides composition and navigation; reusable UI and movie models live in core.

## Module boundaries

```text
app -> feature:{search,favorites,details}:{domain,impl}, core:{domain,ui}
app -> sdk, sdk:admob, sdk:launch
feature:search:impl -> feature:search:domain, feature:favorites:domain, core:{domain,ui}, sdk
feature:favorites:impl -> feature:favorites:domain, core:{domain,ui}
feature:details:impl -> feature:details:domain, feature:favorites:domain, core:{domain,ui}
feature domains -> core:domain, Navigation 3 runtime where destination keys require it
core:ui -> core:domain, Compose, Coil
core:domain -> Kotlin, coroutines
sdk -> Compose UI, coroutines
sdk:admob -> sdk, Google Mobile Ads
sdk:launch -> sdk, DataStore
```

Domain modules expose models and contracts needed by consumers, navigation keys, and relevant mapping functions. They use Android library conventions for Navigation 3 keys. `core:domain` remains plain Kotlin. Implementation modules use `movies.android.feature`; Retrofit declarations, DTOs, Room declarations, repository implementations, and use-case implementations stay internal. Hilt bindings live in each owning implementation module. Public route/ViewModel factory entry points allow the app to compose destinations.

## Navigation and Details

MovieNavigator handles back-stack mutations. One saved Navigation 3 back stack starts at Search, with saveable entry state and entry-scoped ViewModel stores. Selecting Favorites pushes it; selecting Search pops Favorites. The bottom bar appears on the two roots. Details is pushed from Search or Favorites and Back returns to its source.

DetailsKey contains MovieDetailsArgs, a serializable snapshot of the selected movie. Public mapping functions convert between it and Movie; shared domain models have no navigation serialization. The destination creates DetailsViewModel through its Hilt assisted factory. The ViewModel owns the movie and exposes it in DetailsUiState with `isFavorite: Boolean?`, `isSavingFavorite`, and message/action state. Details does not fetch additional backend data.

## Search

SearchQuery represents either a prepared title or an input below the minimum length. CatalogSearchPolicy trims input and requires three characters. CatalogPaginationPolicy owns page size 10, range 1–100, and continuation. SearchViewModel replaces the pending delay/Pager when the prepared query changes, waits 300 ms, and caches Paging data in its scope.

SearchRequest carries the query and an immutable SearchCursor containing the source page and previously seen movie IDs. DefaultSearchMoviesUseCase validates the request, deduplicates movies, and returns SearchFeedPage with movie/ad-anchor entries, the next cursor, and the distinct movie count. SearchPagingSource only folds prepared page Results into Paging pages or load errors. Failed-page retry retains the same cursor; cancellation propagates.

SearchViewModel combines query-scoped cached pages, ad readiness, and SDK display eligibility. It filters unavailable ad anchors and maps the remaining feed into SearchListItem rows. The cache permits ad availability changes to reuse loaded pages. The two source slots are anchored after the first and second movies. Unfilled slots are omitted without moving the remaining slot to a different movie anchor.

SearchRoute collects one composite Paging stream and input/favorite state. It passes Paging load states and item count to a pure ViewModel projection that returns prompt/loading/error/empty status and pagination feedback; this does not update stored state or return movie snapshots. The ViewModel confirms empty results only after a successful terminal first page. The ViewModel resolves resource messages and backend text into strings using the current UI resources supplied to the projection. SearchScreen renders the supplied state and paged rows, handles keyboard/focus interaction, and lets Paging item access drive further loading.

The five Search previews cover prompt, loading, error, empty, and results, once each. Results uses a large font. Preview state is explicit and creates no ViewModel, network request, or live advertisement.

## Networking and failures

The app supplies OMDb configuration through Search domain. Retrofit returns an HTTP Response; the repository distinguishes unsuccessful HTTP responses from a successful body that reports `Response=False`.

- Local catalog checks throw InvalidQuery, InvalidPage, or MissingApiKey.
- Every failed OMDb body throws CatalogException.Backend with the original Error text, including movie-not-found. There is no classification by backend message or special empty-page conversion.
- CommonException.Network retains I/O causes; Http retains the status code, including 401; InvalidData represents malformed/missing data; Storage retains SQLite failures.
- BaseUseCase and BaseFlowUseCase return failed Results for operation Exceptions, log unexpected Exceptions, preserve cancellation, and propagate JVM errors. Each thrown exception is a separate occurrence with its own stack trace.

Search shows nonblank backend text or a resource fallback. Local errors use resource messages. A successful empty collection produces the empty-results state.

## Favorites

Favorites implementation owns the Room database, DAO, entity, explicit movie-type mapping, and Hilt bindings. Version 1 stores IMDb ID, title, year text, type, and nullable poster URL. Row presence means favorite membership; observation orders rows by title and IMDb ID. Writes are idempotent upsert/delete operations. No destructive migration or repository mutex is used.

Each feature ViewModel injects ObserveFavoritesUseCase and UpdateFavoriteUseCase directly. Search and Favorites track observed/pending ID sets; Favorites also owns the saved collection and loading state. Details tracks a single favorite flag and saving flag. Unknown membership disables writes; database observation confirms membership changes and clears pending writes. Cancellation clears pending state.

MessageScaffold renders the current UserMessage and handles dismissal and retrying a failed write. Message identities prevent stale callbacks. Read errors use “Failed to retrieve favorites”; write errors use “Failed to add favorite.” Read failures can be dismissed; write Retry repeats the movie and requested membership. Saved poster availability offline depends on the image cache.

## Ads

The SDK has three modules. `sdk` contains contracts, NativeAdPlacement, ViewModel-owned native sessions, and fullscreen/launch orchestration. `sdk:admob` owns initialization, format adapters, callback-thread handoffs, native XML asset views, and resource destruction. `sdk:launch` owns the cold-launch policy and DataStore. Google classes do not cross the adapter boundary. The app supplies format/application IDs and starts shared initialization asynchronously through AndroidX Startup.

Native slots are requested once when SearchViewModel starts, independently of search totals. NativeAdPlacement allows display only when at least three movies are currently loaded, excluding ad rows. The ViewModel checks that rule from the unique movie count and resolves eligible ad anchors in PagingData; the UI performs neither eligibility checks nor row insertion. Loading does not depend on this check. Eligible ads appear after the first and second movies; with both slots filled, their display indexes are 1 and 3. Unfilled slots are omitted, and only reachable slots are inserted. A session retains its ads until its ViewModel closes; rows render ads and release their Android view hosts on disposal. The public owner exposes the adapter's StateFlow directly, without a second collector. Adapter callbacks return to Main before changing slot/UI state. Expired native ads are released and requested again on attachment using the existing one-hour age limit.

Interstitial history uses `datastore/ads_launch_history.preferences_pb`, keys `launch_count` and `was_interstitial_ad_shown`, and existing backup exclusions. The launch count saturates at two. Eligibility starts at the second recorded process cold launch and remains true across later launches until confirmed presentation is persisted. Loading alone never consumes eligibility.

LaunchAdSession starts once per process and prepares without blocking content. At the existing Details transition, a ready resource is offered immediately; a still-loading resource can be offered at a later transition. No fill/failure does not trigger an automatic retry in the same session. An attempted prepared resource is single-use. Fullscreen admission permits only one accepted overlay, validates a resumed host, and checks the existing one-hour maximum age.

After SDK handoff, cancellation stops waiting but does not claim that the overlay was dismissed. The SDK's terminal callback releases admission and the resource; presentation confirmation records history in the application scope. Initialization remains on a shared background dispatcher, while DataStore owns disk work and presentation/session state stays on Main. No extra per-request threads or refresh timers are created.

See [ads SDK review](ads-review.md) for placement rules, resource lifetimes, and API references.
