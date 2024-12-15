plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.5.0"
}
rootProject.name = "Script Monitoring"
include("monitoring")
include("script-shared")
include("script-sample")
include("script-nike")
include("script-snkrs")
