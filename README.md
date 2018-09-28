# microservices-events-k8s-kafka-polyglot 

Demo of event driven microservices using Kafka, Kubernetes and various languages (Elixir, Java, Scala)

Based on children's book: Pancakes! Pancakes! https://www.amazon.com/Pancakes-Eric-Carle/dp/0887082750

I have taken a messages-before-technical approach to system design (specifically: Business requirements -> Message language -> Technical specification)

# Business Requirements
The system is designed to mimic a children's book (https://www.amazon.com/Pancakes-Eric-Carle/dp/0887082750) roughly described as:
## Kid wants pancakes. Mom throws harsh reality in his face:
- Go get eggs from the hen!
- Go milk cow for butter!
- Make the butter!
- Go harvest wheat for flour!
- Go mill the wheat into flour!
- Go extract maple syrup!
- Mix it!
- Cook it!
- Receive the goods.

### The system can receive orders for pancakes.
- When the order is received, workers can go out in four directions (retrieve eggs, milk the cow, harvest the wheat, extract the syrup)
- When cow is milked, butter can be churned
- When wheat is harvested, flour can be milled
- When we have eggs, butter and wheat, we can mix it.
- When we have the mix, we can cook the pancakes.
- When the pancakes have been cooked, we can serve them (back to user).
- When the pancakes have been served, we can clean up.

# Messages

#### When the order is received, workers can go out in four directions (retrieve eggs, milk the cow, harvest the wheat, extract the syrup)

From RESTful API, a message will be published.
```
OUT:
{
    role: 'kid',
    cmd: 'order',
    data: {order: ['pancackes']}
}
```

#### When a 'kid' has published a request, a waiter will be subscribed to these events. The waiter will take the order and pass it off to the team responsible for making it. (Waiter will have no awareness of what is requeired to make the order. Waiter _only_ knows how to take the order and serve it.)

From the waiter, a message will be published.
```
# IN:
{
    role: 'kid',
    cmd: 'i-want-pancakes'
}

{
    role: 'cook',
    cmd: 'order-up'
    data: {order: ['pancakes', 'syrup', 'butter']}
}

{
    role: 'expediter',
    cmd: 'order-impossible'
    data: {message: 'We couldn\'t procure any flour'}
}

# OUT:
{ 
    role: 'waiter',
    cmd: 'order-in',
    data: {order: ['pancakes']}
}

{
    role: 'waiter',
    cmd: 'serving',
    data: {order: ['pancakes', 'syrup', 'butter', 'bill'] || ['water', 'contempt']}
}

```

The waiter then also listens for responses to his request to the kitchen workers (see published messages from workers)

#### When an order comes in from the expediter for pancakes or toast, a cow milker runs off to milk a cow.

#### When an order comes in from the expediter for pancakes or toast, a wheat harvester runs off to harvest wheat.

#### When an order comes in from the expediter for pancakes, a syrup extractor runs off to extract syrup.

#### When an order comes in from the expediter for pancakes or an omelette, an egg retriever runs off to retrieve eggs.

They each are listening for messages that will match this pattern (published by the waiter):

```
# IN:
# The guid let's the expediter know that items returning are for a specific order.
{ 
    role: 'waiter',
    cmd: 'order-in',
    id: '<guid>',
    data: {order: ['pancakes']}
}

{
    role: 'egg-retriever',
    cmd: 'eggs-retrieved',
    id: '<guid>'
}
{
    role: 'cow-milker',
    cmd: 'cow-milked',
    id: '<guid>'
}
{
    role: 'syrup-extractor',
    cmd: 'syrup-extracted',
    id: '<guid>'
}
{
    role: 'miller',
    cmd: 'wheat-milled',
    id: '<guid>'
}
{
    role: 'mixer',
    cmd: 'batter-mixed',
    id: '<guid>',
    data: 'batter'
}
{
    role: 'cook',
    cmd: 'order-cooked',
    id: '<guid>',
    data: {order: 'pancakes'}
}

# OUT:
{
    role: 'expediter',
    cmd: 'cook-order',
    id: '<guid>',
    data: {ingredients: ['batter'], order: ['pancakes']}
}

{
    role: 'expediter',
    cmd: 'mix-batter',
    id: '<guid>',
    data: {ingredients: ['eggs', 'milk', 'flour']}
}

{
    role: 'expediter',
    cmd: 'order-up',
    data: {order: ['pancakes', 'syrup', 'butter']}
}

{
    role: 'expediter',
    cmd: 'order-impossible',
    data: {message: 'We couldn\'t procure any flour'}
}

```
#### When wheat has been harvested _and_ milled, someone needs to collect it along with the milk from the cow and the eggs from the hen into a mix. Messages might look like:
```
{
    role: 'cow-milker',
    cmd: 'milk-cow',
    id: '<guid>'
}
{
    role: 'cow-milker',
    cmd: 'cow-milked',
    id: '<guid>'
}

{
    role: 'egg-retriever',
    cmd: 'retrieve-eggs',
    id: '<guid>'
}
{
    role: 'egg-retriever',
    cmd: 'eggs-retrieved',
    id: '<guid>'
}

{
    role: 'syrup-extractor',
    cmd: 'extract-syrup',
    id: '<guid>'
}
{
    role: 'syrup-extractor',
    cmd: 'syrup-extracted',
    id: '<guid>'
}

{
    role: 'wheat-harvester',
    cmd: 'harvest-wheat',
    id: '<guid>'
}
{
    role: 'wheat-harvester',
    cmd: 'wheat-harvested',
    id: '<guid>'
}
```

#### [Happy Path] When eggs, milled wheat and milk have been received, someone will need to mix it.

```
# IN:
{
    role: 'expediter',
    cmd: 'mix-batter',
    id: '<guid>',
    data: {ingredients: ['eggs', 'milk', 'flour']}
}

# OUT:
{
    role: 'mixer',
    id: '<guid>',
    cmd: 'batter-mixed',
    data: 'batter'
}
```

#### [Unhappy Path] When eggs _OR_ milled wheat _OR_ milk could _NOT_ be procured, the party is over and the poor kid will receive a glass of water and contempt from the waiter. (Who published the operation-failed message?)

#### When the batter has been mixed, somone will need to cook it.

```
# IN:
{
    role: 'expediter',
    cmd: 'cook-order',
    id: '<guid>',
    data: {ingredients: ['batter'], order: ['pancakes']}
}

# OUT:
{
    role: 'cook',
    cmd: 'order-up',
    id: '<guid>',
    data: {order: 'pancakes'}
}
```

# Roles

#### kid
#### waiter
#### expediter
#### cook
#### egg-retriever
#### cow-milker
#### wheat-harvester
#### syrup-extractor
#### mixer
#### miller