import androidx.build.LibraryGroups
import androidx.build.LibraryVersions

plugins {
    id("SupportAndroidLibraryPlugin")
}

dependencies {
    api(project(":annotation"))
}

android {
    sourceSets.getByName("main").res.srcDir("res")
}

supportLibrary {
    name = "Android Support CardView v7"
    publish = true
    mavenVersion = LibraryVersions.SUPPORT_LIBRARY
    mavenGroup = LibraryGroups.CARDVIEW
    inceptionYear = "2011"
    description = "Android Support CardView v7"
    failOnDeprecationWarnings = false
}
