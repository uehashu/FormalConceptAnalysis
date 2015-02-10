import java.util.Arrays;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

/**
 * 形式概念解析を実装したクラス. 
 * 単純に全組み合わせのテーブルを作ってから解析しているので, 
 * オブジェクトも属性も共に31個までしか使えない. 
 * これは, 配列の最大数が int つまり 2^31 個であるためである.
 *
 * @author  uehashu
 * @version 1.0
 */
public class FormalConceptSimpleAnalyzer{
    
    //////////////////
    // Constructors //
    //////////////////
    
    
    
    /////////////
    // Methods //
    /////////////
    
    /**
     * オブジェクト極集合に対応した属性部分集合番号を返すメソッド.
     * 
     * @param objectSet    オブジェクトの部分集合に対応したオブジェクト部分集合番号
     * @param contextTable コンテクスト表. [オブジェクト]x[属性]
     * @return             オブジェクト極集合に対応した属性部分集合番号
     */
    public static int getObjectPolarSet(int objectSet, boolean[][] contextTable){
	
	// まずは, オブジェクト部分集合の要素数を取り出す.
	int elementSize = Integer.bitCount(objectSet);

	// もし要素数が0ならば, 全ての要素を持つ属性集合番号を返す.
	if(elementSize == 0){
	    return (1 << (contextTable[0].length+1)) - 1;
	}
	
	// オブジェクト部分集合に含まれる要素全てのコンテクスト表を取り出す.
	boolean[][] contextSubTable = new boolean[elementSize][contextTable[0].length];
	int objectCounter=0;
	for(int i=0;i<contextTable.length;i++){
	    if(((objectSet>>>i) & 1) == 1){
		for(int j=0;j<contextTable[0].length;j++){
		    contextSubTable[objectCounter][j] = contextTable[i][j];
		}
		objectCounter++;
	    }
	}

	// 上記のコンテクスト表の共通部分を取得.
	boolean[] intersection = intersectAttributes(contextSubTable);
	
	// 共通部分から属性部分集合の番号を取得して返す.
	return getBitFromBools(intersection);
	
    }
    
    
    /**
     * 属性極集合に対応したオブジェクト部分集合番号を返すメソッド.
     * 
     * @param attributeSet 属性の部分集合に対応した属性部分集合番号
     * @param contextTable コンテクスト表. [オブジェクト]x[属性]
     * @return             属性極集合に対応したオブジェクト部分集合番号
     */
    public static int getAttributePolarSet(int attributeSet, boolean[][] contextTable){
	
	// まずは, 属性集合の要素数を取り出す.
	int elementSize = Integer.bitCount(attributeSet);

	// もし要素数が0ならば, 全ての要素を持つオブジェクト集合番号を返す.
	if(elementSize == 0){
	    return (1 << (contextTable.length+1)) - 1;
	}
	
	// 属性部分集合に含まれる要素全てのコンテクスト表を取り出す.
	boolean[][] contextSubTable = new boolean[elementSize][contextTable.length];
	int attributeCounter = 0;
	for(int i=0;i<contextTable[0].length;i++){
	    if(((attributeSet>>>i) & 1) == 1){
		for(int j=0;j<contextTable.length;j++){
		    contextSubTable[attributeCounter][j] = contextTable[j][i];
		}
		attributeCounter++;
	    }
	}

	// 上記のコンテクスト表の共通部分を取得.
	boolean[] intersection = intersectAttributes(contextSubTable);

	// 共通部分からオブジェクト部分集合の番号を取得して返す.
	return getBitFromBools(intersection);
    }
    
    
    /**
     * int の2進数表記からブール配列を返すメソッド.
     *
     * @param bits   int の2進数表記. 非負整数であることを仮定する.
     * @param length 配列の長さ
     * @return       ブール配列
     */
    public static boolean[] getBoolsFromBits(int bits, int length){
	boolean[] bools = new boolean[length];
	
	for(int i=0;i<bools.length;i++){
	    // シフトしてマスクしたやつが1じゃなかったら真(?)
	    if(((bits >>> i) & 1) == 1){
		bools[i] = true;
	    }else{
		bools[i] = false;
	    }
	}
	
	return bools;
    }


