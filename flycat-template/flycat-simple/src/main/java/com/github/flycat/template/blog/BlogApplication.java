package com.github.flycat.template.blog;

import com.github.flycat.platform.springboot.SpringBootPlatform;
import com.github.flycat.platform.springboot.annotation.SpringBoot;

@SpringBoot
public class BlogApplication {

	public static void main(String[] args) {
		SpringBootPlatform.run(BlogApplication.class, args);
	}
}
