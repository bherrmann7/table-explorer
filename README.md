
# Overview

Table Explorer is a small (toy) tool that allows you to visualize a database's schema in a web browser.  The schema is presented using boxes and lines generated by plantuml.

Similar to (and inspired by) https://github.com/achiku/planter, Table Explorer offers more dynamic functionality, making it particularly useful for drilling into large databases with many tables and constraints.    Typically a larger database cannot be displayed in a single diagram because the diagram becomes so large that the table names and the lines showing the relations is too small (everything is too tiny, like a map of NYC on a postcard.) Table explorer allows you to start off small and click your way though each relation.

# Requirements

The postgres jdbc driver is presumed.   To add a different jdbc driver you need to add the dependency to the deps.edn

# How to Use

To run Table Explorer, execute the following command in your terminal:

$ clojure src/web_explorer

Then, open your web browser and navigate to http://localhost:3000

# Demo

If you're interested in seeing Table Explorer in action, check out a short demo video at https://youtu.be/SejOoCdCq3I.

# Future Work

Currently, Table Explorer assumes that there is only one user accessing the web application. However, this could be problematic if you were to share the URL with teammates, as they would all be logged into the same session. 

Allow a table to be collapsed.
