package net.khoudmi.billingservice;

import net.khoudmi.billingservice.entities.Bill;
import net.khoudmi.billingservice.entities.ProductItem;
import net.khoudmi.billingservice.feign.CustomerRestClient;
import net.khoudmi.billingservice.feign.ProductRestClient;
import net.khoudmi.billingservice.model.Customer;
import net.khoudmi.billingservice.model.Product;
import net.khoudmi.billingservice.repository.BillRepository;
import net.khoudmi.billingservice.repository.ProductItemRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Random;

@SpringBootApplication
@EnableFeignClients
public class BillingServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(BillingServiceApplication.class, args);
	}
	@Bean
	CommandLineRunner commandLineRunner(BillRepository billRepository,
										ProductItemRepository productItemRepository,
										CustomerRestClient customerRestClient,
										ProductRestClient productRestClient){


        return args -> {
			Collection<Customer> customers = customerRestClient.getAllCustomers().getContent();
			Collection<Product> products = productRestClient.getAllProducts().getContent();

			customers.forEach(customer -> {
				Bill bill = Bill.builder()
						.billingDate(new Date())
						.customerId(customer.getId())
						.build();
				billRepository.save(bill);
				products.forEach(product -> {
					ProductItem productItem = ProductItem.builder()
							.bill(bill)
							.productId(product.getId())
							.quantity(1+new Random().nextInt(10))
							.unitPrice(product.getPrice())
							.build();
					productItemRepository.save(productItem);
				});
			});
		};
    }

}
