package operate;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import jxl.Sheet;
import jxl.Workbook;

public class utils {
	
	
    // 得到当前目录下所有的文件夹
    public static List<String> getAllDirs(String path){
        List<String> list = new ArrayList<>();
        File file = new File(path);
        File[] fileList = file.listFiles();
        for(int i = 0; i < fileList.length; i++){
            if(fileList[i].isDirectory()){
                list.add(fileList[i].getName());
            }
        }
        return list;
    }
    //得到当前目录下所有的excel文件夹
    public static List<String> getAllFiles(String nowPath){
        List<String> list = new ArrayList<>();
        File file = new File(nowPath);
        File[] fileList = file.listFiles();
        if(fileList != null){
            for(int i = 0; i < fileList.length; i++) {
            	String name = fileList[i].getName();
            	//是文件且为.xls文件
                if ((fileList[i].isFile()) && (name.endsWith("xls")) ) {
                    // 获取点前面的
                    int index = name.lastIndexOf(".");
                    String tableName = name.substring(0, index);
                    list.add(tableName);
                }
            }
        }
        return list;
    }
    
	
    public static boolean confirm(){
        System.out.println("确认删除: Yes or No ");
        System.out.print("请输入: ");
        Scanner scanner = new Scanner(System.in);
        //nextLine() 读取字符串的时候不以中间的空格为字符串结束判断
        //trim忽略输入字符串两边的空格
        String input = scanner.nextLine().trim().toLowerCase();
//        System.out.print(input);
        if("yes".equals(input)){
            return true;
        }
        else if("no".equals(input)){
            return false;
        }
        else{ //不删除
            System.out.println("请按规则输入！");
            return false;
        }
    }
    
    //  要删除的数据库/数据
    public static boolean deleteFile(File dirFile) {
        // 如果dir对应的文件/文件夹不存在，则退出
        if (!dirFile.exists()) {
            return false;
        }
        //文件就直接删除
        if (dirFile.isFile()) {
            return dirFile.delete();
        } // 文件夹就递归删除下面的文件/文件夹，然后再删除文件夹自己（在本项目中，只会有数据库时文件夹，不会很深）
        else {
            for (File file : dirFile.listFiles()) {
                deleteFile(file);
            }
            return dirFile.delete();
        } 
    }
    
    //传入表文件路径(这里传入的表一定存在)，返回表描述二维列表，
    // 这里的list是excel的一列，也就是到时候显示出来的一行
    public static List<List<String>> describle(File file) { 
    	// 要返回的表内容,
        List<List<String>> lists = new ArrayList<>(); 
		try {
	    	// 打开文件  
	        Workbook book = Workbook.getWorkbook(file); 
	       // 获取sheet0(define) 
	        Sheet sheet = book.getSheet("define");
	        
	        int rows = sheet.getRows();
	        int cols = sheet.getColumns();
//	        System.out.println(cols);
	        String temp = null;
	        
	     // 四行：sname, char ,10, not null
	     // 注意tab()函数传进去的那个二维list是每一个list是到时候显示输出的一行
	        int i = 0; 
	        while(i < cols) {
	        	//遍历excel表每一列的时候都要重新定义一次
	        	List<String> row = new ArrayList<>(); 
	        	for(int j=0; j<rows; j++) {
	        		temp = sheet.getCell(i, j).getContents(); //第j行第i列 ，前面是列
//	        		System.out.println(temp);
	        		row.add(temp);
	        	}
	        	lists.add(row);
	        	i++;
	        }
	        
		}catch (Exception e) {  
	        System.out.println(e);  
	    }
		return lists;  
    }
    
