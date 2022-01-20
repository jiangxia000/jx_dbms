package operate;
import java.io.*;
import java.util.ArrayList;
import java.util.List;


public class database {
    
	// 创建数据库（文件夹）
	public static void create_database(String dbname){
		System.out.println("语句识别成功");
		//当前数据库的路径 = 根路径path + 数据库名称
	    File file = new File(SQLConstant.getNowPath() + "\\" + dbname);
	    // 如果根目录下没有这个名字的文件，说明该数据库不存在，可以创建
	    if(!file.exists()){
	        file.mkdir();
	        System.out.println(dbname + " database is created");
	    }
	    else{
	        System.out.println("ERROR: database exists");
	    }
	}
    
    // 显示所有数据库（文件夹）
    public static void show_databases(){
    	System.out.println("语句识别成功");
   
    	String path = SQLConstant.getNowPath();
        List<String> dbList = utils.getAllDirs(path);
        List<String> db = new ArrayList<>();
        db.add("Database");
        List<List<String>> list = new ArrayList<>();
        for(int i = 0; i < dbList.size(); i++){
            List<String> ls = new ArrayList<>();
            ls.add(dbList.get(i));
            list.add(ls);
        }
        System.out.println(Tab.tab(db, list));
    }
    
    // 删除一个已有数据库
    public static void drop_database(String dbname){
        //获得当前路径
        String path = SQLConstant.getNowPath();
        //判断是否有该数据库
        List<String> dbList = utils.getAllDirs(path);
        // 判断要删除的数据库是否存在
        boolean a = dbList.contains(dbname);
        if(a) {
        	// 确定是否删除
            boolean b = utils.confirm();
            if (b) {
                String nowPath = path + "\\" + dbname;
                utils.deleteFile(new File(nowPath)); 
                System.out.println("database "+ dbname + " is droped successfully");
            }
            else{
                System.out.println("撤回成功,数据库未被删除");
            }
        }
        else{
            System.out.println("ERROR: 该数据库不存在");
        }
    }
    
    // 使用一个已有数据库
    public static void use_database(String dbname){
    	//获得当前路径
        String path = SQLConstant.getNowPath();

        List<String> dbList = utils.getAllDirs(path);
        //判断使用的数据库是否存在
        boolean b = dbList.contains(dbname);
        
        if(b){
        	//注意一旦使用use dbname之后，所访问的路径就要发生改变了
            SQLConstant.setNowPath(dbname);
            System.out.println("Database changed");
        }
        else{
            System.out.println("ERROR: 数据库不存在");
        }
    }
  
}
