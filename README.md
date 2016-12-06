Formal Concept Analysis(FCA)
============================

<a href="https://travis-ci.org/uehashu/FormalConceptAnalysis">
	<img src="https://travis-ci.org/uehashu/FormalConceptAnalysis.svg?branch=master" alt="Build Status">
</a>

What?
-----
形式概念解析を行なうプログラム群.
描画ライブラリとして[jung2](http://jung.sourceforge.net)を使っている.



内容物
-----
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


How to Use?
-----------
ant 使うとラクです.
```
$ ant clean
$ ant compile
$ ant run
```


License
-------
Copyright (c) 2013, uehashu(uehashu@gmail.com)
All rights reserved.

Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.

2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.

3. Neither the name of the copyright holder nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
