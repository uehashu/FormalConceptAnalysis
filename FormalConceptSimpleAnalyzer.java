import java.util.Arrays;
import java.util.ArrayList;

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
     * コンテクスト表からオブジェクト集合の極集合を取得するメソッド.<br>
     * オブジェクト番号とは, オブジェクトの集合を2進数表現したものである.<br>
     * 属性番号も同様.<br>
     * java の特性上, この方法ではオブジェクト数も属性数も31を超えてはいけない.
     *
     * @param contextTable コンテクスト表. [オブジェクト]x[属性]
     * @return             各オブジェクト番号に対応した属性番号
     */
    public static int[] getObjectPolarSet(boolean[][] contextTable){
	
	int objectLength = contextTable.length;
	int attributeLength = contextTable[0].length;
	
	// オブジェクト集合の全ての組み合わせの数だけ配列を作る.
	int[] objectPolarSet = new int[1 << objectLength];
	Arrays.fill(objectPolarSet,0);
	
	// 全ての組み合わせを全探索.
	boolean[] bools; // オブジェクト番号から取得されたブール配列.
	int numOfPovObject; // 注目するオブジェクトの数.
	int count; // 適当なカウンタ.
	boolean[][] povObject; // 注目すべきオブジェクト.
	boolean[] intersection; //属性の共通部分.
	for(int i=0;i<objectPolarSet.length;i++){
	    // オブジェクト番号からブール配列を取得.
	    // true の付いたオブジェクトの共通部分を見るのである.
	    bools = getBoolsFromBits(i,objectLength);
	    numOfPovObject = getNumOfTrue(bools);
	    povObject = new boolean[numOfPovObject][attributeLength];
	    count = 0;
	    // 注目すべきブール配列群を作成. 注目オブジェクトが0個の時は,
	    // 共通部分は全て真で定義する.
	    if(numOfPovObject==0){
		intersection = new boolean[attributeLength];
		Arrays.fill(intersection,true);
	    }else{
		for(int j=0;j<objectLength;j++){
		    if(bools[j]){
			povObject[count] = contextTable[j];
			count++;
			if(count>=numOfPovObject){
			    break;
			}
		    }
		}
		// 共通部分を取得.
		intersection = intersectAttributes(povObject);
	    }
	    
	    // 共通部分から属性番号を取得し, 格納.
	    objectPolarSet[i] = getBitFromBools(intersection);
	}
	
	return objectPolarSet;
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
     * int の2進数表記からブール配列を返すメソッド.
     * @param bits   int の2進数表記. 非負整数であることを仮定する.
     * @param length 配列の長さ
     * @return       ブール配列
     */
    private static boolean[] getBoolsFromBits(int bits, int length){
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
	
	int[] objectPolarSet = getObjectPolarSet(contextTable);
	
	for(int i=0;i<objectPolarSet.length;i++){
	    for(int j=0;j<5;j++){
		int num = (i >>> j) & 1;
		System.out.print(num);
	    }
	    System.out.print(":");
	    for(int j=0;j<4;j++){
		int num = (objectPolarSet[i] >>> j) & 1;
		System.out.print(num);
	    }
	    System.out.println();
	}
    }
}
