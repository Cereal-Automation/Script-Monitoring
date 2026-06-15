plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}
rootProject.name = "Script Monitoring"
include("command")
include("command-monitoring")
include("script-common")
include("script-sample")
include("script-nike")
include("script-snkrs")
include("script-bdga-store")
include("script-tgtg")
include("script-zalando")
include("script-rss")
include("script-rental")
include("stockx-api-client")
