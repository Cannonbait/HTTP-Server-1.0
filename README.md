# HTTP-Server/1.0
To run the server simply run the run.sh script which first kills any running server, recompiles the entire program then runs the server.
Alternatively you can simply run the WebServer class using java.

To test the WebServer there is a supplied WebServerTest that needs to be compiled and then run with java. Upon running there needs to be supplied the port that the server is running on. This results in roughly:
java WebServerTest -p <<port>>
