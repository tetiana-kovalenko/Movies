# Movies

An Android application built with Kotlin and Jetpack Compose. Search finds movies by title with paged OMDb results. Favorites saves movie metadata with Room and synchronizes membership across screens. Details displays the selected movie snapshot without another request. Advertising uses native list placements and one persisted interstitial eligibility rule.

## Setup

Use Android Studio compatible with AGP 9.3.1 and Android SDK 37. The minimum Android version is 11 (API 30). Gradle 9.6.0 uses a Java 25 daemon and Java 17 compilation toolchains. Dependency and JDK provisioning need internet access on an initial build.

Add `omdb.api.key=YOUR_KEY` to ignored root `local.properties`, then rebuild. No API key is required to compile. Queries are trimmed, require at least three characters, and wait 300 ms before requesting the first page. Paging loads later pages as item access approaches the end of the feed.

[ads.defaults.properties](ads.defaults.properties) supplies public Google sample IDs. Override `admob.application.id`, `admob.native.ad.unit.id`, and `admob.interstitial.ad.unit.id` in ignored `local.properties` when needed, then rebuild. Keep real identifiers and machine-specific settings outside committed source.

```sh
./gradlew :app:assembleDebug
./gradlew :checkQuality
./gradlew :verifyProject
```

`checkQuality` runs production Android lint. `verifyProject` adds debug and unsigned release builds. Append `--offline` when the required dependencies and toolchains are cached.

## Structure

| Module | Responsibility |
| --- | --- |
| `app` | Startup, configuration, Navigation 3 back stack and destination composition |
| `feature:{search,favorites,details}:domain` | Public feature contracts/models and navigation keys |
| `feature:search:impl` | Search state/UI, Paging, OMDb API/DTOs, repository and use case |
| `feature:favorites:impl` | Favorites state/UI, Room storage, repository and use cases |
| `feature:details:impl` | Details state/UI and assisted ViewModel creation |
| `core:domain` | Plain Kotlin movie models, common exceptions and base use cases |
| `core:ui` | Theme, movie cards/posters, favorite controls and message/action layout |
| `sdk` | Ad contracts, display eligibility and session ownership |
| `sdk:admob` | Google Mobile Ads Next-Gen SDK integration and native asset rendering |
| `sdk:launch` | Cold-launch rule, use cases and persisted advertising history |
| `feature` | Aggregate build project for feature implementations |
| `build-logic` | Shared Android, Compose, Hilt and serialization conventions |

Navigation uses one saved back stack with entry-scoped ViewModels. Favorites is popped when Search is selected. Details receives a serializable movie snapshot through navigation and supplies the restored movie to its ViewModel through assisted injection.

SearchViewModel owns status selection, error-message mapping, and mixed movie/ad row preparation using SDK display eligibility. The screen renders strings supplied by the ViewModel using the current UI resources or backend text. A failed OMDb body uses one backend error type; local validation and configuration failures remain separate. Favorites failures appear through MessageScaffold, with a retry action for failed writes.

Native ads are requested once when SearchViewModel starts, retained for its lifetime, and shown only when the current list contains at least three movies. The two slots are anchored after the first and second movies. Unfilled slots are omitted; loading is independent of the display threshold. The interstitial becomes eligible on the second recorded cold launch and stays eligible across launches until presentation is confirmed and persisted. The existing Details transition offers a ready ad without waiting for loading.

Search has one full-screen preview for each main state; its results preview uses a large font. Read [architecture](docs/architecture.md), [verification](docs/verification.md), [advertising SDK](docs/ads-review.md), and [project guidance](AGENTS.md) for details.
