HOW TO BUILD (GitHub, no installs)
1. Create a free account at github.com and click New repository (name: confis-client, Public or Private).
2. On the new repo page click "uploading an existing file", drag in EVERYTHING from this folder
   (including the hidden .github folder) and click Commit changes.
   Tip: easiest is to drag the folders themselves: .github, intermediary-src, src, tools plus the
   loose files build.gradle, gradle.properties, settings.gradle.
3. Click the Actions tab -> "Build mod" -> wait ~3-6 minutes for a green check.
4. Open the finished run -> scroll to Artifacts -> download confis-mod-jar -> unzip -> put the .jar
   in .minecraft/mods (Fabric Loader + Fabric API required).
If the run is red, open it, copy the red error text, and send it to Claude.
