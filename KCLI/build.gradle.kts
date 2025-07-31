import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

plugins {
	alias(libs.plugins.multiplatform)
	alias(libs.plugins.maven.publish)
	alias(libs.plugins.buildKonfig)
}

val projGitHubURL: String = System.getProperty("projGitHubURL")
val mainPkg: String = System.getProperty("mainPkg")
val projPkg: String = System.getProperty("projPkg")
val projVersion: String = System.getProperty("projVersion")
val projName: String = System.getProperty("projName")

kotlin {
	// macOS support... never.
	jvm()
	linuxX64()
	mingwX64()

	sourceSets {
		commonMain.dependencies { }

		commonTest.dependencies {
			implementation(kotlin("test"))
		}
	}

	targets.withType<KotlinNativeTarget> {
		binaries {
			sharedLib()
		}
		// https://kotlinlang.org/docs/native-objc-interop.html#export-of-kdoc-comments-to-generated-objective-c-headers
		compilations["main"].compileTaskProvider.configure {
			compilerOptions {
				freeCompilerArgs.add("-Xexport-kdoc")
			}
		}
	}
}

// TODO: Maven Central setup is annoying
// Publishing your Kotlin Multiplatform library to Maven Central
// https://www.jetbrains.com/help/kotlin-multiplatform-dev/multiplatform-publish-libraries.html
mavenPublishing {
	publishToMavenCentral()
	coordinates(mainPkg, projName, projVersion)

	pom {
		name = projName
		description = "Kotlin Multiplatform library for CLI parsing."
		url = projGitHubURL

		licenses {
			license {
				name = "APACHE-2.0"
				url = "https://opensource.org/license/apache-2-0"
			}
		}

		developers {
			developer {
				id = "" // TODO
				name = "" // TODO
			}
		}

		scm {
			url = projGitHubURL
		}
	}

	if (project.hasProperty("signing.keyId")) signAllPublications()
}

buildkonfig {
	// BuildKonfig configuration here.
	// https://github.com/yshrsmz/BuildKonfig#gradle-configuration
	packageName = projPkg
	defaultConfigs {
	}
}