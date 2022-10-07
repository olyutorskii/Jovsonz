/*
 * JSON parse error information
 *
 * License : The MIT License
 * Copyright(c) 2009 olyutorskii
 */

package jp.sourceforge.jovsonz;

/**
 * 入力文字列パース中断例外。
 *
 * <p>JSON文字列ソースへのパース処理の中断時に投げられる。
 */
@SuppressWarnings("serial")
public class JsParseException extends Exception {

    static final String ERRMSG_INVALIDTOKEN =
            "invalid JSON token";
    static final String ERRMSG_INVALIDROOT =
            "top root JSON value must be OBJECT or ARRAY";
    static final String ERRMSG_NODATA =
            "We need but no more JSON data";

    private static final int LINE_UNKNOWN = 0;

    private final int lineNumber;

    /**
     * コンストラクタ。
     */
    public JsParseException(){
        this(null, LINE_UNKNOWN);
        return;
    }

    /**
     * コンストラクタ。
     *
     * @param message 詳細メッセージ。不明な場合はnull
     * @param lineNumber 行番号。不明な場合は0以下の値
     */
    public JsParseException(String message, int lineNumber){
        this(message, (Throwable) null, lineNumber);
        return;
    }

    /**
     * コンストラクタ。
     *
     * @param message 詳細メッセージ。不明な場合はnull
     * @param cause 原因となった例外。不明な場合はnull
     * @param lineNumber 行番号。不明な場合は0以下の値
     */
    public JsParseException(String message, Throwable cause, int lineNumber){
        super(message, cause);
        this.lineNumber = lineNumber;
        return;
    }

    /**
     * パースエラーの起きた行番号を返す。
     *
     * @return 行番号。不明な場合は0以下の値。
     */
    public int getLineNumber(){
        return this.lineNumber;
    }

    /**
     * 有効な行番号を保持しているか判定する。
     *
     * @return 有効な行番号(1以上)を保持していればtrue
     */
    public boolean hasValidLineNumber(){
        if(this.lineNumber > 0) return true;
        return false;
    }

    /**
     * {@inheritDoc}
     *
     * <p>有効な行番号があれば一緒に出力される。
     *
     * @return {@inheritDoc}
     */
    @Override
    public String getMessage(){
        StringBuilder message = new StringBuilder();

        String superMessage = super.getMessage();
        if(superMessage != null){
            message.append(superMessage);
        }

        if(hasValidLineNumber()){
            if(message.length() > 0) message.append(' ');
            message.append("[line:").append(this.lineNumber).append(']');
        }

        if(message.length() <= 0) return null;
        return message.toString();
    }

}
