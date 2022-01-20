package operate;

public class SQLConstant {
	//本项目创建的所有文件存放的位置
    private static final String path = "E:\\eclipse_workingspace\\jx_dbms\\save";

    //当前数据库的路径 = 根路径path + 数据库名称
    private static String nowPath = path;

    
    //返回当前路径，默认是在sava下面，除非setNowPath改变了路径
    public static String getNowPath(){
    	System.out.println(nowPath);
        return nowPath;
    }
 
    //设置当前路径,当前数据库的路径 = 根路径path + 数据库名称
    public static void setNowPath(String name){
        nowPath = nowPath + "\\" + name;
        System.out.println(nowPath);
    }

}
