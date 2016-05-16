# easyF
com.yangc.platform
一个初级的权限框架

与com.yangc.frame如出一辙，应广大要求换成了springmvc。
看到大家有问com.yangc.utils.jar在哪里，其实都在这里，做程序猿，多点点，反正也不花钱，而且你还会有更多的发现。 (๑¯∀¯๑)
若发现bug，请联系QQ：511636835，感激不尽。
使用jdk6，maven项目
后台 hibernate3.6.10 + spring3.2.8 + springmvc3.2.8 + shiro1.2.3
前端 extjs4.2 + jquery1.8
目前支持的是oracle和mysql数据库，可以使用不同的数据库，不同的数据库脚本放入不同的文件夹中

登录账号（用户名 密码）
yangchen 1
wangsheng 123456

shiro

使用shiro实现登录认证和权限控制，将shiro默认的ehcache缓存改为使用redis缓存，通过配置文件，配置redis的集群策略。

springmvc

通过注解的方式实现请求数据的传递，拦截器的实现，全局异常的实现，rest风格。

hibernate

hibernate中主要使用的是HibernateTemplate和JdbcTemplate，封装了两个dao（BaseDao和JdbcDao）提供使用，BaseDao主要用来增、删、改、批量、和单表查询，JdbcDao主要负责多表联合的复杂查询，主要为了保证性能。并且sql语句使用xml作为载体写在代码外面，根据不同数据库写不同的sql语句，如：项目模块名-oracle-sql.xml、项目模块名-mysql-sql.xml

Tips：

1.通过过滤器获取页面分页的pageNow、pageSize放到ThreadLocal，无需每回在action（controller、resource..
  汗，叫的名称真是多种多样啊）的方法中写入这两个参数，直接通过ThreadLocal获取。
2.通过hibernate的拦截器去保存公共信息，如：createTime、updateTime。
3.重新授予权限后记得清空过期的权限缓存。（毕竟菜单、权限等不经常修改的信息适合放入缓存）
4.想到了再写...
