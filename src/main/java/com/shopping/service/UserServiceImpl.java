package com.shopping.service;

import com.shopping.client.OrderClient;
import com.shopping.db.User;
import com.shopping.db.UserDao;
import com.shopping.server.OrderServer;
import com.shopping.stubs.order.Order;
import com.shopping.stubs.user.Gender;
import com.shopping.stubs.user.UserRequest;
import com.shopping.stubs.user.UserResponse;
import com.shopping.stubs.user.UserServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UserServiceImpl extends UserServiceGrpc.UserServiceImplBase {
    private UserDao userDao = new UserDao();
    private static final Logger logger = Logger.getLogger(UserServiceImpl.class.getName());

    @Override
    public void getUserDetails(UserRequest request, StreamObserver<UserResponse> responseObserver) {
        User user = userDao.getUserDetails(request.getUsername());


        List<Order> orders = getOrders(user);

        UserResponse.Builder builder = UserResponse.newBuilder()
                .setId(user.getId())
                .setAge(user.getAge())
                .setUsername(user.getUsername())
                .setName(user.getName())
                .setNoOfOrders(orders.size())
                .setGender(Gender.valueOf(user.getGender()));
        UserResponse userResponse = builder.build();
        responseObserver.onNext(userResponse);
        responseObserver.onCompleted();
    }

    private List<Order> getOrders(User user) {
        //get Order by Invoking order client
        ManagedChannel managedChannel = ManagedChannelBuilder.forTarget("localhost:50052").usePlaintext().build();
        OrderClient orderClient = new OrderClient(managedChannel);
        List<Order> orders =orderClient.getOrders(user.getId());
        try {
            managedChannel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            logger.log(Level.SEVERE,"Unable to Shutdown managed Channel", e);
        }
        return orders;
    }
}
