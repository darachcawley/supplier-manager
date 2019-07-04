# supplier-manager

== Running the quickstart standalone on your machine

To run this quickstart as a standalone project on your local machine:

. Download the project and extract the archive on your local filesystem.
. Build the project:
+
[source,bash,options="nowrap",subs="attributes+"]
----
$ cd PROJECT_DIR
$ mvn clean package
----
. Run the service:

+
[source,bash,options="nowrap",subs="attributes+"]
----
$ mvn spring-boot:run
----
+
Alternatively, you can run the application locally using the executable JAR produced:
+
----
$ java -jar -Dspring.profiles.active=dev target/supplier-manager-1.0-SNAPSHOT.jar
----
+
This uses an embedded in-memory HSQLDB database. You can use the default Spring Boot profile in case you have a MySQL server available for you to test.

. You can then access the REST API directly from your Web browser, e.g.:

- <http://localhost:8080/camel-rest-sql/books>
- <http://localhost:8080/camel-rest-sql/books/order/1>