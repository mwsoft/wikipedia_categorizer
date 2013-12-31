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

実行すると、以下の3つの情報が表示されます。

	query : 指定された文書から生成されたクエリ

	wikipedia categories : クエリから取得した、Wikipedia側が持つカテゴリ

	my categories : Wikipediaのカテゴリから変換した独自カテゴリ

## カテゴリの拡張 ##

Wikipediaの持つカテゴリと、こちらが付加したいカテゴリをマッピングする形で結果を算出しています。下記ファイルを編集することで、カテゴリのマッピングを変更することができます。

	data/category_map.txt

上記ファイルは編集している途中に力尽きたので、中途半端な内容になっています。【wikipedia categories】では正しい結果が推測されているのに、【my categories】には出ない場合は、category_map.txtに記述がないせいです。記述を追加して再度 run すると、反映された結果が表示されます。

## 既知の問題点 ##

一部のサイトでは文字化けが発生する為、カテゴリの推測に失敗します。JSoupのconnectメソッド任せなせいです。UTF-8形式のファイルとして読み込ませるか、MltSearcherクラスにjava.io.Readerを渡せるので、ちゃんとした文字コード判定をしてReaderを渡す処理を書けば改善します。

## ライセンス ##

	MIT Licens

