package com.lingsb.gateway.filter;

import com.lingsb.gateway.feignclient.Oauth2ServiceClient;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Component
public class AuthFilter implements GlobalFilter,Ordered {


    @Autowired
    @Lazy
    private Oauth2ServiceClient oauth2ServiceClient;

    @SneakyThrows
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();
        String path = request.getURI().getPath();
        if(path.contains("/oauth")
                || path.contains("/user/register")) {
            return chain.filter(exchange);
        }

        String token = request.getHeaders().getFirst("Authorization");
//        Map<String, Object> result = oauth2ServiceClient.checkToken(token); //这是同步的调用，rest发送请求，等待结果回执
       //改为异步形式，看看效果
        CompletableFuture<Map> future = CompletableFuture.supplyAsync(() -> {
           return oauth2ServiceClient.checkToken(token);
        });
        Map<String, Object> result = future.get();
        boolean active = (boolean) result.get("active");
        if(!active) {
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return response.setComplete();
        }
        //比如说我们可以给微服务转发请求的时候带上一些header
//        ServerHttpRequest httpRequest = request.mutate().headers(httpHeaders -> {
//            httpHeaders.set("personId", request.getHeaders().getFirst("personId"));
//            httpHeaders.set("tracingId", "");
//        }).build();
//        exchange.mutate().request(httpRequest);

        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
