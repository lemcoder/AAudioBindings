# AAudio Bindings
AAudio low-level API wrapper for Android

## Get started

Add this to your TOML file
```
[versions]
aaudio = <latest-version>

[dependencies]
aaudio = { group = "io.github.lemcoder", name = "aaudio-bindings", version.ref = "aaudio" }
```

Also add jitpack to your repositories in `settings.gradle.kts`
```
dependencyResolutionManagement {
    repositories {
        // ...
         maven { url = uri("https://jitpack.io") }
    }
}
```