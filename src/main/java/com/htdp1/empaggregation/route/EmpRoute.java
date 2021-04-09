package com.htdp1.empaggregation.route;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.apache.camel.model.rest.RestBindingMode;
import org.apache.camel.processor.aggregate.GroupedBodyAggregationStrategy;
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
				.get("/departments_d001") //
				.to("direct:departments_d001") //
		;

		String toUri1 = "http://localhost:8090/emp-manage/v1/employees?bridgeEndpoint=true";
		from("direct:employees") //
//				.setHeader("CamelHttpMethod", constant("GET")) //
//				.setHeader("CamelHttpUri", simple(toUri1)) //
				.to(toUri1) //
//				.convertBodyTo(String.class) //
				.unmarshal().json(JsonLibrary.Jackson) //
				.end() //
		;

		String toUri2 = "http://localhost:8090/emp-manage/v1/departments?bridgeEndpoint=true";
		from("direct:departments") //
//				.setHeader("CamelHttpMethod", constant("GET")) //
//				.setHeader("CamelHttpUri", simple(toUri2)) //
				.to(toUri2) //
//				.convertBodyTo(String.class) //
				.unmarshal().json(JsonLibrary.Jackson) //
				.end() //
		;

		from("direct:departments_d001") //
//		.setHeader("CamelHttpMethod", constant("GET")) //
//		.setHeader("CamelHttpUri", simple(toUri2)) //
				.to(toUri2) //
				.choice() //
					.when().jsonpath("$.data[?(@.deptNo == 'd001')]") //
						.convertBodyTo(String.class) //
						.to("mock:middle") //
//				.unmarshal().json(JsonLibrary.Jackson) //
//						.to("log:filter") //
//				.to("direct:filter")
//		.convertBodyTo(String.class) //
//				.unmarshal().json(JsonLibrary.Jackson) //
				.end() //
		;

		from("direct:filter") //
				.to("log:filter") //
				.convertBodyTo(String.class) //
//				.unmarshal().json(JsonLibrary.Jackson) //
		;

		from("direct:emp_dept") //
//				.aggregate(constant(true), new GroupedMessageAggregationStrategy())
//				.completionTimeout(3000)
//				.multicast(new AggregationStrategy() {
//					public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {
//						log.debug("aggregate");
//						
//						if (oldExchange != null) {
//							log.debug("old" + oldExchange.getIn().getBody());
//						}
//						if (newExchange != null) {
//							log.debug("new" + newExchange.getIn().getBody());
//						}
//						
//						return oldExchange != null ? oldExchange : newExchange;
//					}
//				}) //
				.multicast(new GroupedBodyAggregationStrategy()) //
				.parallelProcessing() //
				.to("direct:departments") //
				.to("direct:employees") //
				.end() //
//				.convertBodyTo(String.class) //
//				.unmarshal().json(JsonLibrary.Jackson) //
//				.end() //
		;
	}
}