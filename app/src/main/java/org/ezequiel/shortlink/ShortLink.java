package org.ezequiel.shortlink;

public class ShortLink {


    private String id;
    private String date;
    private boolean isOkUrl1 = false;
    private boolean isOkUrl2 = false;
    private String errorMensagem1 = "", errorMensagem2 = "";
    private String error_code1 = "-1";
    private String error_code2 = "-1";
    private String code1 = "error";
    private String code2 = "error";
    private String url = " ";

    public String getUrlApi() {
        return urlApi;
    }

    public void setUrlApi(String urlApi) {
        this.urlApi = urlApi;
    }

    private String urlApi = " ";

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    private String original_link = "http://example.org/very/long/link.html";

    public ShortLink(boolean isOkUrl1, String code1, String original_link) {
        this.isOkUrl1 = isOkUrl1;
        this.code1 = code1;
        this.original_link = original_link;
    }

    public ShortLink() {

    }



    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public boolean getIsOkUrl1() {
        return isOkUrl1;
    }

    public boolean getIsOkUrl2() {
        return isOkUrl2;
    }

    public String getError_code1() {
        return error_code1;
    }

    public String getError_code2() {
        return error_code2;
    }

    public void setError_code1(String error_code) {
        error_code1 = error_code;

        switch (Integer.parseInt(error_code)) {

            case 1:
                setErrorMensagem1(ErrorCode.errorCode_1);
                break;
            case 2:
                setErrorMensagem1(ErrorCode.errorCode_2);
                break;
            case 3:
                setErrorMensagem1(ErrorCode.errorCode_3);
                break;
            case 4:
                setErrorMensagem1(ErrorCode.errorCode_4);
                break;
            case 5:
                setErrorMensagem1(ErrorCode.errorCode_5);
                break;
            case 6:
                setErrorMensagem1(ErrorCode.errorCode_6);
                break;
            case 7:
                setErrorMensagem1(ErrorCode.errorCode_7);
                break;
            case 8:
                setErrorMensagem1(ErrorCode.errorCode_8);
                break;
            case 9:
                setErrorMensagem1(ErrorCode.errorCode_9);
                break;
            case 10:
                setErrorMensagem1(ErrorCode.errorCode_10);
                break;

            default:
                setErrorMensagem1(ErrorCode.errorCode + '=' + error_code);
                break;


        }


    }

    public void setError_code2(String error_code2) {
        this.error_code2 = error_code2;
    }

    public void setIsOkUr1(boolean ok) {
        isOkUrl1 = ok;
    }

    public void setIsOkUr2(boolean ok) {
        isOkUrl2 = ok;
    }

    public String getCode1() {
        return code1;
    }

    public String getCode2() {
        return code2;
    }

    public void setCode1(String code) {
        this.code1 = code;
    }

    public void setCode2(String code) {
        this.code2 = code;
    }

    public String getOriginal_link() {
        return original_link;
    }

    public void setOriginal_link(String original_link) {
        this.original_link = original_link;
    }

    public String getErrorMensagem1() {
        return errorMensagem1;
    }

    public String getErrorMensagem2() {
        return errorMensagem2;
    }

    public void setErrorMensagem1(String errorMensagem1) {
        this.errorMensagem1 = errorMensagem1;
        errorMensagem2 = errorMensagem1;
    }

    public void setErrorMensagem2(String errorMensagem2) {
        this.errorMensagem2 = errorMensagem2;
    }

    @Override
    public String toString() {
        return "Shortlink{" +
                "isOk=" + isOkUrl1 +
                ", error_code='" + error_code1 + '\'' +
                ", code='" + code1 + '\'' +
                ", original_link='" + original_link + '\'' +
                '}';
    }

}

