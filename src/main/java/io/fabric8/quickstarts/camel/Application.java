/*
 * Copyright 2005-2016 Red Hat, Inc.
 *
 * Red Hat licenses this file to you under the Apache License, version
 * 2.0 (the "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied.  See the License for the specific language governing
 * permissions and limitations under the License.
 */
package io.fabric8.quickstarts.camel;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.servlet.CamelHttpTransportServlet;
import org.apache.camel.model.rest.RestBindingMode;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ImportResource;
import org.springframework.stereotype.Component;
import org.apache.camel.Exchange;

@SpringBootApplication
@ImportResource({"classpath:spring/camel-context.xml"})
public class Application extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    ServletRegistrationBean servletRegistrationBean() {
        ServletRegistrationBean servlet = new ServletRegistrationBean(
            new CamelHttpTransportServlet(), "/restock/*");
        servlet.setName("RestockServlet");
        return servlet;
    }

    @Component
    class RestApi extends RouteBuilder {

        @Override
        public void configure() {
            restConfiguration()
                .contextPath("/restock").apiContextPath("/api-doc")
                    .apiProperty("api.title", "Restock REST API")
                    .apiProperty("api.version", "1.0")
                    .apiProperty("cors", "true")
                    .apiContextRouteId("doc-api")
                .component("servlet")
                .bindingMode(RestBindingMode.json);

            rest("/books")
        		.consumes("application/json")
        		.produces("application/json")
            	.description("Restock Books REST service")
                .post("/")
                	.description("Submit an order to restock a book")
                    .route().routeId("books-api")
                    .bean("orderService", "generateOrder(${body})")
                    .log("Processed restock order request #id: ${body.id} for: ${body.item} & a quantity of: ${body.quantity}")
                    .to("direct:callSupplier")
                    .endRest();
            
            from("direct:callSupplier")
	            .routeId("call-supplier")
	            .setHeader(Exchange.HTTP_METHOD, constant("POST"))
	            .setHeader(Exchange.CONTENT_TYPE, constant("application/json"))
	            .removeHeaders("CamelHttp*") // remove Camel headers to non camel endpoints - they will get rejected otherwise 
	            .setBody( simple("{ \"orderItem\": \"${body.id}\", \"quantity\": \"${body.quantity}\" }"))
	            .log("Supplier payload: ${body}")
	            .to("http://supplier-api-camel-test-brian.1ef9.nwr-dev.openshiftapps.com/orders/v1.0");
        }
    }
}