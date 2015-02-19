import java.util.HashSet;
import java.util.TreeSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.SortedSet;
import java.util.NavigableSet;

/**
 * 形式概念解析で得られたタプルに対するハッセ図を作るクラス.
 * この場合, 包含関係が順序構造である.
 *
 * @author  uehashu
 * @version 1.0
 */
public class FCAHasseDiagram{
    
    TreeSet<Node> nodes = new TreeSet<>(); // ノードの集合.
    HashMap<Node,HashSet<Node>> arrows = new HashMap<>(); // 有向エッジ. <to,froms>
    
    //////////////////
    // Constructors //
    //////////////////

    /**
     * コンストラクタを呼んだ段階で, すでに各ノードはオブジェクト部分集合の要素数により
     * 降順でソートされている. (Setのくせに...)
     * また, 有向エッジのコレクションも初期化しておく.
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
	
	// 上の階層(オブジェクト部分集合の要素数が多い順)から見ていく.
	for(Node povNode : nodes){
	    // 注目ノードより上位のノードを、昇順で取り出す.
	    TreeSet<Node> headSet = new TreeSet<>(nodes.headSet(povNode));
	    for(Node supNode : headSet.descendingSet()){
		// 注目ノードが取り出したノードの部分集合であるならば以下.
		if(supNode.getTupple().getObjectSubset().containsAll
		   (povNode.getTupple().getObjectSubset())){
		    // 注目ノードの親集合コレクションが空である, もしくは
		    // 取り出したノードが注目ノードの親集合に入っていないならば, 
		    // 有向エッジを引き, 取り出したノードおよびその親集合を
		    // 注目ノードの親集合に加える.
		    if(povNode.getSuperclasses().isEmpty() ||
		       !povNode.getSuperclasses().contains(supNode)){
			arrows.get(povNode).add(supNode);
			povNode.getSuperclasses().add(supNode);
			povNode.getSuperclasses().addAll(supNode.getSuperclasses());
		    }
		}
	    }
	}
    }
    

    /**
     * ノードの集合を返すメソッド.
     * @return ノードの集合
     */
    public TreeSet<Node> getNodes(){
	return nodes;
    }

    
    /**
     * 有向エッジの集合を返すメソッド.
     * @return 有向エッジの集合
     */
    public HashMap<Node,HashSet<Node>> getArrows(){
	return arrows;
    }
    
    
    
    //////////////////
    // InnerClasses //
    //////////////////

    /**
     * 形式概念解析で得られたタプルに対するハッセ図のノードを表すクラス.
     * オブジェクト部分集合の要素数により降順で並ぶように順序付けをしている.
     */
    public class Node implements Comparable<Node>{

	Tupple tupple; // このノードが持つタプル.
	int layer; // このノードの階層番号. タプルが持つオブジェクト集合の要素数に等しい.
	HashSet<Node> superclasses; // このノードの親集合の集合.
	
	Node(Tupple tupple){
	    this.tupple = tupple;
	    layer = tupple.getObjectSubsetCardinality();
	    superclasses = new HashSet<>();
	}
	
	/**
	 * このノードが持つタプルを返すメソッド.
	 * @return タプル
	 */
	Tupple getTupple(){
	    return tupple;
	}
	
	/**
	 * 階層番号を返すメソッド.
	 * @return 階層番号
	 */
	int getLayer(){
	    return layer;
	}

	/**
	 * このノードの親集合の集合を返すメソッド.
	 * @return 親集合の集合
	 */
	HashSet<Node> getSuperclasses(){
	    return superclasses;
	}
	
	/**
	 * 階層順にノードを並べるためのメソッド.
	 * このメソッドがノード間における順序構造を表すわけではないことに注意する.
	 * 通常, 等しい場合は0を返すが, ここで0を返すと同一のノードとみなされ
	 * 重複削除されてしまうため, 0を返さない仕組みにしていることに気をつける.
	 * @param o 比較されるノード
	 * @return  要素数が比較対象より少ない場合は1, そうでない場合は-1を返す.
	 */
	// @override
	public int compareTo(Node o){
	    if(getLayer() < o.getLayer()){
		return 1;
	    }else{
		return -1;
	    }
	}

	
	/**
	 * テキトーに文字列として出力するメソッド.
	 */
	// @override
	public String toString(){
	    String str = new String();
	    str += "Tupple: ";
	    System.out.print("{");
	    for(Integer e : tupple.getObjectSubset()){
		System.out.print(e + ",");
	    }
	    System.out.println("}");
	    return str;
	}
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
	TreeSet<Node> nodes = fcahd.getNodes();
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
