wikipedia_categorizer
=====================

## 必要環境 ##

JDK6以上がインストールされていること。

メモリ2GB以上のマシン推奨。

## 事前準備 ##

下記ページから　jawiki-latest-pages-articles.xml.bz2 を落としてきて、解凍せずにそのままdataフォルダ配下に置く。

	http://dumps.wikimedia.org/jawiki/latest/

IndexCreaterを呼び出して、Luceneのインデックスを生成する。この処理には数時間かかります。うちの数年前に買ったPCで90分くらい。実行中は標準出力にカウンタが出ます。108万くらいで止まると思います。

	./activator 'runMain jp.mwsoft.wikipedia.categorizer.IndexCreater'

## 実行方法 ##

インデックスの生成が終わったら、利用可能になります。下記コマンドで実行できます。引数にはファイル名かURLを指定します。

	./activator 'run http://www.mwsoft.jp/'

    ./activator 'run data/example_file.txt'

## ライセンス ##

MIT Licens

