# Advertising SDK

The advertising implementation uses Google Mobile Ads Next-Gen SDK 1.5.0. `sdk` owns contracts, placement rules and resource lifetimes; `sdk:admob` owns Google integration and native asset views; `sdk:launch` owns persisted launch history.

## Native ads

- Request two slots once when SearchViewModel starts, independently of search results.
- Display ads only when the current list contains at least three movies, excluding ad rows.
- Insert the two native slots after the first and second movies. Omit unfilled slots; retain each filled slot at its movie anchor.
- Keep ads for the Search ViewModel lifetime. Rows release their Android view hosts on disposal; the session releases the ads when closed.

SearchViewModel combines cached PagingData with the native session flow and query-local eligibility. It applies NativeAdPlacement using the unique movie count, filters source ad anchors, and exposes paged movie/ad rows without retaining a loaded movie snapshot. UI code renders those rows without deciding eligibility or inserting ads.

## Interstitial

Eligibility starts on the second recorded cold launch and continues across later launches until SDK-confirmed presentation is persisted. The count saturates at two. Preparation runs once per process without blocking content; the Details transition offers an already-prepared ad. An early transition does not cancel loading.

A prepared ad is single-use. No fill or load failure does not automatically retry in the same session. Fullscreen admission prevents overlapping accepted overlays, and presentation requires a resumed Activity. Native attachment and interstitial presentation retain their one-hour age checks; there is no refresh timer.

History uses `datastore/ads_launch_history.preferences_pb`, keys `launch_count` and `was_interstitial_ad_shown`, and the app's backup exclusions. Only confirmed presentation records consumption. History writes retain best-effort failure handling.

## Threading and ownership

Shared initialization runs on a background dispatcher and outlives individual load callers. DataStore owns disk scheduling. SDK callbacks that update session/UI state return to Main.

After fullscreen handoff, cancelling the waiting coroutine does not imply dismissal. SDK terminal callbacks release the resource and admission; presentation confirmation records history in the application scope. Late native ads delivered after session closure are destroyed.

NativeAdView and MediaView host the SDK assets and click/impression handling. The ad is registered after layout. A host's background drawable is reused when theme colors change. The native owner exposes its session flow directly; the ViewModel derives eligible display rows from it.

## References

API behavior follows Google's [initialization](https://developers.google.com/admob/android/next-gen/migration), [callback threading](https://developers.google.com/admob/android/next-gen/migration/handle-callbacks), [native loading](https://developers.google.com/admob/android/next-gen/native), [native rendering](https://developers.google.com/admob/android/next-gen/native/advanced), and [single interstitial loading](https://developers.google.com/admob/android/next-gen/interstitial/single-load) documentation.

Community discussions on [ad integration](https://www.reddit.com/r/admob/comments/1ojk0vn/my_tips_to_improve_your_revenue_from_ads/) and [Compose performance](https://www.reddit.com/r/androiddev/comments/1p2ssq7/are_there_ad_sdks_that_are_compose_first/) provide experience reports, not API contracts or application business rules. No memory, frame-time, fill-rate or revenue measurements are claimed here.
