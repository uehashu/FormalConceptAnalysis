import java.util.HashSet;

/**
 * オブジェクト部分集合と属性部分集合を組で格納するクラス.
 * 演算するとき, オブジェクト集合の数が多い方から比較していくと早いはずなので, 
 * そういうふうにソートするように Comparator を実装しておく.
 *
 * @author  uehashu
 * @version 1.0
 *
 * 2013.12.31 コメントの追加とリファクタリング.
 */
public class Tupple implements Comparable<Tupple>{
    
    
    HashSet<Integer> objectSubset; // オブジェクト部分集合を格納するコレクション.
    HashSet<Integer> attributeSubset; // 属性部分集合を格納するコレクション.
    
    //////////////////
    // Constructors //
    //////////////////
    
    public Tupple(){
	objectSubset = new HashSet<>();
	attributeSubset = new HashSet<>();
    }
    
    
    /**
     * @param objectSubset    オブジェクト部分集合
     * @param attributeSubset 属性部分集合
     */
    public Tupple(HashSet<Integer> objectSubset, HashSet<Integer> attributeSubset){
	this.objectSubset = objectSubset;
	this.attributeSubset = attributeSubset;
    }
    
    
    /**
     * 1つのオブジェクトに対して属性部分集合を設定するときにこのコンストラクタを用いるとよい. 
     * @param obj             オブジェクト
     * @param attributeSubset 属性部分集合
     */
    public Tupple(Integer obj, HashSet<Integer> attributeSubset){
	this.objectSubset = new HashSet<>();
	objectSubset.add(obj);
	this.attributeSubset = attributeSubset;
    }
    
    
    /**
     * 1つのオブジェクトと1つの属性を設定するときにこのコンストラクタを用いるとよい. 
     * @param obj       オブジェクト
     * @param attribute 属性
     */
    public Tupple(Integer obj, Integer attribute){
	this.objectSubset = new HashSet<>();
	this.attributeSubset = new HashSet<>();
	objectSubset.add(obj);
	attributeSubset.add(attribute);
    }
    
    
    /**
     * 1つのオブジェクトと1つの属性を設定するときにこのコンストラクタを用いるとよい. 
     * @param obj       オブジェクト
     * @param attribute 属性
     */
    public Tupple(int obj, int attribute){
	this((new Integer(obj)),(new Integer(attribute)));
    }
    
    
    /**
     * オブジェクトが空で属性が1つのような組を設定するときにこのコンストラクタを用いるとよい. 
     * @param attribute 属性
     */
    public Tupple(int attribute){
	this();
	attributeSubset.add(new Integer(attribute));
    }
    
    
    
    /////////////
    // Methods //
    /////////////
    
    /**
     * 属性部分集合の要素数を返すメソッド.
     * @return 属性部分集合の要素数
     */
    public int getAttributeSubsetCardinality(){
	return attributeSubset.size();
    }
    
    
    /**
     * オブジェクト部分集合の要素数を返すメソッド.
     * @return オブジェクト部分集合の要素数
     */
    public int getObjectSubsetCardinality(){
	return objectSubset.size();
    }
    
    
    /**
     * 比較用. オブジェクト部分集合の要素数によってソートするときに必要. 特に気にしなくて良い.
     * @param o 比較されるタプル
     * @return  要素数が比較対象より多い時は-1, 少ない時は1, 等しいときは0を返す.
     */
    // @Override
    public int compareTo(Tupple o){
	if(this.equals(o)){
	    return 0;
	}else if(this.getObjectSubsetCardinality() > o.getObjectSubsetCardinality()){
	    return -1;
	}else{
	    return 1;
	}
    }
    
    
    /**
     * オブジェクト部分集合にオブジェクトを追加するメソッド.
     * @param obj 追加するオブジェクト
     */
    public void addElementToObject(Integer obj){
	objectSubset.add(obj);
    }
    
    /**
     * オブジェクト部分集合を返すメソッド.
     * @return オブジェクトが格納されたコレクション
     */
    public HashSet<Integer> getObjectSubset(){
	return objectSubset;
    }
    

    /**
     * 属性部分集合を返すメソッド.
     * @return 属性が格納されたコレクション
     */
    public HashSet<Integer> getAttributeSubset(){
	return attributeSubset;
    }
    
    
    /**
     * 比較用. 重複削除の際に利用される. 特に気にしなくて良い.
     * @param t 比較対象
     * @return  比較対象と等しいとき, 真を返す.
     */
    // @Override
    public boolean equals(Object t){
	if(this.objectSubset.equals(((Tupple)t).getObjectSubset()) &&
	   this.attributeSubset.equals(((Tupple)t).getAttributeSubset())){
	    return true;
	}else{
	    return false;
	}
    }
    
    
    /**
     * 比較用. 重複削除の際に利用される. 特に気にしなくて良い.
     * @return ハッシュ
     */
    // @Override
    public int hashCode(){
	String str = new String();
	for(Integer obj : objectSubset){
	    str += obj.toString();
	}
	for(Integer attribute : attributeSubset){
	    str += attribute.toString();
	}
	return str.hashCode();
    }
    
    
    /**
     * ディープコピーを返すメソッド.
     * @return ディープコピー
     */
    public Tupple deepCopy(){
	HashSet<Integer> objectSubset = new HashSet<>();
	HashSet<Integer> attributeSubset = new HashSet<>();
	
	for(Integer obj : this.getObjectSubset()){
	    objectSubset.add(new Integer(obj.intValue()));
	}
	for(Integer attribute : this.getAttributeSubset()){
	    attributeSubset.add(new Integer(attribute.intValue()));
	}
	return new Tupple(objectSubset,attributeSubset);
    }
    
    
    /**
     * ディープコピーを返すメソッド.
     * @param original コピー対象
     * @return         ディープコピー
     */
    public static Tupple deepCopy(Tupple original){
	return original.deepCopy();
    }
}
