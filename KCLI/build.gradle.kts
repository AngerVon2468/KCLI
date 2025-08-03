import com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING

import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

plugins {
	alias(libs.plugins.multiplatform)
	alias(libs.plugins.maven.publish)
	alias(libs.plugins.buildKonfig)
}

val projGitHubURL: String by project
val mainPkg: String by project
val projPkg: String by project
val projVersion: String by project
val projName: String by project

kotlin {
	// macOS support... never.
	jvm()
	linuxX64()
	mingwX64()

	sourceSets {
		this.forEach {
			it.dependencies {
				implementation(libs.kotlin.reflect)
			}
		}

		commonMain.dependencies { }

		commonTest.dependencies {
			implementation(kotlin("test"))
		}
	}

	targets.withType<KotlinNativeTarget> {
		binaries {
			sharedLib()
			staticLib()
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
		inceptionYear = "2025"
		description = "Kotlin Multiplatform library for CLI parsing."
		url = projGitHubURL

		licenses {
			license {
				name = "The Apache License, Version 2.0"
				url = "https://www.apache.org/licenses/LICENSE-2.0.txt"
				distribution = "https://www.apache.org/licenses/LICENSE-2.0.txt"
			}
		}

		developers {
			developer {
				id = "EpicVon2468"
				name = "EpicVon2468 (Mavity The Madity)"
				url = projGitHubURL.substringBeforeLast("/")
			}
		}

		scm {
			url = projGitHubURL
			connection = "scm:git:git://github.com/EpicVon2468/KCLI.git"
			developerConnection = "scm:git:ssh://git@github.com/EpicVon2468/KCLI.git"
		}
	}

	if (project.hasProperty("signing.keyId")) signAllPublications()
}

buildkonfig {
	// BuildKonfig configuration here.
	// https://github.com/yshrsmz/BuildKonfig#gradle-configuration
	packageName = projPkg
	exposeObjectWithName = "Constants"
	defaultConfigs {
		buildConfigField(STRING, "VERSION", projVersion)
	}
}