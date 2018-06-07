

CRUD using Scala, Akka-Http and a Couchbase 5 database.  

Notes:
This CRUD was designed as the backend for a to do list app.  Therefore the update feature has an additional enable/disable feature (so users can mark items as done/to-do without actually deleting them from the database).  

I haven't added a dockerised version of the database yet, so in order to get this project working at the moment, you simply have to create a couchbase bucket called planner.  

