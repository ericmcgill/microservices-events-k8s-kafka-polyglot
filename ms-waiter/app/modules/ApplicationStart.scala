package modules

import scala.concurrent.Future
import javax.inject._
import play.Logger
import play.api.inject.ApplicationLifecycle
import services.consumers.{KafkaOrderFromCustomerListener, KafkaOrderReadyListener}

// This creates an `ApplicationStart` object once at start-up and registers hook for shut-down.
@Singleton
class ApplicationStart @Inject() (lifecycle: ApplicationLifecycle) {
  // Shut-down hook
  lifecycle.addStopHook { () =>
    Logger.info(">>>>>>>>>> ApplicationStop <<<<<<<<<<<<")
    Future.successful(())
  }
  //...
  Logger.info(">>>>>>>>>> ApplicationStart <<<<<<<<<<<<")
  new KafkaOrderFromCustomerListener().run()
  new KafkaOrderReadyListener().run()
}