# ObjectPortal

ObjectPortal is a somewhat simple library aiming to provide a set of functionality useful for automatically loading 
dependencies or simply retrieving objects based on some contextual characteristic. It aspires to support JSR 330 
interactions, but in the meantime it uses the same annotations to be either familiar or super-annoying (take your pick).

# Features

ObjectPortal aims to be a flexible and easy-to-use Dependency Injection solution with a focus on minimal configuration
and simple syntax. It tries to behave in many of the ways that you'd expect a dependency injector to work, including 
sane handling of things like `@Inject` annotations and object creation. Along the way, it provides a few things that 
are more novel and possibly useful:

* Support for the definition of injected objects with `Supplier` instances, allowing for injected objects to be defined
by non-default constructors or factory methods, deferring creation until after configuration or until necessary.
* Support for contextual dependencies. Objects can assigned a context and retrieved according to a requested context.
Contexts can be controlled directly, or implicitly by thread membership.
* On-Demand dependency injection from within the target object. This allows for dependency injection to occur even in
objects that weren't created by the injector. (Okay... there are other solutions that do this just as well)
* Methods for direct object storage and retrieval. This allows for more customized behaviors like fully-manual 
dependency fetching or simple object storage.
* Multiple layers of suitably exposed abstraction to aid in testing. No static methods with library logic in them. No
singleton classes with load-time configuration. No immutable interface instances to make error testing difficult.

# Structure

* **Global/Static Facade**: The `ObjectPortal` facade provides a library of static methods that are globally available
for use without needing syntactic gymnastics or the need to provide your own facade. It is a very light facade, 
delegating almost every method to underlying objects and configuration.
* **Factory/Provider Layer**: The `PortalProvider` interface defines the implementation layer responsible for performing
the actions exposed by the `ObjectPortal` facade. This can be changed and configured at runtime, avoiding the need for
configuration at class-load time. This also makes testing far easier, as the `PortalProvider` can be changed at will or
even replaced with a mock object without needless complications.
* **Extensible Context Tokens**: Contexts managed by the provider layer are linked to easily extensible token objects.
* **Object Storage/Injection**: The `ObjectPortalContext` implementations contain the actual code responsible for most
of the object manipulation code:
    * Creating object instances
    * Dependency injection
    * Object storage and retrieval

# Development Plan

### Milestone 1 (v0.5)

* **ThreadLocal-linked PortalProvider**: A PortalProvider
* Singleton PortalProvider
* Basic implementations of tokens

### Milestone 2 (v0.6)

* Registering objects via `Supplier` instances.
* Linking to parent/default contexts

### Milestone 3 (v0.7)

* Manually expiring contexts
* Auto-expiring thread-linked contexts

### Milestone 4 (v0.8)

* Support for constructors that use parameters available in the current context

### Milestone 5 (v0.9)

* Support for optional injection (skipping injection if no object is available)
* Support for refusing injection on targets that are already assigned
