package dotty.tools.sbtplugin

import org.junit.Assert._
import org.junit.Test
import DottyPlugin.autoImport._

class DottyPluginTest {
  @Test def latestNightlyVersion() = {
    val version = dottyLatestNightlyBuild.get // assert doesn't crash
    assertTrue(version, version.startsWith(dottyLatestMinorVersion))
  }
}
