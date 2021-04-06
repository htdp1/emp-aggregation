package com.htdp1.empaggregation.route;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.apache.camel.model.rest.RestBindingMode;
import org.apache.camel.processor.aggregate.StringAggregationStrategy;
import org.springframework.stereotype.Component;

@Component
public class EmpRoute extends RouteBuilder {

	@Override
	public void configure() throws Exception {
		intercept().to("log:hello");

		restConfiguration().component("servlet").bindingMode(RestBindingMode.json);

		rest("aggregation") //
				.get("/employees") //
				.to("direct:employees") //
				.get("/departments") //
				.to("direct:departments") //
				.get("/emp_dept") //
				.to("direct:emp_dept") //
		;

		String toUri1 = "http://localhost:8090/emp-manage/api/employees/10001?bridgeEndpoint=true";
		from("direct:employees") //
//				.setHeader("CamelHttpMethod", constant("GET")) //
//				.setHeader("CamelHttpUri", simple(toUri1)) //
				.to(toUri1) //
				.convertBodyTo(String.class) //
				.unmarshal().json(JsonLibrary.Jackson) //
//				.end() //
		;

		String toUri2 = "http://localhost:8090/emp-manage/v1/departments/d001?bridgeEndpoint=true";
		from("direct:departments") //
//				.setHeader("CamelHttpMethod", constant("GET")) //
//				.setHeader("CamelHttpUri", simple(toUri2)) //
				.to(toUri2) //
				.convertBodyTo(String.class) //
				.unmarshal().json(JsonLibrary.Jackson) //
//				.end() //
		;

		from("direct:emp_dept") //
//				.aggregate(constant(true), new GroupedMessageAggregationStrategy())
//				.completionTimeout(3000)
				.multicast((new StringAggregationStrategy()).delimiter("----")) //
				.parallelProcessing() //
				.to("direct:employees") //
				.to("direct:departments") //
				.end() //
//				.log("${body}") //
//				.convertBodyTo(String.class) //
//				.unmarshal().json(JsonLibrary.Jackson) //
//				.end() //
		;
	}
}