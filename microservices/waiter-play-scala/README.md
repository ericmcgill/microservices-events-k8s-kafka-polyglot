# Waiter microservice

## Part of a microservices best practices demo

### Purpose of the microservice

The _waiter_ subscribes to two message topics/channels:

- _orders-from-customers_ which contains a list of items that a customer is ordering.
- _order-ready_ which is an order that is ready to be delivered to a particular customer.

The _waiter_ should make no assumptions about the messaging layer (though it will need to support Kafka out of the box, but should be extensible).

This means we should abstract the pub/sub send/listen methods and we should configure what to listen to and what class to deserialize the message to. Also, configure which handler for a given message type.

Also, this architecture will be very common, so registering the messages and tying them to classes and handlers should all be abstracted.

To start the listeners, we will need to spin them up onApplicationStart and pass them the handlers, I guess?
