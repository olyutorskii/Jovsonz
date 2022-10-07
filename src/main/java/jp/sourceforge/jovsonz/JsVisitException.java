/*
 * JSON traverse error exception
 *
 * License : The MIT License
 * Copyright(c) 2009 olyutorskii
 */

package jp.sourceforge.jovsonz;

/**
 * トラバース中断例外。
 *
 * <p>JSONツリー構造のトラバース処理の中断時に投げられる。
 *
 * <p>トラバース処理内部でIOExceptionなどのチェック例外が発生した場合、
 * チェーン例外機構({@link java.lang.Throwable#getCause()} etc.)
 * を用いるのが望ましい。
 */
@SuppressWarnings("serial")
public class JsVisitException extends Exception {

    /**
     * コンストラクタ。
     */
    public JsVisitException(){
        super();
        return;
    }

    /**
     * コンストラクタ。
     *
     * @param message 詳細メッセージ。不明な場合はnull
     */
    public JsVisitException(String message){
        super(message);
        return;
    }

    /**
     * コンストラクタ。
     *
     * @param message 詳細メッセージ。不明な場合はnull
     * @param cause 原因となった例外。不明な場合はnull
     */
    public JsVisitException(String message, Throwable cause){
        super(message, cause);
        return;
    }

    /**
     * コンストラクタ。
     *
     * @param cause 原因となった例外。不明な場合はnull
     */
    public JsVisitException(Throwable cause){
        super(cause);
        return;
    }

}
