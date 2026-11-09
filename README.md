# DSub2000-more

Connect to your Subsonic server and listen to your music wherever you go. Songs
are cached for playback to save on your mobile bandwidth and to make them
available when you have no connection at all.

DSub2000-more is a community fork of [DSub2000](https://github.com/paroj/DSub2000).
It retains the upstream settings back-navigation behavior and adds:

* A configurable Now Playing controls auto-hide delay, including an option to disable it
* Wrapping for long Now Playing track titles and text on generated placeholder cover art
* Coalescing of pending remote playback-state updates so the latest state is sent
* A music-library folder list with an all-folders option, plus metadata-based album detail pages
* Separate song and album menu visibility settings, and a responsive artwork-focused player
* Themed list separators, shadow-free toolbars, and single-line truncated list names

Subsonic is a cross-platform FOSS media server that's capable of indexing very
large media collections. The server can transcode if necessary so that the app
can play files that your device may not normally support.

DSub2000-more is not currently published on F-Droid.

## Installation

This fork uses the application ID `github.trigsoft.dsub2000more`, so it can be
installed alongside the original DSub2000 without replacing it. Settings and
server accounts are separate; configure your server again after installation.

## Screenshots

These screenshots are inherited from upstream DSub2000 and do not show all
changes in this fork.

| Albums | Sidebar | Current Song |
| ------ | ------- | ------------ |
| ![](fastlane/metadata/android/en-US/images/phoneScreenshots/1.jpg) | ![](fastlane/metadata/android/en-US/images/phoneScreenshots/2.png) | ![](fastlane/metadata/android/en-US/images/phoneScreenshots/3.jpg) |

| Playlists | Now Playing | Details |
| --------- | ----------- | ------- |
| ![](fastlane/metadata/android/en-US/images/phoneScreenshots/4.jpg) | ![](fastlane/metadata/android/en-US/images/phoneScreenshots/5.png) | ![](fastlane/metadata/android/en-US/images/phoneScreenshots/6.png) |
| You can make playlists available offline by selecting "cached" here. | This shows the play queue. You drag and drop songs to rearrange. Note that you can press the "cached" icon for a context menu. | You can check whether the file was transcoded here. |

## Features

* Drag and drop songs to rearrange your playlist on the Now Playing tab
* Pause playback when other apps request audio focus (navigation, etc)
* Lockscreen controls
* Gapless Playback
* Highly customizable UI
* User defined Cache Size, Network Timeout, and Buffer Length

## Permissions

This is why we need the permissions we do:

* Foreground Service - Keep playback and playlist downloads running in the background
* Notifications - Show playback controls and download status
* Read Media Audio - Read audio files from shared storage on Android 13 and later
* Read & Write External Storage - Access shared storage on Android 9 and earlier
* Location - Identify the current WiFi network for local server URL selection

## License and Credits

See [LICENSE.txt](LICENSE.txt) for the GNU General Public License, version 3.
This fork builds on [DSub2000](https://github.com/paroj/DSub2000) and the original
DSub project. Credit for the upstream app belongs to its authors and contributors.

## Updating Icons
Media Icons are double standard size.  On https://romannurik.github.io/AndroidAssetStudio/icons-actionbar.html you can manually change this via the following js commands:
```
PARAM_RESOURCES.iconSize = {w: 64, h: 64}
PARAM_RESOURCES['targetRect-clipart'] = { x: 12, y: 12, w: 40, h: 40 }
form.fields_[0].params_.maxFinalSize = {w: 512, h: 512}
```
