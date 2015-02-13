import java.util.TreeSet;
import java.util.HashSet;
import java.util.ArrayList;

/**
 * 形式概念解析を実装したクラス. 
 * スパースなリストを使って解析を行うので, かなりの数の属性とオブジェクトに
 * 対応できるんじゃないかなぁ(願望).
 * ただし, リストを全探索する必要があるので, 演算速度は遅いはず.
 *
 * @author  uehashu
 * @version 1.0
 *
 * 2013.12.31 コメントの詳細化.
 */
public class FormalConceptSparseAnalyzer{
    
    TreeSet<Tupple> data; // 対象データ
    
    
    //////////////////
    // Constructors //
    //////////////////
    
    /**
     * @param 対象データ
     */
    public FormalConceptSparseAnalyzer(TreeSet<Tupple> data){
	this.data = data;
    }
    
    
    
    /////////////
    // Methods //
    /////////////
    
    /**
     * 形式概念解析を行うメソッド.
     * @return 解析結果
     */
    public TreeSet<Tupple> analize(){
	
	// 結果を格納するヤツ.
	HashSet<Tupple> tupples =  new HashSet<>();
	
	for(Tupple povTupple : data){
	    
	    // 一時的に結果を格納するヤツ.
	    HashSet<Tupple> tempTupples = new HashSet<>();
	    
	    // まずは注目タプルの極作用素を行う.
	    tempTupples.add(new Tupple(povTupple.deepCopy().getObjectSubset(),
				       getObjectPolarSet(povTupple.getObjectSubset())));
	    
	    // 次に, 今までの結果との共通部分なオブジェクト部分集合に対する極作用素も行う.
	    // 共通部分が空な場合は何もしない.
	    for(Tupple pastTupple : tupples){
		HashSet<Integer> intersection = intersectObject(povTupple,pastTupple);
		if(!intersection.isEmpty()){
		    tempTupples.add(new Tupple(intersection,
					       getObjectPolarSet(intersection)));
		}
	    }
	    
	    // 結果に格納.
	    tupples.addAll(tempTupples);
	}
	
	// オブジェクト部分集合の要素数でソートしてから返す.
	return new TreeSet<Tupple>(tupples);
    }
    
    
    /**
     * 形式概念解析を行うメソッド.
     * @param data 対象データ
     * @return     解析結果
     */
    public static TreeSet<Tupple> analize(TreeSet<Tupple> data){
	FormalConceptSparseAnalyzer fcsa = 
	    new FormalConceptSparseAnalyzer(data);
	return fcsa.analize();
    }
    
    
    /**
     * 形式概念解析を行うメソッド.
     * @param data 対象データ
     * @return     解析結果
     */
    public static ArrayList<Tupple> analize(ArrayList<Tupple> data){
	return new ArrayList<Tupple>(FormalConceptSparseAnalyzer.analize
				     (new TreeSet<Tupple>(data)));
    }
    
    
    /**
     * オブジェクト部分集合の共通部分を返すメソッド.
     * @param a,b タプル
     * @return    a と b のオブジェクト部分集合の共通部分を格納したコレクション
     */
    private HashSet<Integer> intersectObject(Tupple a, Tupple b){
	HashSet<Integer> aCloneObject = a.deepCopy().getObjectSubset();
	aCloneObject.retainAll(b.getObjectSubset());
	return aCloneObject;
    }
    
    
    /**
     * オブジェクトに対し極作用素を適用させるメソッド.
     * @param objectSubset 極作用素の対象とするオブジェクト部分集合
     * @return             極作用結果. 属性部分集合.
     */
    private HashSet<Integer> getObjectPolarSet(HashSet<Integer> objectSubset){
	HashSet<Integer> polar = new HashSet<>();
	for(Tupple tupple : data){
	    if(tupple.getObjectSubset().containsAll(objectSubset)){
		polar.addAll(tupple.deepCopy().getAttributeSubset());
	    }
	}
	return polar;
    }
    
    
    /**
     * 属性に対し極作用素を適用させるメソッド.
     * @param attributeSubset 極作用素の対象となる属性部分集合
     * @return                極作用結果. オブジェクト部分集合.
     */
    private HashSet<Integer> getAttributePolarSet(HashSet<Integer> attributeSubset){
	HashSet<Integer> polar = new HashSet<>();
	for(Tupple tupple : data){
	    if(tupple.getAttributeSubset().containsAll(attributeSubset)){
		polar.addAll(tupple.deepCopy().getObjectSubset());
	    }
	}
	return polar;
    }
    
    
    
    //////////
    // Test //
    //////////
    
    public static void main(String args[]){
	
	boolean t = true;
	boolean f = false;
	
	boolean[][] contextTable = 
	    {{f,t,t,t},
	     {t,f,t,t},
	     {t,f,t,f},
	     {t,f,t,f},
	     {t,f,f,f}};
	
	/*
	boolean[][] contextTable = 
	    {{f,t,t,t,t,f},
	     {t,f,t,t,t,t},
	     {t,f,t,f,t,t},
	     {t,f,t,f,f,t},
	     {t,f,f,f,f,f}};
	*/
	
	// 対象を格納するリスト.
	ArrayList<Tupple> testData = new ArrayList<>();
	
	// リストに対象を格納していく.
	for(int i=0;i<contextTable[0].length;i++){
	    Tupple tupple = new Tupple(i+1);
	    for(int j=0;j<contextTable.length;j++){
		if(contextTable[j][i]){
		    tupple.getObjectSubset().add(new Integer(j+1));
		}
	    }
	    testData.add(tupple);
	}
	
	// 解析.
	ArrayList<Tupple> concepts = FormalConceptSparseAnalyzer.analize(testData);
	
	// 結果の表示.
	System.out.println("{attributes}  :  {objects}");
	for(Tupple tupple : concepts){
	    String attributes = new String();
	    String objects = new String();
	    for(Integer attribute : tupple.getAttributeSubset()){
		attributes += attribute.intValue();
		attributes += ",";
	    }
	    attributes = attributes.substring(0,attributes.length()-1);
	    
	    for(Integer obj : tupple.getObjectSubset()){
		objects += obj.intValue();
		objects += ",";
	    }
	    objects = objects.substring(0,objects.length()-1);
	    
	    System.out.println("{" + attributes + "}  :  {" + objects + "}");
	    
	}
    }
}
