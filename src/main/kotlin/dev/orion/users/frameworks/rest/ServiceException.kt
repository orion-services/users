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
package dev.orion.users.frameworks.rest

import jakarta.ws.rs.WebApplicationException
import jakarta.ws.rs.core.Response
import jakarta.ws.rs.core.Response.Status

/**
 * Frameworks and Drivers layer of Clean Architecture.
 */
class ServiceException(message: String, status: Status) : WebApplicationException(init(message, status)) {

    companion object {
        /**
         * A static method to init the message.
         *
         * @param message : An error message
         * @param status  : A HTTP error code
         *
         * @return A Response object
         */
        private fun init(message: String, status: Status): Response {
            val violations = listOf(mapOf("message" to message))

            return Response
                .status(status)
                .entity(mapOf("violations" to violations))
                .build()
        }
    }
}

