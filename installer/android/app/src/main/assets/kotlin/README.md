# Current version: Kotlin 1.8.0
## Updating
Use android d8 to convert the stdlib to a dex file:
`d8 kotlin-stdlib-1.8.0.jar --release --min-api 24 --output kotlin-1.8.0.zip`
Then extract the dex file from the zip and copy it to here.
