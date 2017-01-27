import java.util.HashSet;
import java.util.TreeSet;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.HashMap;
import java.util.SortedSet;
import java.util.NavigableSet;
import java.util.Collections;
import java.util.Comparator;

/**
 * 形式概念解析で得られたタプルに対するハッセ図を作るクラス.
 * この場合, 包含関係が順序構造である.
 *
 * @author  uehashu
 * @version 1.0
 */
public class FCAHasseDiagram{

    HashSet<Node> nodes = new HashSet<>(); // ノードの集合.
    HashMap<Node,HashSet<Node>> arrows = new HashMap<>(); // 有向エッジ. <to,froms>

    //////////////////
    // Constructors //
    //////////////////

    /**
     * 有向エッジのコレクションも初期化しておく.
     * ついでにハッセ図の構造も算出しとく.
     * @param tupples 形式概念解析で得られたタプル
     */
    public FCAHasseDiagram(ArrayList<Tupple> tupples){
        for(Tupple tupple : tupples){
            nodes.add(new Node(tupple));
        }
        for(Node node : nodes){
            arrows.put(node,new HashSet<Node>());
        }

        calcHasseDiagram();
    }



    /////////////
    // Methods //
    /////////////

    /**
     * ハッセ構造(ノードとエッジ)を計算するメソッド.
     */
    private void calcHasseDiagram(){

        // ノードの集合を,  オブジェクト部分集合の要素数の降順でソートするためのコンパレータ.
        Comparator<Node> comp = new Comparator<Node>(){
                // @override
                public int compare(Node n1, Node n2){
                    if(n1.getTupple().getObjectSubsetCardinality() >
                       n2.getTupple().getObjectSubsetCardinality()){
                        return -1;
                    }else if(n1.getTupple().getObjectSubsetCardinality() <
                             n2.getTupple().getObjectSubsetCardinality()){
                        return 1;
                    }else{
                        return 0;
                    }
                }
            };

        // ノードの集合を, オブジェクト部分集合の要素数の降順でソートする.
        ArrayList<Node> sortedNodes = new ArrayList<>(nodes);
        Collections.sort(sortedNodes,comp);

        // 上の階層(オブジェクト部分集合の要素数が多い順)から見ていく.
        for(Node povNode : sortedNodes){
            // 注目ノードよりオブジェクト要素数が多いノードを,
            // オブジェクト部分集合の要素数の昇順で取り出す.
            // この取り出したノードが注目ノードの親集合の候補になる.
            LinkedList<Node> candidateSupsetNodes = new LinkedList<>();
            for(Node candidateNode : sortedNodes){
                if(candidateNode.getTupple().getObjectSubsetCardinality() >
                   povNode.getTupple().getObjectSubsetCardinality()){
                    candidateSupsetNodes.addFirst(candidateNode);
                }else{
                    // 降順に並んでいるので, 取り出したノードのオブジェクト部分集合の数が
                    // 注目ノードのオブジェクト部分集合の数以下になったとき, ループを抜ける.
                    break;
                }
            }

            // 候補ノードでループ.
            for(Node candidateNode : candidateSupsetNodes){
                // 注目ノードが候補ノードの部分集合であるならば以下.
                if(candidateNode.getTupple().getObjectSubset().containsAll
                   (povNode.getTupple().getObjectSubset())){
                    // 注目ノードの親集合コレクションが空である, もしくは
                    // 取り出したノードが注目ノードの親集合に入っていないならば,
                    // 有向エッジを引き, 取り出したノードおよびその親集合を
                    // 注目ノードの親集合に加える.
                    if(povNode.getSuperclasses().isEmpty() ||
                       !povNode.getSuperclasses().contains(candidateNode)){
                        arrows.get(povNode).add(candidateNode);
                        povNode.getSuperclasses().add(candidateNode);
                        povNode.getSuperclasses().addAll(candidateNode.getSuperclasses());
                    }
                }
            }
        }
    }


    /**
     * ノードの集合を返すメソッド.
     * @return ノードの集合
     */
    public HashSet<Node> getNodes(){
        return nodes;
    }


    /**
     * 有向エッジの集合を返すメソッド.
     * @return 有向エッジの集合
     */
    public HashMap<Node,HashSet<Node>> getArrows(){
        return arrows;
    }



    //////////
    // Test //
    //////////

    public static void main(String args[]){

        boolean t = true;
        boolean f = false;

        HashSet<Integer> universeOfObject = new HashSet<>(); // オブジェクトの全集合
        HashSet<Integer> universeOfAttribute = new HashSet<>(); // 属性の全集合

        // オブジェクトの全集合を準備.
        for(int i=1;i<=5;i++){
            universeOfObject.add(i);
        }

        // 属性の全集合を準備.
        for(int i=1;i<=4;i++){
            universeOfAttribute.add(i);
        }

        boolean[][] contextTable =
            {{f,t,t,t},
             {t,f,t,t},
             {t,f,t,f},
             {t,f,t,f},
             {t,f,f,f}};

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

        // ハッセ図を計算.
        FCAHasseDiagram fcahd = new FCAHasseDiagram(concepts);

        // ノードの集合と有向エッジの集合を取得.
        HashSet<Node> nodes = fcahd.getNodes();
        HashMap<Node,HashSet<Node>> arrows = fcahd.getArrows();

        for(Node to : arrows.keySet()){
            HashSet<Node> froms = arrows.get(to);
            for(Node from : froms){
                System.out.print("{");
                for(Integer n : from.getTupple().getObjectSubset()){
                    System.out.print(n);
                    System.out.print(",");
                }
                System.out.print("} -> {");
                for(Integer n : to.getTupple().getObjectSubset()){
                    System.out.print(n);
                    System.out.print(",");
                }
                System.out.println("}");
            }
        }
    }
}
