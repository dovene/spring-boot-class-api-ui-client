package com.dev.microservicesuiclient.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;

import com.dev.microservicesuiclient.model.Customer;

import reactor.core.publisher.Mono;

@Controller
@RequestMapping("/customers")
public class CustomerController {
    private final WebClient webClient;
    private final String gatewayBaseUrl = "http://localhost:8080/customers";

    public CustomerController(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl(gatewayBaseUrl).build();
    }

    @GetMapping("/")
    public String getAllCustomers(Model model) {
        Mono<Customer[]> customersMono = webClient.get()
                .retrieve()
                .bodyToMono(Customer[].class);
        model.addAttribute("customers", customersMono.block());
        return "customer-list";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("customer", new Customer());
        return "customer-create";
    }

    @PostMapping("/")
    public String createCustomer(@ModelAttribute Customer customer) {
        webClient.post()
                .body(Mono.just(customer), Customer.class)
                .retrieve()
                .bodyToMono(Void.class)
                .block();
        return "redirect:/customers/";
    }

    @GetMapping("/{id}/edit")
    public String showUpdateForm(@PathVariable Long id, Model model) {
        Mono<Customer> customerMono = webClient.get()
                .uri("/{id}", id)
                .retrieve()
                .bodyToMono(Customer.class);
        model.addAttribute("customer", customerMono.block());
        return "customer-update";
    }

    @PostMapping("/{id}")
    public String updateCustomer(@PathVariable Long id, @ModelAttribute Customer customer) {
        webClient.put()
                .uri("/{id}", id)
                .body(Mono.just(customer), Customer.class)
                .retrieve()
                .bodyToMono(Void.class)
                .block();
        return "redirect:/customers/";
    }

    @GetMapping("/{id}/delete")
    public String deleteCustomer(@PathVariable Long id) {
        webClient.delete()
                .uri("/{id}", id)
                .retrieve()
                .bodyToMono(Void.class)
                .block();
        return "redirect:/customers/";
    }
}

