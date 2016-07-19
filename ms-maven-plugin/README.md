#Ibatis Maven 插件,自动生成DAO和xml文件

###example
mvn com.ms.maven.plugins:ms-maven-plugin:generateDao -Durl=jdbc:mysql://192.168.1.170:3306/mustang -Duser=dev -Dpassword=dev1234 -Dtables=t_user -DserviceName=UserService -DbasePackage=com.ms.biz.user