    /**
     * コンテクスト表から形式概念解析を行なうメソッド.
     *
     * @param contextTable コンテクスト表. [オブジェクト]x[属性]
     * @return             オブジェクト部分集合番号から属性部分集合番号へのマップのリスト
     */
    public static HashMap<Integer,Integer> analize(boolean[][] contextTable){

	// 結果を格納するマップリスト. 初期容量0.
	HashMap<Integer,Integer> formalConcepts = new HashMap<>(0);
	
	// オブジェクトにおける全ての組み合わせを計算する.
	int objectCombinationLength = (1 << contextTable.length);
	
	// オブジェクトの全ての組み合わせに対して, 双極集合を計算する.
	// 双極集合を表す番号が元のオブジェクト部分集合を表す番号と一致している場合,
	// 結果に格納する.
	int objectPolarSet,dualPolarSet;
	for(int i=0;i<objectCombinationLength;i++){
	    objectPolarSet = getObjectPolarSet(i,contextTable);
	    dualPolarSet = getAttributePolarSet(objectPolarSet,contextTable);
	    if(i == dualPolarSet){
		formalConcepts.put(i,objectPolarSet);
	    }
	}

	return formalConcepts;
    }
    
    
    
    /////////////////////
    // Private Methods //
    /////////////////////
    
    /**
     * 属性の共通部分を返すメソッド.
     * @param boolSet ブール集合. [オブジェクト]x[属性]
     * @return        共通部分
     */
    private static boolean[] intersectAttributes(boolean[][] boolSet){
	
	int objectLength = boolSet.length;
	int attributeLength = boolSet[0].length;
	
	boolean[] intersection = new boolean[attributeLength];
	Arrays.fill(intersection,true);
	
	for(int i=0;i<attributeLength;i++){
	    for(int j=0;j<objectLength;j++){
		// 1つでも偽なデータがあれば, 共通部分は偽である.
		if(!boolSet[j][i]){
		    intersection[i] = false;
		    break;
		}
	    }
	}

	return intersection;
    }
    
    
    /**
     * ブール配列を2進数表記とみなし, int を返すメソッド.
     * @param bools ブール配列
     * @return      整数
     */
    private static int getBitFromBools(boolean[] bools){
	
	int bit = 0;
	
	for(int i=0;i<bools.length;i++){
	    if(bools[i]){
		bit += Math.pow(2,i);
	    }
	}
	
	return bit;
    }
    
    
    /**
     * ブール配列内に, 真がいくつあるかを返すメソッド.
     * @param bools ブール配列
     * @return      true の個数
     */
    private static int getNumOfTrue(boolean[] bools){
	int numOfTrue = 0;
	for(int i=0;i<bools.length;i++){
	    if(bools[i]){
		numOfTrue++;
	    }
	}
	
	return numOfTrue;
    }
    
    
    
    //////////
    // Test //
    //////////
    
    public static void main(String args[]){
	
	boolean[][] contextTable = 
	    {{false,true,true,true},
	     {true,false,true,true},
	     {true,false,true,false},
	     {true,false,true,false},
	     {true,false,false,false}};
	
	HashMap<Integer,Integer> formalConcepts = analize(contextTable);

	for(Map.Entry<Integer,Integer> map : formalConcepts.entrySet()){
	    for(int i=0;i<5;i++){
		int num = (map.getKey() >>> i) & 1;
		System.out.print(num);
	    }
	    System.out.print(":");
	    for(int i=0;i<4;i++){
		int num = (map.getValue() >>> i) & 1;
		System.out.print(num);
	    }
	    System.out.println();
	    
	}
    }
}
