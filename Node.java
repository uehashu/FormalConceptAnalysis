import java.util.HashSet;

/**
 * 形式概念解析で得られたタプルに対するハッセ図のノードを表すクラス.
 * オブジェクト部分集合の要素数により降順で並ぶように順序付けをしている.
 */
public class Node{

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
     * @return    要素数が比較対象より少ない場合は1, そうでない場合は-1を返す.
     */
    /*
        public int compareTo(Node o){
        if(getLayer() < o.getLayer()){
        return 1;
        }else{
        return -1;
        }
        }
    */


    /**
     * テキトーに文字列として出力するメソッド.
     */
    // @override
    public String toString(){
        String str = new String();
        str += tupple.getObjectSubset().toString();
        str += ":";
        str += tupple.getAttributeSubset().toString();
        str += "\n";
        return str;
    }
}
