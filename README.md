Instructions:

The web interface was built using Meteor.JS, a library built around Node.JS.
To use this, you need to install Meteorite on your computer. This is currently
only available on Mac and Linux.

To install meteorite, use the following command on your shell:

>> sudo -H npm install -g meteorite


Following on that, you need to change the following line to reflect your directory structure:

./web_gui/server/optabinding.js: Line 1
var jar_location="/home/.../route-planner-1.0-SNAPSHOT-jar-with-dependencies.jar"

Change "/home/..." to the absolute directory in which the route planner jar is located. 

After that, you can run the code simply by typing:

>> meteor

You'll be able to access the interface through your web-browser, at localhost:3000

