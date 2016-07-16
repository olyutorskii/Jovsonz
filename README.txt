[UTF-8 Japanese]

                              J o v s o n z
                                  Readme

                                              Copyright(c) 2009 olyutorskii


=== Jovsonzとは ===

 Jovsonzライブラリは、JSONデータの入出力を行うためのJavaライブラリです。
JovsonzはJindolfプロジェクトから派生したオープンソースプロジェクトです。

※ このアーカイブは、開発者向けにJovsonzのソースコードのみをまとめたものです。


=== 実行環境 ===

 - JovsonzはJava言語(JavaSE7)で記述されたプログラムです。
 - JovsonzはJRE1.7に準拠したJava実行環境で利用できるように作られています。
   原則として、JRE1.7に準拠した実行系であれば、プラットフォームを選びません。


=== 開発プロジェクト運営元 ===

  http://sourceforge.jp/projects/jovsonz/ まで。


=== ディレクトリ内訳構成 ===

基本的にはMaven3のmaven-archetype-quickstart構成に準じます。

./README.txt
    あなたが今見てるこれ。

./CHANGELOG.txt
    変更履歴。

./LICENSE.txt
    ライセンスに関して。

./SCM.txt
    ソースコード管理に関して。

./pom.xml
    Maven3用プロジェクト構成定義ファイル。

./src/main/java/
    Javaのソースコード。

./src/test/java/
    JUnit 4.* 用のユニットテストコード。

./src/main/config/
    各種ビルド・構成管理に必要なファイル群。

./src/main/config/checks.xml
    Checkstyle用configファイル。

./src/main/config/pmdrules.xml
    PMD用ルール定義ファイル。


--- EOF ---
