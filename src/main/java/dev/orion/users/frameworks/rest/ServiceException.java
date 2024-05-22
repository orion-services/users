/**
 * @License
 * Copyright 2024 Orion Services @ https://orion-services.dev
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package dev.orion.users.frameworks.rest;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;

/**
 * Frameworks and Drivers layer of Clean Architecture.
 */
public class ServiceException extends WebApplicationException {

    /**
     * The version number for serialization and deserialization of objects of
     * this class.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Service Exception constructor.
     *
     * @param message : The message of the exception
     * @param status  : The HTTP error code
     */
    public ServiceException(final String message, final Status status) {
        super(init(message, status));
    }

    /**
     * A static method to init the message.
     *
     * @param message : An error message
     * @param status  : A HTTP error code
     *
     * @return A Response object
     */
    private static Response init(final String message, final Status status) {
        List<Map<String, String>> violations = new ArrayList<>();
        violations.add(Map.of("message", message));

        return Response
            .status(status)
            .entity(Map.of("violations", violations))
            .build();
    }
}
