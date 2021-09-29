package org.ezequiel.shortlink;

public class ShortLink {
    private  boolean isOk = false;
    private  String errorMensagem = "";
    private  String error_code = "-1";
    private  String code = "KCveN";
    private  String original_link = "http://example.org/very/long/link.html";

    public ShortLink(boolean isOk,String code, String original_link){
        this.isOk = isOk;
        this.code = code;
        this.original_link = original_link;
    }

    public ShortLink() {

    }


    public boolean isOk() {
        return isOk;
    }

    public String getError_code() {
        return error_code;
    }
    public void setError_code(String error_code) {
        this.error_code = error_code;

        switch (Integer.parseInt(error_code)){

            case 1:
                setErrorMensagem(ErrorCode.errorCode_1);
                break;
            case 2:
                setErrorMensagem(ErrorCode.errorCode_2);
                break;
            case 3:
                setErrorMensagem(ErrorCode.errorCode_3);
                break;
            case 4:
                setErrorMensagem(ErrorCode.errorCode_4);
                break;
            case 5:
                setErrorMensagem(ErrorCode.errorCode_5);
                break;
            case 6:
                setErrorMensagem(ErrorCode.errorCode_6);
                break;
            case 7:
                setErrorMensagem(ErrorCode.errorCode_7);
                break;
            case 8:
                setErrorMensagem(ErrorCode.errorCode_8);
                break;
            case 9:
                setErrorMensagem(ErrorCode.errorCode_9);
                break;
            case 10:
                setErrorMensagem(ErrorCode.errorCode_10);
                break;

            default:
                setErrorMensagem(ErrorCode.errorCode+'='+error_code);
                break;



        }


    }
    public void setOk(boolean ok) {
        isOk = ok;
    }
    public String getCode() {
        return code;
    }
    public void setCode(String code) {
        this.code = code;
    }
    public String getOriginal_link() {
        return original_link;
    }
    public void setOriginal_link(String original_link) {
        this.original_link = original_link;
    }

    public String getErrorMensagem() {
        return errorMensagem;
    }

    public void setErrorMensagem(String errorMensagem) {
        this.errorMensagem = errorMensagem;
    }

    @Override
    public String toString() {
        return "Shortlink{" +
                "isOk=" + isOk +
                ", error_code='" + error_code + '\'' +
                ", code='" + code + '\'' +
                ", original_link='" + original_link + '\'' +
                '}';
    }

}

