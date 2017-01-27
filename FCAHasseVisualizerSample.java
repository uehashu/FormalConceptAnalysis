import java.util.HashSet;
import java.util.HashMap;
import java.util.ArrayList;
import edu.uci.ics.jung.graph.DirectedSparseGraph;
import edu.uci.ics.jung.graph.util.EdgeType;
import edu.uci.ics.jung.algorithms.layout.StaticLayout;
import edu.uci.ics.jung.visualization.BasicVisualizationServer;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;
import java.awt.Dimension;
import javax.swing.JFrame;

public class FCAHasseVisualizerSample{


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

        // グラフの描画システムを作成
        DirectedSparseGraph<Node,Integer> graph = new DirectedSparseGraph<>();

        // ノードを作成.
        for(Node node : nodes){
            graph.addVertex(node);
        }

        // エッジを作成.
        int edgeIndex = 0;
        for(Node to : arrows.keySet()){
            HashSet<Node> froms = arrows.get(to);
            for(Node from : froms){
                graph.addEdge(edgeIndex, from, to, EdgeType.DIRECTED);
                edgeIndex++;
            }
        }

        // 手動レイアウト.
        StaticLayout<Node,Integer> layout = new StaticLayout<>(graph);

        // パネルのサイズ.
        Dimension dim = new Dimension(600,600);

        // ノードを綺麗に配置するための仕組み.
        // まず, ノードのオブジェクト部分集合の要素数の最大値を取得する.
        int maxObjectSize = 0;
        for(Node node : nodes){
            if(node.getTupple().getObjectSubset().size() > maxObjectSize){
                maxObjectSize = node.getTupple().getObjectSubset().size();
            }
        }

        // グラフをオブジェクト部分集合の要素数ごとに仕分ける.
        ArrayList<ArrayList<Node>> dividedNodes = new ArrayList<>();
        for(int i=maxObjectSize;i>=0;i--){
            ArrayList<Node> sameSizeNodes = new ArrayList<>();
            for(Node node : nodes){
                if(node.getTupple().getObjectSubset().size() == i){
                    sameSizeNodes.add(node);
                }
            }
            if(!sameSizeNodes.isEmpty()){
                dividedNodes.add(sameSizeNodes);
            }
        }

        int bandY = (int)(dim.getHeight() / (dividedNodes.size()*2));
        for(int i=0;i<dividedNodes.size();i++){
            ArrayList<Node> sameSizeNodes = dividedNodes.get(i);
            int bandX = (int)(dim.getWidth() / (sameSizeNodes.size()*2));
            for(int j=0;j<sameSizeNodes.size();j++){
                Node node = sameSizeNodes.get(j);
                layout.setLocation(node, bandX*(2*j+1), bandY*(2*i+1));
            }
        }

        // グラフを貼るパネル.
        BasicVisualizationServer<Node,Integer> panel =
            new BasicVisualizationServer<>(layout, dim);

        // Node の toString() をノード名として表示させる.
        panel.getRenderContext().setVertexLabelTransformer(new ToStringLabeller<Node>());

        JFrame frame = new JFrame("Hasse Graph");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(panel);
        frame.pack();
        frame.setVisible(true);
    }
}
