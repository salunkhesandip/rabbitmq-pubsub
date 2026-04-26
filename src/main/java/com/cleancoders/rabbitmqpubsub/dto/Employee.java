package com.cleancoders.rabbitmqpubsub.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * Inbound request body — represents an employee to be created.
 */
public record Employee(

        @NotBlank(message = "id must not be blank")
        String id,

        @NotBlank(message = "name must not be blank")
        String name,

        @Email(message = "email must be a valid address")
        @NotBlank(message = "email must not be blank")
        String email,

        @NotBlank(message = "department must not be blank")
        String department
) {}
