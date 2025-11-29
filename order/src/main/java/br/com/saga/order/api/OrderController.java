package br.com.saga.order.api;

import br.com.saga.order.services.CreateOrderService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/orders")
public class OrderController {
    @Autowired
    CreateOrderService createOrderService;

    @PostMapping
    @Transactional
    public ResponseEntity<Void> createOrder(
            @Valid @RequestBody CreateOrderRequest request) {

        createOrderService.create(request.getConsumerGuid());

        return ResponseEntity.ok().build();
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class CreateOrderRequest {
        @NotNull
        private UUID consumerGuid;
    }
}
