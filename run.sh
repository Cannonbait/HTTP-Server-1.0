rm HTTP/*.class
pkill 'java Webserver'
javac HTTP/WebServer.java
javac HTTP/WebServerTest.java
java HTTP.WebServer &
