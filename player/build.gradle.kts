import org.gradle.internal.os.OperatingSystem

plugins {
    application
    id("com.github.johnrengelman.shadow") version "5.2.0"
}

val game: String by project
val year: String by project
val gameName: String by project
val deployDir: File by project
val testLogDir: File by project
val version = rootProject.version.toString()

sourceSets.main {
    java.srcDir("src")
    resources.srcDir("src")
}

application {
    mainClassName = "sc.player2021.Starter"
}

dependencies {
    implementation(project(":plugin"))
    implementation(kotlin("script-runtime"))
}

tasks {
    shadowJar {
        archiveFileName.set("defaultplayer.jar")
    }
    
    val prepareZip by creating(Copy::class) {
        into(buildDir.resolve("zip"))
        with(copySpec {
            from("buildscripts")
            filter {
                it.replace("VERSION", version).replace("GAME", game).replace("YEAR", year)
            }
        }, copySpec {
            from(rootDir.resolve("gradlew"), rootDir.resolve("gradlew.bat"))
            filter { it.replace(Regex("gradle([/\\\\])wrapper"), "lib$1gradle-wrapper") }
        }, copySpec {
            from(rootDir.resolve("gradle").resolve("wrapper"))
            into("lib/gradle-wrapper")
        }, copySpec {
            from("src")
            into("src")
        }, copySpec {
            from(configurations.default, arrayOf("sdk", "plugin").map { project(":$it").tasks.getByName("sourcesJar").outputs.files })
            into("lib")
        })
        if(!project.hasProperty("nodoc")) {
            dependsOn(":sdk:doc", ":plugin:doc")
            with(copySpec {
                from(project(":plugin").buildDir.resolve("doc"))
                into("doc/plugin-$gameName")
            }, copySpec {
                from(project(":sdk").buildDir.resolve("doc"))
                into("doc/sdk")
            })
        }
    }
    
    val deploy by creating(Zip::class) {
        dependsOn(shadowJar.get(), prepareZip)
        destinationDirectory.set(deployDir)
        archiveFileName.set("simpleclient-$gameName-src.zip")
        from(prepareZip.destinationDir)
        doFirst {
            copy {
                from("build/libs")
                into(deployDir)
                rename("defaultplayer.jar", project.property("deployedPlayer") as String)
            }
        }
    }
    
    run.configure {
        args = System.getProperty("args", "").split(" ")
    }
    
    val playerTest by creating {
        dependsOn(prepareZip)
        val execDir = File(System.getProperty("java.io.tmpdir")).resolve("socha-player")
        doFirst {
            execDir.deleteRecursively()
            execDir.mkdirs()
            
            copy {
                from(prepareZip.destinationDir)
                into(execDir)
            }
            val command = arrayListOf(if(OperatingSystem.current() == OperatingSystem.WINDOWS) "./gradlew.bat" else "./gradlew", "shadowJar", "--quiet", "--offline")
            testLogDir.mkdirs()
            val process = ProcessBuilder(command).directory(execDir)
                    .redirectOutput(testLogDir.resolve("player-shadowJar-build.log"))
                    .redirectError(testLogDir.resolve("player-shadowJar-err.log"))
                    .start()
            val timeout = 5L
            if(process.waitFor(timeout, TimeUnit.MINUTES)) {
                val result = process.exitValue()
                if(result != 0 || !execDir.resolve("${game}_client.jar").exists())
                    throw Exception("Player was not generated by shipped gradlew script!")
            } else {
                throw Exception("Gradlew shadowJar for player did not finish within $timeout minutes!")
            }
            println("Successfully generated client jar from shipped source")
        }
    }
    
}
