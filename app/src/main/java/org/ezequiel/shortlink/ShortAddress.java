package org.ezequiel.shortlink;

public class ShortAddress {
    private Long id;
    private String code;
    private String date;
    private String longUrl;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getOriginal_link() {
        return longUrl;
    }

    public void setOriginal_link(String longUrl) {
        this.longUrl = longUrl;
    }


}
