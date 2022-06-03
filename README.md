# -
商品秒杀项目

用户登录后对商品进行秒杀购买，并通过流量削峰、接口隐藏等操作进行服务优化

作者：王晓雨

技术栈：SpringBoot、Spring、SpringMVC、MyBatis、MySQL、Redis、RabbitMQ、JQuery、Themeleaf

项目描述：

（1）两次MD5加密：浏览器MD5加密去后端，后端MD5加密存到数据库

（2）登录时：手机号码、密码等参数校验，非空、长度等要求，使用@Valid注解，异常枚举类

（3）实现分布式Session：使用cookie+session记录用户信息(包含uuid生成的一组序列)，并采用springsession解决分布式共享问题

（4）springboot的拦截器进行用户登录拦截并验证。实现HandlerInterceptor接口来使用拦截器，在preHandle方法里完成用户登录拦截并验证。如果请求中的cookie信息与redis里的springsession中的用户信息能匹配上，说明该用户已经登陆过，那么 Controller 就会继续后面的操作；如果不存在，就会重定向到登录界面。实现WebMvcConfigurer接口来实现一个配置类，将上面实现的拦截器的一个对象注册到这个配置类中（重写addInterceptors方法）。将拦截器注册到了拦截器列表中，并且指明了拦截哪些访问路径，不拦截哪些访问路径，不拦截哪些资源文件；最后再以 @Configuration 注解将配置注入

（5）基本的秒杀业务：登录-->商品列表-->选择秒杀商品-->秒杀成功进入订单页

（6）将用户、商品和订单信息加载到redis中，减少数据库访问，采用“先更新后删除”保证redis和Mysql中数据保持一致

（7）解决超卖问题：
  
  1）Setnx：在java里是setIfAbsent()方法，传入key（设置的一个锁）、value和失效时间，同时在删除这个key的部分要注意避免错删（删除的是其他线程加的锁，比如自己的锁在执行业务过程中超时失效了，删除key时删除的就是别人的锁），可以用uuid来校验。
 
  2）更好的方法是使用redisson分布式锁，引入依赖后，再@Bean注入这个类，使用redisson.getLock()和lock.tryLock()、lock.unlock()来获得锁、加锁和释放锁，用来解决超卖问题

（8）使用RabbitMQ里的topic模式对秒杀请求削峰，通过消息队列完成异步请求

（9）隐藏秒杀接口地址：用户点击购买时分两次请求，前端第一次请求时，后端根据用户id和uuid生成一个随机的path，并存到redis里再返回；前后第二次带着这个path再请求，跟redis信息匹配成功再开启秒杀服务，避免秒杀接口地址直接暴露

（10）使用Captcha验证码，在redis里存储记录验证码的答案

（11）对秒杀接口限流，为每个用户设置一个秒杀计数器，5秒内最多发送5次请求，存放在redis里，5秒后失效

（12）使用用户工具类：循环生成若干用户，插入到数据库中，然后模拟这些用户登录，将用户手机号码、密码和登录信息（uuid）都写入一个txt文件里，让jmeter(csv数据文件设置，HttpCookie管理器)读取并测试

