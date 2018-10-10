package modules

import com.google.inject.AbstractModule
import play.api.Logger

class ApplicationStartModule extends AbstractModule {
  Logger.info(""">> StartModule called...""")
  override def configure() = {
    bind(classOf[ApplicationStart]).asEagerSingleton()
  }
}