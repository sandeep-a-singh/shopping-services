package com.shopping.service;

import com.google.protobuf.util.Timestamps;
import com.shopping.db.Order;
import com.shopping.db.OrderDao;
import com.shopping.stubs.order.OrderRequest;
import com.shopping.stubs.order.OrderResponse;
import com.shopping.stubs.order.OrderServiceGrpc;
import io.grpc.stub.StreamObserver;

import java.util.List;
import java.util.stream.Collectors;

public class OrderServiceImpl extends OrderServiceGrpc.OrderServiceImplBase {
    private OrderDao orderDao = new OrderDao();
    @Override
    public void getOrderForUser(OrderRequest request, StreamObserver<OrderResponse> responseObserver) {
        List<Order> orders = orderDao.getOrders(request.getUserId());

        List<com.shopping.stubs.order.Order> orderList = orders.stream()
                .map(o -> com.shopping.stubs.order.Order.newBuilder()
                                                        .setOrderId(o.getOrderId())
                                                        .setUserId(o.getUserId())
                                                        .setNoOfItems(o.getNoOfItems())
                                                        .setTotalAmount(o.getTotalAmount())
                                                        .setOrderDate(Timestamps.fromMillis(o.getOrderDate().getTime()))
                                                        .build())
                                                        .collect(Collectors.toList());

        OrderResponse orderResponse = OrderResponse.newBuilder().addAllOrder(orderList).build();
        responseObserver.onNext(orderResponse);
        responseObserver.onCompleted();

    }
}
