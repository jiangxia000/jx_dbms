package operate;


// 这个类里面存放了一个string的字符串，还有这个字符串控制台输入的时候的数据类型
// 因为如果开始jj存储的时候就还要标识哪个数num，哪个是string，然后写入数据库的时候就方便转型了
public class SupportedValue {
    private DataType dataType;
    private String value;
    
    
    public DataType getDataType() {
        return dataType;
    }

    public String getValue() {
        return value;
    }
    
    //构造函数
    public SupportedValue(DataType dataType, String value) {
        this.dataType = dataType;
        this.value = value;
    }

//    @Override
//    public String toString() {
//        return "ComparableValue{" +
//                "dataType=" + dataType +
//                ", value='" + value + '\'' +
//                '}';
//    }
//    

}


