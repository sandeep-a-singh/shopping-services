package com.shopping.server;

import com.shopping.service.OrderServiceImpl;
import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class OrderServer {

        private static final Logger logger = Logger.getLogger(OrderServer.class.getName());

        private Server server;

        public void startServer() {
        int port = 50052;
        try {
            server = ServerBuilder.forPort(port)
                    .addService(new OrderServiceImpl())
                    .build()
                    .start();
            logger.info("Server started on port " + port);
            Runtime.getRuntime().addShutdownHook(new Thread() {
                @Override
                public void run() {
                    logger.log(Level.ALL, "Server Shutdown in case of JVM Shutdown");

                    OrderServer.this.stopServer();
                }
            });
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Server Startup Issue", e);
        }
    }

        public void stopServer() {
        if(server != null) {
            try {
                server.shutdown().awaitTermination(30, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                logger.log(Level.SEVERE, "Unable to Stop Server", e);

            }
        }
    }

        public void blockUntilShutdown() {
        if(server != null) {
            try {
                server.awaitTermination();
            } catch (InterruptedException e) {
                logger.log(Level.SEVERE, "Unable to Stop Server", e);

            }
        }
    }

        public static void main(String[] args) {
        OrderServer orderServer = new OrderServer();
        orderServer.startServer();
        orderServer.blockUntilShutdown();
    }
}
