## 瑞吉外卖功能清单

### 后台系统

#### 员工管理:

- 登录、退出
- 新增
- 修改
- 列表
- 禁用、启用

#### 分类管理:

- 新增
- 修改
- 列表
- 删除

#### 菜品管理:

- 新增
- 修改
- 列表
- 删除
- 停售、启售

#### 套餐管理:

- 新增
- 修改
- 列表
- 删除
- 停售、启售



#### 订单明细

- 列表
- 状态操作

### 移动端应用

- 手机号登录
- 个人中心
- 地址管理
- 历史订单
- 浏览菜品、套餐
- 购物车
- 添加购物车
- 清空购物车
- 下单

数据库表在 resource表下

除项目提供的外，使用到代码mybatis-plus代码生成器

```java
public class getCode {
    @Test
    public void main1() {
// 1、创建代码生成器
        AutoGenerator mpg = new AutoGenerator();
// 2、全局配置
        GlobalConfig gc = new GlobalConfig();
        String projectPath = System.getProperty("user.dir");
        System.out.println(projectPath);
        gc.setOutputDir("E:\\frontcode\\guli\\guli-parent\\service\\service-edu" + "/src/main/java");
        gc.setAuthor("anyi");
        gc.setOpen(false); //生成后是否打开资源管理器
        gc.setFileOverride(false); //重新生成时文件是否覆盖
        /*
         * mp生成service层代码，默认接口名称第一个字母有 I
         * UcenterService
         * */
        gc.setServiceName("%sService"); //去掉Service接口的首字母I
        gc.setIdType(IdType.ID_WORKER_STR); //主键策略
        gc.setDateType(DateType.ONLY_DATE);//定义生成的实体类中日期类型
        gc.setSwagger2(true);//开启Swagger2模式
        mpg.setGlobalConfig(gc);
// 3、数据源配置
        DataSourceConfig dsc = new DataSourceConfig();
        dsc.setUrl("jdbc:mysql://localhost:3307/guli?serverTimezone=GMT%2B8");
        dsc.setDriverName("com.mysql.cj.jdbc.Driver");
        dsc.setUsername("root");
        dsc.setPassword("root");
        dsc.setDbType(DbType.MYSQL);
        mpg.setDataSource(dsc);
// 4、包配置
        PackageConfig pc = new PackageConfig();
        pc.setModuleName("serviceedu"); //模块名
        pc.setParent("com.anyi");
        pc.setController("controller");
        pc.setEntity("entity");
        pc.setService("service");
        pc.setMapper("mapper");
        mpg.setPackageInfo(pc);
// 5、策略配置
        StrategyConfig strategy = new StrategyConfig();
        // 修改此处表名即可
        strategy.setInclude("user");
        strategy.setNaming(NamingStrategy.underline_to_camel);//数据库表映射到实体的
        // 命名策略
        strategy.setTablePrefix(pc.getModuleName() + "_"); //生成实体时去掉表前缀
        strategy.setColumnNaming(NamingStrategy.underline_to_camel);//数据库表字段
        //映射到实体的命名策略
        strategy.setEntityLombokModel(true); // lombok 模型 @Accessors(chain =true) setter链式操作
        strategy.setRestControllerStyle(true); //restful api风格控制器
        strategy.setControllerMappingHyphenStyle(true); //url中驼峰转连字符
        mpg.setStrategy(strategy);
// 6、执行
        mpg.execute();
    }
}

```

https://redis.io/docs/getting-started/installation/install-redis-on-mac-os/
# redis自启动
brew services start redis
# 安装界面程序 
https://docs.getmedis.com/
brew install medis


这个repo是别人的,但是功能比较完备,所以我决定在这个基础上学习添加注释,并尝试完善
http://localhost:8080/backend/page/login/login.html
http://localhost:8080/backend/index.html

http://localhost:8080/front/index.html

# about this repo/project
I learned something about SpringBoot from IT-Heima in BiliBili and find an excellent project called Reggie take out.
The tutorial video is very long although detailed, and it does not provide any documents or slide sheets, even a full
GitHub repo. So I searched on GitHub, and luckily I found this repo, this repo is not perfect either such as lack of
logout feature and poor instructions. The swagger ui does not work either, and it is hard to fix this which makes me 
very frustrated.

Good news is that I found another project which is upgrade from Reggie take out:
https://www.bilibili.com/video/BV1TP411v7v6/?p=1&vd_source=2cdc3de199e29dc4f7a75c883bb0d11d
