import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.jdbc.JDBCClient;
import io.vertx.ext.sql.SQLConnection;

import java.util.Random;

public class MyFirstVerticle extends AbstractVerticle {

    @Override
    public void start(Future<Void> fut) {
        JDBCClient client = JDBCClient.createShared(vertx, new JsonObject()
                .put("url", "jdbc:hsqldb:mem:test?shutdown=true")
                .put("driver_class", "org.hsqldb.jdbcDriver")
                .put("max_pool_size", 30));


        client.getConnection(conn -> {
            if (conn.failed()) {throw new RuntimeException(conn.cause());}
            final SQLConnection connection = conn.result();

            // create a table
            connection.execute("create table test(id int primary key, name varchar(255))", create -> {
                if (create.failed()) {throw new RuntimeException(create.cause());}
            });
        });

        vertx
            .createHttpServer()
            .requestHandler(r -> {
                int requestId = new Random().nextInt();
                System.out.println("Request " + requestId + " received");
                    client.getConnection(conn -> {
                         if (conn.failed()) {throw new RuntimeException(conn.cause());}

                         final SQLConnection connection = conn.result();

                         connection.execute("insert into test values ('" + requestId + "', 'World')", insert -> {
                             // query some data with arguments
                             connection
                                 .queryWithParams("select * from test where id = ?", new JsonArray().add(requestId), rs -> {
                                     connection.close(done -> {if (done.failed()) {throw new RuntimeException(done.cause());}});
                                     System.out.println("Result " + requestId + " returned");
                                     r.response().end("Hello");
                                 });
                         });
                     });
            })
            .listen(8080, result -> {
                if (result.succeeded()) {
                    fut.complete();
                } else {
                    fut.fail(result.cause());
                }
            });
    }
}