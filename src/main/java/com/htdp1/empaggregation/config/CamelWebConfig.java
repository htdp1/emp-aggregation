package com.htdp1.empaggregation.config;

import org.apache.camel.component.servlet.CamelHttpTransportServlet;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CamelWebConfig {
	private static final String CAMEL_URL_MAPPING = "/v1/*";
	private static final String CAMEL_SERVLET_NAME = "CamelServlet";

	@Bean
	public ServletRegistrationBean<CamelHttpTransportServlet> servletRegistrationBean() {

		CamelHttpTransportServlet camelHttpTransportServlet = new CamelHttpTransportServlet();
		camelHttpTransportServlet.setAsync(true);

		ServletRegistrationBean<CamelHttpTransportServlet> registration = new ServletRegistrationBean<CamelHttpTransportServlet>(
				camelHttpTransportServlet, CAMEL_URL_MAPPING);
		registration.setName(CAMEL_SERVLET_NAME);

		return registration;
	}
}