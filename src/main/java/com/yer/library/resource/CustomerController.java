package com.yer.library.resource;

import com.yer.library.model.Customer;
import com.yer.library.model.Response;
import com.yer.library.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static com.yer.library.resource.Constants.MAX_PAGE_SIZE;
import static com.yer.library.resource.ControllerUtil.getDataMap;
import static java.time.LocalDateTime.now;
import static org.springframework.http.HttpStatus.*;

@RestController
@RequestMapping(path = "api/v1/customers")
@RequiredArgsConstructor
public class CustomerController {
    private final CustomerService customerService;

    @GetMapping(path = "{customerId}")
    public ResponseEntity<Response> getCustomer(@PathVariable("customerId") Long customerId) {
        return ResponseEntity.ok(
                Response.builder()
                        .timeStamp(now())
                        .data(getDataMap("customer", customerService.get(customerId)))
                        .message("Customer " + customerId + " retrieved")
                        .status(OK)
                        .statusCode(OK.value())
                        .build()
        );
    }

    @GetMapping
    public ResponseEntity<Response> getCustomers() {
        return ResponseEntity.ok(
                Response.builder()
                        .timeStamp(now())
                        .data(getDataMap("customers", customerService.list(MAX_PAGE_SIZE)))
                        .message("Customers retrieved")
                        .status(OK)
                        .statusCode(OK.value())
                        .build()
        );
    }

    @PostMapping
    public ResponseEntity<Response> addCustomer(@RequestBody @Valid Customer customer) {
        return ResponseEntity.ok(
                Response.builder()
                        .timeStamp(now())
                        .data(getDataMap("customer", customerService.add(customer)))
                        .message("Customer created")
                        .status(CREATED)
                        .statusCode(CREATED.value())
                        .build()
        );
    }

    @PutMapping(path = "{customerId}")
    public ResponseEntity<Response> updateCustomer(
            @PathVariable("customerId") Long customerId,
            @RequestBody @Valid Customer customer
    ) {
        return ResponseEntity.ok(
                Response.builder()
                        .timeStamp(now())
                        .data(getDataMap("customer", customerService.fullUpdate(customerId, customer)))
                        .message("Customer " + customerId + " updated")
                        .status(OK)
                        .statusCode(OK.value())
                        .build()
        );
    }

    @DeleteMapping(path = "{customerId}")
    public ResponseEntity<Response> deleteCustomer(@PathVariable("customerId") Long customerId) {
        return ResponseEntity.ok(
                Response.builder()
                        .timeStamp(now())
                        .data(getDataMap("soft deleted", customerService.delete(customerId)))
                        .message("Customer " + customerId + " deleted")
                        .status(NO_CONTENT)
                        .statusCode(NO_CONTENT.value())
                        .build()
        );
    }
}
