plugins {
    id("org.openapi.generator")
    kotlin("jvm")
}

openApiGenerate {
    generatorName.set("kotlin")
    inputSpec.set("specs/stockx.json")
    outputDir.set("${layout.buildDirectory.get()}/generated")
    apiPackage.set("com.cereal.stockx.api")
    invokerPackage.set("com.cereal.stockx.api.invoker")
    modelPackage.set("com.cereal.stockx.api.model")
    configOptions.put("dateLibrary", "java8")
}

sourceSets["main"].java.srcDir("${layout.buildDirectory.get()}/generated/src/main/kotlin")

tasks.named("compileKotlin").configure {
    dependsOn("openApiGenerate")
}
