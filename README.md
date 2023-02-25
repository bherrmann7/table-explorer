

A utility for viewing a database of tables using plantuml.

Similar to https://github.com/achiku/planter

but this project allows the user to use a web browser to add in tables on demand.



To run,

$ clojure src/web_explorer

To view, browse to http://localhost:3000



TODO:

This web application assumes there is a single user, which would be a
problem if you shared the url with teammates.. as they would drop into
your sesstion (aka single global atom holds state)



