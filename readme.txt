重构代码
在com.webserver.http包中定义一个类:HttpServlet
这个类将作为所有Servlet的超类使用.
然后在这个类中定义一个抽象方法:service,这可以要求
所有的Servlet都必须拥有这个方法类处理业务.
然后再定义一个方法forward,用来然所有字类重用响应
具体页面的操作.

将现有的注册和登录的Servlet继承HttpServlet,并修改
响应页面的操作作为forward.

之前的版本存在一个问题,每当添加一个新的业务,我们都
要修改ClientHandler,在的二步处理请求的操作中添加
一个分支,判断请求然后实例化对应Servlet.
因此我们将该操作改掉,任何请求对应任何业务ClientHandler
都无需修改

1:在conf目录下新建一个配置文件servlets.xml
	并在其中定义每个业务类与请求的对应关系
	
2:在com.webservler.core包下新建一个类:ServerContext
	在其中定义一个Map类型的常量,用于保存请求与对应的
	业务处理类的关系.
	然后定义初始化方法,在初始化中读取servlets.xml文件
	利用反射实例化请求对应的Servlet.
	
3:在ServerContext中定义一个静态方法getServlet
	用于根据请求获取到对应的处理类,某个Servlet实例
	
4:修改ClientHandler,将原有的判断若干不同业务的分支
	删除,在判断请求是否为请求业务时,先根据请求通过
	ServerContext尝试获取Servlet实例,若获取到了
	则说明请求是对应业务,从而调用器service方法即可.
