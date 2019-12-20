/**
 * @filename:LoginInfoController 2019年9月6日
 * @project BuyBit  V1.0
 * Copyright(c) 2020 BianPeng Co. Ltd. 
 * All right reserved. 
 */
package com.flying.cattle.exchange.plugins.swagger;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * @ClassName: DataUtil
 * @Description: TODO(SwaggerConfig)
 * @author flying-cattle
 * @date 2019年12月19日
 */
@Configuration
@EnableSwagger2
public class SwaggerConfig {
	
	@Value("${server.port}")
    private String port;
	
	public static String CONTROLLER_URL="com.flying.cattle.exchange.controller";    //Swagger扫描的接口路径
	public static String SWAGGER_TITLE="API文档-BianPeng"; 	    					//Swagger接口文档标题
	public static String SWAGGER_DESCRIPTION="API文档";   							//Swagger接口文档描述
	public static String SWAGGER_VERSION="1.0";                         			//Swagger接口文档版本
	public final  String SWAGGER_URL="http://127.0.0.1:"+port;    					//Swagger项目服务的URL
	
	//验证的页面http://127.0.0.1:8070/swagger-ui.html
	@Bean
    public Docket createRestApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage(CONTROLLER_URL))
                .paths(PathSelectors.any())
                .build();
//				.globalOperationParameters( new ArrayList<Parameter>(){{
//					//token参数
//					add(new ParameterBuilder(){{
//						name("token").description("登录token-需要登录的接口必须传递").modelRef(new ModelRef("string")).parameterType("header").required(false).build();
//					}}.build());
//				}})
    }
    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title(SWAGGER_TITLE)
                .description(SWAGGER_DESCRIPTION)
                .termsOfServiceUrl(SWAGGER_URL)
                .version(SWAGGER_VERSION)
                .build();
    }
}
