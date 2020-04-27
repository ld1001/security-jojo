/**
 * 
 */
package com.imooc.security.app;

import com.imooc.security.core.properties.SecurityProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.security.oauth2.provider.token.store.redis.RedisTokenStore;

import java.security.Security;

/**
 * @author zhailiang
 *
 */
@Configuration
public class TokenStoreConfig {
	
	@Autowired
	private RedisConnectionFactory redisConnectionFactory;
	
	@Bean
	@ConditionalOnProperty(prefix = "imooc.security.oauth2", name = "tokenstore", havingValue = "redis")
	public TokenStore redisTokenStore() {
		return new RedisTokenStore(redisConnectionFactory);
	}

	@Configuration
	// matchIfMissing = true表示如果没有配置这个配置也是有效的
	@ConditionalOnProperty(prefix = "imooc.security.oauth2", name = "tokenstore", havingValue = "jwt", matchIfMissing = true)
	public static class JwtTokenConfig {

		@Autowired
		private SecurityProperties securityProperties;

		// JwtAccessTokenConverter用来对token进行签名
		@Bean
		public JwtAccessTokenConverter jwtAccessTokenConverter() {
			JwtAccessTokenConverter jwtAccessTokenConverter = new JwtAccessTokenConverter();
			jwtAccessTokenConverter.setSigningKey(securityProperties.getOauth2().getJwtSigningKey());
			return jwtAccessTokenConverter;
		}

		// 存储
		@Bean
		public TokenStore jwtTokenStore() {
			return new JwtTokenStore(jwtAccessTokenConverter());
		}
	}

}
