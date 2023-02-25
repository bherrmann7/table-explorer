
# Intro

A utility for viewing a database's schema.

The schema is displayed in a web browser graphically.   plantuml is used to generate the digram.

The diagram of the schema is similar to output from https://github.com/achiku/planter, but is more dynamic.

# Execution

To run,

$ clojure src/web_explorer

To view, browse to http://localhost:3000

# Video

A short demo of Table explorer is at,  https://youtu.be/SejOoCdCq3I

# TODO

This web application assumes there is a single user, which would be a
problem if you shared the url with teammates.. as they would drop into
your sesstion (aka single global atom holds state)



