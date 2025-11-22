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

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.PreMatching;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;

/**
 * CORS Request Filter to handle preflight OPTIONS requests.
 */
@Provider
@PreMatching
public class CorsRequestFilter implements ContainerRequestFilter {

    @Override
    public void filter(ContainerRequestContext requestContext) {
        // Handle preflight requests
        if (requestContext.getRequest().getMethod().equals("OPTIONS")) {
            String origin = requestContext.getHeaderString("Origin");
            Response.ResponseBuilder response = Response.ok();
            
            if (origin != null && (origin.startsWith("http://localhost:") || origin.startsWith("https://localhost:"))) {
                response.header("Access-Control-Allow-Origin", origin);
            } else {
                response.header("Access-Control-Allow-Origin", "*");
            }
            
            response.header("Access-Control-Allow-Credentials", "true");
            response.header("Access-Control-Allow-Headers",
                    "origin, content-type, accept, authorization, x-requested-with");
            response.header("Access-Control-Allow-Methods",
                    "GET, POST, PUT, DELETE, OPTIONS, HEAD, PATCH");
            response.header("Access-Control-Max-Age", "3600");
            
            requestContext.abortWith(response.build());
        }
    }
}

