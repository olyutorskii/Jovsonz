/*
 * JSON utilities
 *
 * License : The MIT License
 * Copyright(c) 2009 olyutorskii
 */

package jp.sourceforge.jovsonz;

import java.io.IOException;
import java.io.Reader;

/**
 * JSON各種共通ユーティリティ。
 */
public final class Json {

    /** MIME タイプ。 */
    public static final String MIME_TYPE = "application/json";


    /**
     * 隠しコンストラクタ。
     */
    private Json(){
        assert false;
        throw new AssertionError();
    }

    /**
     * JSON最上位構造から文字出力を開始する。
     *
     * @param appout 出力先
     * @param topValue OBJECT型かARRAY型のValue
     * @throws NullPointerException 引数がnull
     * @throws JsVisitException 何らかの理由で処理中断
     * @throws IOException 出力エラー
     */
    public static void dumpJson(Appendable appout, JsComposition<?> topValue)
            throws NullPointerException,
                   JsVisitException,
                   IOException {
        if(appout == null || topValue == null){
            throw new NullPointerException();
        }

        JsonAppender appender = new JsonAppender(appout);

        try{
            topValue.traverse(appender);
        }catch(JsVisitException e){
            assert appender.hasIOException();
            throw appender.getIOException();
        }

        return;
    }

    /**
     * JSONの各種Valueを文字ソースから読み取る。
     *
     * @param source 文字入力
     * @return 各種Value。
     *     0個以上連続するホワイトスペースと共にソースの終わりに達したときはnull
     * @throws IOException 入力エラー
     * @throws JsParseException パースエラー
     */
    static JsValue parseValue(JsonSource source)
            throws IOException, JsParseException{
        source.skipWhiteSpace();
        if( ! source.hasMore() ) return null;

        JsValue result;
        result = JsObject.parseObject(source);
        if(result == null){
            result = JsArray.parseArray(source);
        }
        if(result == null){
            result = JsString.parseString(source);
        }
        if(result == null){
            result = JsNull.parseNull(source);
        }
        if(result == null){
            result = JsBoolean.parseBoolean(source);
        }
        if(result == null){
            result = JsNumber.parseNumber(source);
        }

        if(result == null){
            throw new JsParseException(JsParseException.ERRMSG_INVALIDTOKEN,
                                       source.getLineNumber() );
        }

        return result;
    }

    /**
     * JSONの最上位構造を文字ソースから読み取る。
     *
     * @param source 文字入力ソース
     * @return JSON最上位構造。OBJECT型かARRAY型のいずれか。
     *     入力が0個以上のホワイトスペースのみで埋められていた場合はnull。
     * @throws IOException 入力エラー
     * @throws JsParseException パースエラー
     */
    private static JsComposition<?> parseJson(JsonSource source)
            throws IOException, JsParseException{
        JsValue topValue = parseValue(source);
        if(topValue == null) return null;

        if( ! (topValue instanceof JsComposition) ){
            throw new JsParseException(JsParseException.ERRMSG_INVALIDROOT,
                                       source.getLineNumber() );
        }
        JsComposition<?> result = (JsComposition) topValue;

        return result;
    }

    /**
     * JSONの最上位構造を文字リーダから読み取る。
     *
     * @param source 文字入力リーダ
     * @return JSON最上位構造。OBJECT型かARRAY型のいずれか。
     *     入力が0個以上のホワイトスペースのみで埋められていた場合はnull。
     * @throws IOException 入力エラー
     * @throws JsParseException パースエラー
     */
    public static JsComposition<?> parseJson(Reader source)
            throws IOException, JsParseException{
        JsonSource jsonSource = new JsonSource(source);
        return parseJson(jsonSource);
    }

}
