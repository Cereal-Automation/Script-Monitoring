plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.5.0"
}
rootProject.name = "Script Monitoring"
include("command")
include("command-monitoring")
include("script-common")
include("script-sample")
include("script-nike")
include("script-snkrs")
include("script-bdga-store")
include("script-zalando")
include("stockx-api-client")
