
import com.eric.mods.OnApplicationStart
import javax.inject._
import com.google.inject.AbstractModule
import play.Logger
import play.api.{Configuration, Environment}
import net.codingwell.scalaguice.ScalaModule

class Module(environment: Environment, configuration: Configuration)
  extends AbstractModule
    with ScalaModule {
  Logger.info("We're in Module")
  override def configure() = {
    bind(classOf[OnApplicationStart]).asEagerSingleton
  }
}