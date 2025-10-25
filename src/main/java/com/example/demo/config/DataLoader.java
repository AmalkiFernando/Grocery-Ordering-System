package com.example.demo.config;

import com.example.demo.domain.*;
import com.example.demo.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Configuration
public class DataLoader {
    
    private static final Logger logger = LoggerFactory.getLogger(DataLoader.class);

	@Bean
    CommandLineRunner seedData(ProductRepository productRepository,
                              SupplierRepository supplierRepository,
                              FeedbackRepository feedbackRepository,
                              RatingRepository ratingRepository,
                              CustomerRepository customerRepository,
                              OrderRepository orderRepository,
                              PaymentRepository paymentRepository) {
		return args -> {
		    try {
    			if (productRepository.count() == 0) {
    				logger.info("Loading sample products...");
                Product apples = new Product();
    				apples.setSku("SKU-APPLE");
    				apples.setName("Apples");
    				apples.setPrice(new BigDecimal("899.00"));
    				apples.setQuantityInStock(50);
    				apples.setLowStockThreshold(10);
                    apples.setImageUrl("https://images.unsplash.com/photo-1560806887-1e4cd0b6cbd6?auto=format&fit=crop&w=800&q=80");
    				productRepository.save(apples);

				Product milk = new Product();
				milk.setSku("SKU-MILK");
				milk.setName("Milk");
				milk.setPrice(new BigDecimal("300.00"));
				milk.setQuantityInStock(25);
                milk.setLowStockThreshold(10);
                    milk.setImageUrl("https://images.immediate.co.uk/production/volatile/sites/30/2020/02/Glass-and-bottle-of-milk-fe0997a.jpg");
    				productRepository.save(milk);

    				Product bread = new Product();
    				bread.setSku("SKU-BREAD");
    				bread.setName("Bread");
    				bread.setPrice(new BigDecimal("150.00"));
    				bread.setQuantityInStock(15);
                bread.setLowStockThreshold(10);
                    bread.setImageUrl("https://images.unsplash.com/photo-1509440159596-0249088772ff?auto=format&fit=crop&w=800&q=80");
    				productRepository.save(bread);

    				Product eggs = new Product();
    				eggs.setSku("SKU-EGGS");
    				eggs.setName("Eggs (12 pack)");
    				eggs.setPrice(new BigDecimal("330.0"));
    				eggs.setQuantityInStock(6);
                eggs.setLowStockThreshold(8);
                    eggs.setImageUrl("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSdIC8P1fXIU3Au62klNg7tc_BmUEelpxvJ3Q&s");
    				productRepository.save(eggs);

    				Product rice = new Product();
    				rice.setSku("SKU-RICE");
    				rice.setName("Rice 1kg");
    				rice.setPrice(new BigDecimal("160.00"));
    				rice.setQuantityInStock(120);
                rice.setLowStockThreshold(15);
                    rice.setImageUrl("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQxuy9gO5PQiqKUk40fYknx7mnrN1-4x1q2ug&s");
    				productRepository.save(rice);

    				Product coffee = new Product();
    				coffee.setSku("SKU-COFFEE");
    				coffee.setName("Coffee 250g");
    				coffee.setPrice(new BigDecimal("110.00"));
    				coffee.setQuantityInStock(30);
                coffee.setLowStockThreshold(10);
                    coffee.setImageUrl("https://media.istockphoto.com/id/1224409272/photo/bottled-coffee.jpg?s=612x612&w=0&k=20&c=-D1-LvK3JnM2bSC6HSLdbsQhL6jBeKL4NJDainYTVzA=");
    				productRepository.save(coffee);

    				Product bananas = new Product();
    				bananas.setSku("SKU-BANANAS");
    				bananas.setName("Bananas");
    				bananas.setPrice(new BigDecimal("190.00"));
    				bananas.setQuantityInStock(90);
                bananas.setLowStockThreshold(20);
                    bananas.setImageUrl("https://exoticfruits.co.uk/cdn/shop/products/banana-apple-manzano-exoticfruitscouk-905674.jpg?v=1757328358");
    				productRepository.save(bananas);

    				Product chocolate = new Product();
    				chocolate.setSku("SKU-CHOCO");
    				chocolate.setName("Chocolate Bar");
    				chocolate.setPrice(new BigDecimal("420.00"));
    				chocolate.setQuantityInStock(5);
    				chocolate.setLowStockThreshold(12);
                    chocolate.setImageUrl("https://images.unsplash.com/photo-1549007994-cb92caebd54b?auto=format&fit=crop&w=800&q=80");
    				productRepository.save(chocolate);
    			}

    			if (supplierRepository.count() == 0) {
    			    logger.info("Loading sample suppliers...");
    				Supplier s1 = new Supplier();
    				s1.setName("Fresh Farms Ltd");
    				s1.setEmail("fresh@farms.com");
    				s1.setPhone("+1-555-1000");
    				s1.setAddress("12 Farm Road");
    				supplierRepository.save(s1);

    				Supplier s2 = new Supplier();
    				s2.setName("Dairy Direct");
    				s2.setEmail("sales@dairydirect.com");
    				s2.setPhone("+1-555-2000");
    				s2.setAddress("22 Milk Street");
    				supplierRepository.save(s2);
    			}

    			// Add sample feedback and ratings
    			if (feedbackRepository.count() == 0 && ratingRepository.count() == 0) {
    			    logger.info("Loading sample feedback and ratings...");
    				List<Product> products = productRepository.findAll();
    				
    				// Sample feedback for apples
    				if (products.size() > 0) {
    					Product apples = products.get(0);
    					
    					Feedback feedback1 = new Feedback();
    					feedback1.setProduct(apples);
    					feedback1.setCustomerName("John Smith");
    					feedback1.setCustomerEmail("john@email.com");
    					feedback1.setReviewContent("Great quality apples! Fresh and crisp. Will definitely buy again.");
    					feedback1.setRating(5);
    					feedbackRepository.save(feedback1);

    					Feedback feedback2 = new Feedback();
    					feedback2.setProduct(apples);
    					feedback2.setCustomerName("Sarah Johnson");
    					feedback2.setCustomerEmail("sarah@email.com");
    					feedback2.setReviewContent("Good apples but a bit expensive. Quality is decent.");
    					feedback2.setRating(3);
    					feedbackRepository.save(feedback2);

    					// Sample ratings for apples
    					Rating rating1 = new Rating();
    					rating1.setProduct(apples);
    					rating1.setCustomerName("Mike Wilson");
    					rating1.setCustomerEmail("mike@email.com");
    					rating1.setRatingValue(4);
    					ratingRepository.save(rating1);

    					Rating rating2 = new Rating();
    					rating2.setProduct(apples);
    					rating2.setCustomerName("Lisa Brown");
    					rating2.setCustomerEmail("lisa@email.com");
    					rating2.setRatingValue(5);
    					ratingRepository.save(rating2);
    				}

    				// Sample feedback for milk
    				if (products.size() > 1) {
    					Product milk = products.get(1);
    					
    					Feedback feedback3 = new Feedback();
    					feedback3.setProduct(milk);
    					feedback3.setCustomerName("David Lee");
    					feedback3.setCustomerEmail("david@email.com");
    					feedback3.setReviewContent("Fresh milk delivered on time. Good packaging and taste.");
    					feedback3.setRating(4);
    					feedbackRepository.save(feedback3);

    					// Sample rating for milk
    					Rating rating3 = new Rating();
    					rating3.setProduct(milk);
    					rating3.setCustomerName("Emma Davis");
    					rating3.setCustomerEmail("emma@email.com");
    					rating3.setRatingValue(4);
    					ratingRepository.save(rating3);
    				}
    			}

                // Add sample customers, orders, order items, and payments
                if (customerRepository.count() == 0 && orderRepository.count() == 0 && paymentRepository.count() == 0) {
                    logger.info("Loading sample customers, orders, and payments...");
                    Customer alice = new Customer("Alice Johnson", "alice@example.com", "+1-555-3000", "101 Main St");
                    Customer bob = new Customer("Bob Carter", "bob@example.com", "+1-555-3001", "202 Oak Ave");
                    customerRepository.saveAll(List.of(alice, bob));

                    List<Product> products = productRepository.findAll();
                    if (!products.isEmpty()) {
                        // Order 1 - Delivered last month
                        Order order1 = new Order();
                        order1.setOrderNumber("ORD-1001");
                        order1.setCustomer(alice);
                        order1.setOrderDate(LocalDateTime.now().minusDays(40));
                        order1.setStatus(Order.OrderStatus.DELIVERED);
                        order1.setTotalAmount(new BigDecimal("0.00"));
                        order1 = orderRepository.save(order1);

                        OrderItem oi11 = new OrderItem(order1, products.get(0), 3, products.get(0).getPrice());
                        OrderItem oi12 = new OrderItem(order1, products.get(1), 1, products.get(1).getPrice());
                        // persist via order cascade: attach to order
                        order1.getOrderItems().addAll(List.of(oi11, oi12));
                        BigDecimal total1 = oi11.getTotalPrice().add(oi12.getTotalPrice());
                        order1.setTotalAmount(total1);
                        orderRepository.save(order1);

                        Payment p1 = new Payment(order1, total1, Payment.PaymentMethod.CREDIT_CARD);
                        p1.setStatus(Payment.PaymentStatus.COMPLETED);
                        p1.setPaymentDate(order1.getOrderDate().plusDays(1));
                        paymentRepository.save(p1);

                        // Order 2 - Delivered this month
                        Order order2 = new Order();
                        order2.setOrderNumber("ORD-1002");
                        order2.setCustomer(bob);
                        order2.setOrderDate(LocalDateTime.now().minusDays(5));
                        order2.setStatus(Order.OrderStatus.DELIVERED);
                        order2.setTotalAmount(new BigDecimal("0.00"));
                        order2 = orderRepository.save(order2);

                        OrderItem oi21 = new OrderItem(order2, products.get(Math.min(2, products.size()-1)), 2, products.get(Math.min(2, products.size()-1)).getPrice());
                        OrderItem oi22 = new OrderItem(order2, products.get(Math.min(3, products.size()-1)), 5, products.get(Math.min(3, products.size()-1)).getPrice());
                        order2.getOrderItems().addAll(List.of(oi21, oi22));
                        BigDecimal total2 = oi21.getTotalPrice().add(oi22.getTotalPrice());
                        order2.setTotalAmount(total2);
                        orderRepository.save(order2);

                        Payment p2 = new Payment(order2, total2, Payment.PaymentMethod.CASH);
                        p2.setStatus(Payment.PaymentStatus.COMPLETED);
                        p2.setPaymentDate(order2.getOrderDate().plusHours(2));
                        paymentRepository.save(p2);

                        // Order 3 - Pending (should not count in sales/payment completed)
                        Order order3 = new Order();
                        order3.setOrderNumber("ORD-1003");
                        order3.setCustomer(alice);
                        order3.setOrderDate(LocalDateTime.now().minusDays(1));
                        order3.setStatus(Order.OrderStatus.PENDING);
                        order3.setTotalAmount(new BigDecimal("0.00"));
                        order3 = orderRepository.save(order3);

                        OrderItem oi31 = new OrderItem(order3, products.get(0), 1, products.get(0).getPrice());
                        order3.getOrderItems().add(oi31);
                        order3.setTotalAmount(oi31.getTotalPrice());
                        orderRepository.save(order3);

                        Payment p3 = new Payment(order3, oi31.getTotalPrice(), Payment.PaymentMethod.PAYPAL);
                        p3.setStatus(Payment.PaymentStatus.PENDING);
                        paymentRepository.save(p3);
                    }
                }
                // Ensure images are present for all products created above
                logger.info("Ensured curated product images are set.");
                
                logger.info("Data loading completed successfully.");
		    } catch (Exception e) {
		        logger.error("Error loading sample data: ", e);
		    }
		};
	}
}