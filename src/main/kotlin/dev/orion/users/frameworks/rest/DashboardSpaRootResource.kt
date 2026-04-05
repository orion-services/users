/*
 * Copyright 2026 Orion Services.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/**
 * Serve o index.html do admin em GET /dashboard (sem barra final).
 * Ficheiros estáticos (/dashboard/assets/...) são servidos pelo Quarkus a partir de META-INF/resources/dashboard/.
 */
package dev.orion.users.frameworks.rest

import jakarta.enterprise.context.ApplicationScoped
import jakarta.ws.rs.GET
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response

@ApplicationScoped
@Path("/dashboard")
class DashboardSpaRootResource {
    @GET
    @Produces(MediaType.TEXT_HTML)
    fun index(): Response {
        val bytes =
            javaClass.classLoader
                .getResourceAsStream("META-INF/resources/dashboard/index.html")
                ?.use { it.readAllBytes() }
                ?: return Response.status(Response.Status.NOT_FOUND).build()
        return Response.ok(bytes, MediaType.TEXT_HTML).build()
    }
}
