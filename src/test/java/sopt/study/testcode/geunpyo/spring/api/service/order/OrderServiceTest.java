package sopt.study.testcode.geunpyo.spring.api.service.order;

import static org.assertj.core.api.Assertions.*;
import static sopt.study.testcode.geunpyo.spring.domain.product.ProductSellingStatus.*;
import static sopt.study.testcode.geunpyo.spring.domain.product.ProductType.*;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import sopt.study.testcode.geunpyo.spring.api.controller.order.request.OrderCreateRequest;
import sopt.study.testcode.geunpyo.spring.api.service.order.response.OrderResponse;
import sopt.study.testcode.geunpyo.spring.domain.order.OrderRepository;
import sopt.study.testcode.geunpyo.spring.domain.orderproduct.OrderProduct;
import sopt.study.testcode.geunpyo.spring.domain.orderproduct.OrderProductRepository;
import sopt.study.testcode.geunpyo.spring.domain.product.Product;
import sopt.study.testcode.geunpyo.spring.domain.product.ProductRepository;
import sopt.study.testcode.geunpyo.spring.domain.product.ProductType;

@ActiveProfiles("test")
@SpringBootTest
class OrderServiceTest {

	@Autowired
	private ProductRepository productRepository;

	@Autowired
	private OrderRepository orderRepository;

	@Autowired
	private OrderProductRepository orderProductRepository;

	@Autowired
	private OrderService orderService;

	@AfterEach
	void tearDown() {
		orderProductRepository.deleteAllInBatch();
		productRepository.deleteAllInBatch();
		orderRepository.deleteAllInBatch();
	}

	@DisplayName("주문번호 리스트를 받아 주문을 생성한다.")
	@Test
	void createOrder() {
		//given
		LocalDateTime registeredDateTime = LocalDateTime.now();
		Product product1 = createProduct(HANDMADE, "001", 1000);
		Product product2 = createProduct(HANDMADE, "002", 3000);
		Product product3 = createProduct(HANDMADE, "003", 5000);

		productRepository.saveAll(List.of(product1, product2, product3));
		OrderCreateRequest createRequest = OrderCreateRequest.builder()
			.productNumbers(List.of("001", "002"))
			.build();

		//when
		OrderResponse orderResponse = orderService.createOrder(createRequest, registeredDateTime);

		//then
		assertThat(orderResponse.getId()).isNotNull();
		assertThat(orderResponse)
			.extracting("registeredDateTime", "totalPrice")
			.contains(registeredDateTime, 4000);
		assertThat(orderResponse.getProducts()).hasSize(2)
			.extracting("productNumber", "price")
			.containsExactlyInAnyOrder(
				tuple("001", 1000),
				tuple("002", 3000)
			);
	}
	
	@DisplayName("중복되는 상품번호 리스트로 주문을 생성할 수 있다.")
	@Test
	void createOrderWithDuplicateProductNumbers() {
		//given
		LocalDateTime registeredDateTime = LocalDateTime.now();
		Product product1 = createProduct(HANDMADE, "001", 1000);
		Product product2 = createProduct(HANDMADE, "002", 3000);
		Product product3 = createProduct(HANDMADE, "003", 5000);

		productRepository.saveAll(List.of(product1, product2, product3));
		OrderCreateRequest createRequest = OrderCreateRequest.builder()
			.productNumbers(List.of("001", "001"))
			.build();

		//when
		OrderResponse orderResponse = orderService.createOrder(createRequest, registeredDateTime);

		//then
		assertThat(orderResponse.getId()).isNotNull();
		assertThat(orderResponse)
			.extracting("registeredDateTime", "totalPrice")
			.contains(registeredDateTime, 2000);
		assertThat(orderResponse.getProducts()).hasSize(2)
			.extracting("productNumber", "price")
			.containsExactlyInAnyOrder(
				tuple("001", 1000),
				tuple("001", 1000)
			);
	 
	 }
	

	private Product createProduct(ProductType type, String productNumber, int price) {
		return Product.builder()
			.type(type)
			.productNumber(productNumber)
			.price(price)
			.sellingStatus(SELLING)
			.name("메뉴 이름")
			.build();
	}

}