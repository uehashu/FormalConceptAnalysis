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
    TreeSet<Tupple> deduplicatedTupples; // オブジェクト部分集合の重複を省いたデータ
    HashSet<Integer> universeOfObject; // オブジェクトの全集合
    HashSet<Integer> universeOfAttribute; // 属性の全集合


    //////////////////
    // Constructors //
    //////////////////

    /**
     * @param data                対象データ
     * @param universeOfObject    オブジェクトの全集合
     * @param universeOfAttribute 属性の全集合
     */
    public FormalConceptSparseAnalyzer(TreeSet<Tupple> data,
                                       HashSet<Integer> universeOfObject,
                                       HashSet<Integer> universeOfAttribute){
        this.data = data;
        this.deduplicatedTupples = new TreeSet<>();
        this.universeOfObject = universeOfObject;
        this.universeOfAttribute = universeOfAttribute;
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

        /*
        for(Tupple povTupple : data){
            System.out.print("{");
            System.out.print(povTupple.getObjectSubset().toString());
            System.out.print("},{");
            System.out.print(povTupple.getAttributeSubset().toString());
            System.out.println("}");
        }
        */

        // まず, 与えられたデータのオブジェクト部分集合に重複が存在しないか，
        //即ち同じオブジェクト部分集合を持つ属性が存在しないかを確認する.
        // 重複する属性は統合する.
        //HashSet<Tupple> deduplicatedTupples = new HashSet<>();
        for(Tupple povTupple : data){
            boolean dup = false;
            for(Tupple compTupple : deduplicatedTupples){
                if(povTupple.getObjectSubset().equals(compTupple.getObjectSubset())){
                    compTupple.addElementsToAttribute(povTupple.getAttributeSubset());
                    dup = true;
                    break;
                }
            }
            if(!dup){
                deduplicatedTupples.add(povTupple.deepCopy());
            }
        }

        /*
        System.out.println("dedup.");
        for(Tupple povTupple : deduplicatedTupples){
            System.out.print("{");
            System.out.print(povTupple.getObjectSubset().toString());
            System.out.print("},{");
            System.out.print(povTupple.getAttributeSubset().toString());
            System.out.println("}");
        }
        */      

        
        // ここからが実際の解析だよー.

        
        for(Tupple povTupple : deduplicatedTupples){

            // 一時的に結果を格納するヤツ.
            HashSet<Tupple> tempTupples = new HashSet<>();

            // まずは, 注目タプルが形式概念であるかどうかを判定する.
            // 具体的には, オブジェクト部分集合とその双極集合を比較し,
            // 同一であれば形式概念と判定される.
            if(povTupple.getObjectSubset().equals
               (getAttributePolarSet(getObjectPolarSet(povTupple.getObjectSubset())))){
                tempTupples.add(new Tupple(povTupple.deepCopy().getObjectSubset(),
                                           getObjectPolarSet(povTupple.getObjectSubset())));
            }

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

        // 概念束の上限の形式概念が持つオブジェクト集合が, オブジェクト全集合と
        // 一致しないならば, オブジェクト全集合かつ属性が空集合であるような形式概念を
        // 概念束に付与する.
        TreeSet<Tupple> treed = new TreeSet<>(tupples);
        if(!treed.first().getObjectSubset().equals(universeOfObject)){
            Tupple top = new Tupple();
            top.addElementsToObject(universeOfObject);
            tupples.add(top);
        }

        // 同様に, 下限の形式概念が持つ属性集合が, 属性全集合と
        // 一致しないならば, オブジェクトが空集合かつ属性が全集合であるような形式概念を
        // 概念束に付与する.
        if(!treed.last().getAttributeSubset().equals(universeOfAttribute)){
            Tupple bottom = new Tupple();
            bottom.addElementsToAttribute(universeOfAttribute);
            tupples.add(bottom);
        }
        

        // オブジェクト部分集合の要素数でソートしてから返す.
        return new TreeSet<Tupple>(tupples);
    }


    /**
     * 形式概念解析を行うメソッド.
     * @param data                対象データ
     * @param universeOfObject    オブジェクトの全集合
     * @param universeOfAttribute 属性の全集合
     * @return                    解析結果
     */
    public static TreeSet<Tupple> analize(TreeSet<Tupple> data,
                                          HashSet<Integer> universeOfObject,
                                          HashSet<Integer> universeOfAttribute){
        FormalConceptSparseAnalyzer fcsa =
            new FormalConceptSparseAnalyzer(data,universeOfObject,universeOfAttribute);
        return fcsa.analize();
    }


    /**
     * 形式概念解析を行うメソッド.
     * @param data                対象データ
     * @param universeOfObject    オブジェクトの全集合
     * @param universeOfAttribute 属性の全集合
     * @return                    解析結果
     */
    public static ArrayList<Tupple> analize(ArrayList<Tupple> data,
                                            HashSet<Integer> universeOfObject,
                                            HashSet<Integer> universeOfAttribute){
        return new ArrayList<Tupple>(FormalConceptSparseAnalyzer.analize
                                     (new TreeSet<Tupple>(data),
                                      universeOfObject,universeOfAttribute));
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
        for(Tupple tupple : deduplicatedTupples){
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
        for(Tupple tupple : deduplicatedTupples){
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
        HashSet<Integer> universeOfObject = new HashSet<>(); // オブジェクトの全集合
        HashSet<Integer> universeOfAttribute = new HashSet<>(); // 属性の全集合

        boolean[][] contextTable =
        {{t,t,t,f},
        {f,t,t,t},
        {f,t,t,t},
        {t,f,f,t}};
        /*
        boolean[][] contextTable = new boolean[50][30];
        for(int i=0;i<contextTable.length;i++){
            for(int j=0;j<contextTable[0].length;j++){
                if(Math.random() > 0.5){
                    contextTable[i][j] = true;
                }else{
                    contextTable[i][j] = false;
                }
            }
        }
        */

        // オブジェクトの全集合を準備.
        for(int i=1;i<=contextTable.length;i++){
            universeOfObject.add(i);
        }

        // 属性の全集合を準備.
        for(int i=1;i<=contextTable[0].length;i++){
            universeOfAttribute.add(i);
        }

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
        ArrayList<Tupple> concepts =
            FormalConceptSparseAnalyzer.analize(testData,
                                                universeOfObject,
                                                universeOfAttribute);

        // 結果の表示.
        System.out.println("{attributes}  :  {objects}");
        for(Tupple tupple : concepts){
            String attributes = new String();
            String objects = new String();

            if(tupple.getAttributeSubset().size() > 0){
                for(Integer attribute : tupple.getAttributeSubset()){
                    attributes += attribute.intValue();
                    attributes += ",";
                }
                attributes = attributes.substring(0,attributes.length()-1);
            }else{
                attributes = "Empty";
            }

            if(tupple.getObjectSubset().size() > 0){
                for(Integer obj : tupple.getObjectSubset()){
                    objects += obj.intValue();
                    objects += ",";
                }
                objects = objects.substring(0,objects.length()-1);
            }else{
                objects = "Empty";
            }

            System.out.println("{" + attributes + "}  :  {" + objects + "}");

        }
    }
}
