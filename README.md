Formal Concept Analysis(FCA)
============================

What?
-----
形式概念解析を行なうプログラム群.


### FormalConceptSimpleAnalyzer.java ###
シンプルにテーブルを配列で実装したプログラム.
配列の数は最大で2^31個までなので, `(オブジェクト数+属性数)<31` までしか対応できない.
しかし, オブジェクトの部分集合や属性の部分集合から配列番号が一意に求まるため,
計算時間はとても速い. 


### FormalConceptSparseAnalyzer.java ###
コレクションリストを用いたスパースなテーブルを実装することにより,
上記のシンプルな実装に比べて対応できる個数を増加させたプログラム.
対応できる個数はテーブルによるが, 少なくとも上記のプログラムと比較すると多い.
ただし, 極集合を計算するときに全ての部分集合のリストを舐める必要があるため,
計算時間で劣る.


### Tupple.java ###
オブジェクト部分集合と属性部分集合の組を表すオブジェクトクラス.


### Node.java ###
ハッセ図のノードを表すオブジェクトクラス.


### HasseDiagram.java ###
ハッセ図の構造を算出するプログラム.


### HasseVisualizerSample.java ###
ハッセ図を描画するサンプルコード.



## License ##
Copyright (c) 2013 uehashu(uehashu@gmail.com)
Released under the MIT license
http://opensource.org/licenses/mit-license.php
