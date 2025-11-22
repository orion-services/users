/**
 * @License
 * Copyright 2025 Orion Services @ https://orion-services.dev
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

import jakarta.annotation.Priority;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.ext.Provider;
import java.io.IOException;

/**
 * CORS Filter to handle Cross-Origin Resource Sharing.
 */
@Provider
@Priority(1)
public class CorsFilter implements ContainerResponseFilter {

    @Override
    public void filter(ContainerRequestContext requestContext,
                       ContainerResponseContext responseContext) throws IOException {
        MultivaluedMap<String, Object> headers = responseContext.getHeaders();

        // Remover headers CORS existentes para evitar duplicação
        headers.remove("Access-Control-Allow-Origin");
        headers.remove("Access-Control-Allow-Credentials");
        headers.remove("Access-Control-Allow-Headers");
        headers.remove("Access-Control-Allow-Methods");
        headers.remove("Access-Control-Max-Age");

        // Adicionar headers CORS corretos
        String origin = requestContext.getHeaderString("Origin");
        if (origin != null && (origin.startsWith("http://localhost:") || origin.startsWith("https://localhost:"))) {
            headers.putSingle("Access-Control-Allow-Origin", origin);
        } else {
            headers.putSingle("Access-Control-Allow-Origin", "*");
        }

        headers.putSingle("Access-Control-Allow-Credentials", "true");
        headers.putSingle("Access-Control-Allow-Headers",
                "origin, content-type, accept, authorization, x-requested-with");
        headers.putSingle("Access-Control-Allow-Methods",
                "GET, POST, PUT, DELETE, OPTIONS, HEAD, PATCH");
        headers.putSingle("Access-Control-Max-Age", "3600");
    }
}

