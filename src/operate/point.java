package operate;

// 这个是为了search函数返回对应的位置
public class point {
    private int row;
    private int clo;
    
    
    public int get_row() {
        return row;
    }

    public int get_clo() {
        return clo;
    }
    
    //构造函数
    public point(int row, int clo) {
        this.row = row;
        this.clo = clo;
    }

}
