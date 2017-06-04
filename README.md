# Event sourcing demo

Incomplete implementation of a shop. Just me tinkering around with Event
Sourcing.

Some info about the implementation:
* There's _no_ UI, just tests
* No library used
* Events in memory or persisted to SQL-Database (JPA, Hibernate, H2)
* Not necessarily the "correct way" of doing things
* Business-logic in services:
    - "Disallowed events" are persisted as well
    - These events are reverted immediately
* Snapshots are prepared, but not actually generated for now
* Events are somewhat separate from actual payload data

## Open questions / issues

* I'm unsure on how "Foreign Key" bindings could be done prettier
* There's a lot of copying where properties have to be listed. So quite
    some boilerplate
* A bit of decluttering is required - I was a bit lazy
* I couldn't get away without *instanceof* or casts
* The "DI" is a bit... nah... rudimentary
* Performance could be improved: There are situations, where the complete
    log is fetched
* A way of inconsistencies (due to eventual consistency) is required
    - For example a change to a user, that has been deleted

## About the db-persistence

There's one table per event-payload-type, i.e. all user-events goto one
table. This yields a lot of null-values. A better approach would be a
DB that stores documents, rather than a table-based one.

Events are distinguished using a @DiscriminatorColumn, so the proper
classes are read from the DB.

## Structure

There are two main packages:

* *scaffolding* contains generic stuff, that may be part of a library
* *sample* contains the implementation of the store

Where events are routed is determined by some wiring in the
*ApplicationConfiguration*: Events may either be sent directly to a store
or to the *ListenableEventStore* the later one will ask the wiring and
dispatch them accordingly.

## License

BSD three clause. Any feedback is welcome.

Have fun :)