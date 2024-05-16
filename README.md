Reproduction repo for a bug in the Apollo-GraphQL Java/Kotlin library: https://github.com/apollographql/apollo-kotlin/issues/5901

To reproduce the bug, simply run `./gradlew test` and observe that `TestMain.testNullEnumHangs` times out after the 
configured 30 second timeout.
