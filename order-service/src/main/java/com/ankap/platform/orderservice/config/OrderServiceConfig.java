package com.ankap.platform.orderservice.config;

import com.ankap.platform.orderservice.application.port.in.PayForOrderUseCase;
import com.ankap.platform.orderservice.application.port.in.PlaceOrderUseCase;
import com.ankap.platform.orderservice.application.port.in.SettlePaymentUseCase;
import com.ankap.platform.orderservice.application.port.in.UpdateOrderStatusUseCase;
import com.ankap.platform.orderservice.application.port.out.OrderEventPublisherPort;
import com.ankap.platform.orderservice.application.port.out.OrderRepositoryPort;
import com.ankap.platform.orderservice.application.port.out.PaymentPort;
import com.ankap.platform.orderservice.application.port.out.ProductCheckPort;
import com.ankap.platform.orderservice.application.service.PayForOrderService;
import com.ankap.platform.orderservice.application.service.PlaceOrderService;
import com.ankap.platform.orderservice.application.service.SettlePaymentService;
import com.ankap.platform.orderservice.application.service.UpdateOrderStatusService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OrderServiceConfig {

    @Bean
    public PlaceOrderUseCase placeOrderUseCase(OrderRepositoryPort repositoryPort, ProductCheckPort productCheckPort, OrderEventPublisherPort eventPublisherPort) {
        return new PlaceOrderService(repositoryPort, productCheckPort, eventPublisherPort);
    }

    @Bean
    public UpdateOrderStatusUseCase updateOrderStatusUseCase(OrderRepositoryPort repositoryPort) {
        return new UpdateOrderStatusService(repositoryPort);
    }

    @Bean
    public SettlePaymentUseCase settlePaymentUseCase(OrderRepositoryPort repositoryPort, OrderEventPublisherPort eventPublisherPort) {
        return new SettlePaymentService(repositoryPort, eventPublisherPort);
    }

    @Bean
    public PayForOrderUseCase payForOrderUseCase(OrderRepositoryPort repositoryPort, PaymentPort paymentPort, SettlePaymentUseCase settlePaymentUseCase) {
        return new PayForOrderService(repositoryPort, paymentPort, settlePaymentUseCase);
    }
}
