package com.eric.mods

import services.consumers.{KafkaOrderFromCustomerListener, KafkaOrderReadyListener}

import scala.concurrent.Future
import javax.inject.{Inject, Singleton}
import services.producers.KafkaProducer
import play.Logger
import play.api.inject.ApplicationLifecycle
import services.producers.KafkaProducer

// This creates an `ApplicationStart` object once at start-up and registers hook for shut-down.
@Singleton
class OnApplicationStart @Inject() (lifecycle: ApplicationLifecycle) {
    Logger.info("We're in OnApplicationStart")
    // Shut-down hook
    lifecycle.addStopHook { () =>
        Logger.info("Shutting this _down_")
        Future.successful(())
    }
    val orderFromCustomerListener = new KafkaOrderFromCustomerListener().run()
    val orderReadyListener = new KafkaOrderReadyListener().run()

}