    //传入文件，cloname,op,SupportedValue，返回 ArrayList<point>找到对应满足条件单元格的位置（可以有多个所以用ArrayList）
    //返回值：target_location.size()==0 没有查到符合条件的元组；
    //target_location.size()==1&&第一个元素为（-1，-1）表示该属性列不在表中
    public static ArrayList<point> search(File file, String cloname,String op, String clo_value ) { 
    	// 要返回的内容
    	ArrayList<point> target_location = new ArrayList<>(); 
		try {
	    	// 打开文件  
	        Workbook book = Workbook.getWorkbook(file); 
	       // 获取sheet0(define) 
	        Sheet sheet = book.getSheet("data");
	        
	        int rows = sheet.getRows();
	        int cols = sheet.getColumns();
	        //得到表的所有的属性名
 	        ArrayList<String> t_clo_name = new ArrayList<>();
	        for(int c=0; c<cols; c++) 
	        	t_clo_name.add(sheet.getCell(c, 0).getContents()); //前面是列
	        //如果列名属于table,记录下对应的列号current_clo_index
	        if(t_clo_name.contains(cloname)) {
	        	int current_clo_index = t_clo_name.indexOf(cloname);
    			//因为excle表中的数据都是合格的，并且填进去的时候都是以String填进去的，
    			//所以直接拿到clo_value的String值,然后和表中的数据比较就行
        		for(int dr=1; dr < rows; dr++) {
        			if(op.equals("=")) {
        				if ((sheet.getCell(current_clo_index, dr).getContents()).equals(clo_value)) {
        					target_location.add(new point(dr,current_clo_index));
    		        		System.out.println("第"+ dr+"行存在"+cloname + op + clo_value + "的元组"); 
        			    }        			
        			}		        			
        		}		   	        	
	        }else {
	        	target_location.add(new point(-1,-1)); //（-1,-1）表示该属性列不在表中
	        }        
		}catch (Exception e) {  
	        System.out.println(e);  
	    }
		return target_location;  
    }
    
    //文件、要显示的列，要显示的元组包含的单元格坐标（如果target_location为空则表示全部元组要显示）
    public static void show_data(File file, ArrayList<String> list_clo_name, ArrayList<point> target_location) { 
  
        //输出的元组内容
        List<List<String>> lists = new ArrayList<>();
        //输出的表头
        List<String> list4 = new ArrayList<>();
        for (String i : list_clo_name) 
        	list4.add(i);
        
		try {
	    	// 打开文件  
	        Workbook book = Workbook.getWorkbook(file); 
	       // 获取sheet1(data) 
	        Sheet sheet = book.getSheet("data");
	        
	        int rows = sheet.getRows();
	        int cols = sheet.getColumns();
//	        System.out.println(cols);
	        String temp = null;
  
	     // 注意tab()函数传进去的那个二维list是每一个list是到时候显示输出的一行
	        if(target_location.size()==0) { //表示全部元组要显示
		        int i = 1; 
		        while(i < rows) {
		        	//遍历excel表每一列的时候都要重新定义一次
		        	List<String> row = new ArrayList<>(); 
		        	for(int j=0; j<cols; j++) {
		        		if(list_clo_name.contains(sheet.getCell(j, 0).getContents())) {
		        			temp = sheet.getCell(j, i).getContents(); //第i行第j列 ，前面是列
//			        		System.out.println(temp);
			        		row.add(temp);
		        		}	
		        	}
		        	lists.add(row);
		        	i++;
		        }	        	
	        }else {
	        	int index = 0; //这里表示取得是target_location中第index个point(元组)
	        	rows = target_location.size();
	        	int r = 0 ;//对应excel中的行号
		        while(index < rows) {
		        	r = target_location.get(index).get_row();
		        	//遍历excel表每一列的时候都要重新定义一次
		        	List<String> row = new ArrayList<>(); 
		        	for(int j=0; j<cols; j++) {
		        		if(list_clo_name.contains(sheet.getCell(j, 0).getContents())) {
		        			temp = sheet.getCell(j, r).getContents(); //第r行第j列 ，前面是列
//			        		System.out.println(temp);
			        		row.add(temp);
		        		}	
		        	}
		        	lists.add(row);
		        	index++;
		        }	        	
	        }
	        
		}catch (Exception e) {  
	        System.out.println(e);  
	    }
		System.out.println(Tab.tab(list4, lists));
    }

}
