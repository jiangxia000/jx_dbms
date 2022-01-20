#项目介绍
- 本项目为《高级数据库技术》课程作业
- 采用javacc实现词法、语法分析，java实现一些具体操作
- 引入jxl.jar包进行excel相关操作

ps:
1、运行前请安装好javacc编译插件（https://blog.csdn.net/qq_29232943/article/details/62439283） 
2、修改./src/operate/SQLConstant.java中的 path = "E:\\eclipse_workingspace\\jx_dbms\\save"为自己本地文件夹绝对路径即可正常运行。

## 1.设计
### 数据库设计
./sava(文件夹) 
----dbname1（文件夹）
----------tablename1.xls(文件）
----------tablename2.xls(文件)
----------....
----dbname2
----dbname3
----....
### 数据表设计
- 每一个表对应的excel文件包含两个sheet(define和data)
- define的每一列从上至下依次存放 colname、vachar/int、定义时vachar限制的字符个数、列完整性约束（not null、unique、primary key）
- data的第一行存放列名，下面每一行对应一个元组


## 2.结构
./sava 产生的数据文件存放位置
./src
----rule
----------MyNewGrammar.jj javacc词法、语法
----operate
----------SQLConstant.java 路径相关的操作 
----------database.java 数据库相关指令的一些实现函数
----------table.java 表相关指令的一些实现函数
----------record.java 元组相关指令的一些实现函数
----------untils.java 一些工具函数（显示、查询等）
----------Tab.java 用来美化输出显示的工具函数
----------....
 
## 3.支持的功能
### 数据库操作：
    1. //创建数据库
       create database dbname;   
       eg: create database db1;

    2. //显示当前目录下的所有数据库  
      show databases; 

    3. //删除当前目录下的某个数据库          
       drop database dbname;     
       drop database db2; 

    4. //切换当前路径，进入数据库进行表操作
       use dbname;               
       eg: use db1;

### 表操作
    1. //新建表，类型支持vachar/int，列完整性约束支持primary key/unique/not null
	    CREATE TABLE Student_3
	    (
	    Sno vachar(8) primary key,
	    Sname vachar(10) unique,
	    Ssex vachar(8),
	    Sage int not null
	    );  

    2. //删除表（定义和数据全部删除）
       drop table tablename; 
       eg: drop table Student1;

    3. //列出当前数据库中存在的所有表
       show tables;　 

    4. //显示出表的定义(sheet：define）
       desc table_name;
       eg: desc Student1;
### 元组操作
    // 判断条件只支持等值判断
	// javacc支持的插入字符串为“字母开头，后接数字/字母/下划线”，不能为空

    1. insert //增
    	eg: 支持：
    	INSERT INTO Student VALUES ('S21W001','Alice','female',18);
    	INSERT INTO Student (Sno,Sname,Ssex,Sage) VALUES ('S21W002','Bob','male',18);
    	//测试not null
    	INSERT INTO Student (Sno,Sname,Ssex) VALUES ('S21W003','Jeffery','female');
    	//测试primary key
    	INSERT INTO Student (Sno,Sname,Ssex,Sage) VALUES ('S21W001','Dora','female',20);
    	//测试unique
    	INSERT INTO Student VALUES ('S21W005','Alice','female',20);

    2. delete //删
        eg:支持：
    	DELETE FROM Student WHERE Sno='S21W005';
		DELETE FROM Student;

    3. select //查
       eg:支持：
		SELECT Sno,Sname FROM Student;
		SELECT * FROM Student;
		SELECT Sno,Sname FROM Student WHERE Sno='S21W001';
		SELECT * FROM Student WHERE Sage=18;

	4. update  //改
	   eg:支持：
		UPDATE Student SET Sage=19,Ssex='male' WHERE Sno='S21W001';
		UPDATE Student1 SET Sage=23;
        // 测试primary key约束,Sno是primary key
		UPDATE Student SET Sno='S21W002',Sage=18 WHERE Sname='Alice';
		// 测试Unique约束,Sname是Unique
		UPDATE Student SET Sage=55,Sname='Alice' WHERE Sno='S21W001';
### 结束程序：
    	exit/quit


