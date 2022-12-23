package com.example.tdtuapp.posts;

public class Posts {
   /*
   * id -s
   * content -s
   * create_date -n
   * field -s
   * from -n
   * to -n
   * like
   * linkImg -s
   * owner -s từ owner load ra avatar và name trong adapter
    */
   private String owner, linkImg, field, content, id;
   private Long create_date, from, to;

    public Posts(String owner, String id, String linkImg, String content, String field, Long create_date, Long from, Long to) {
        this.owner = owner;
        this.linkImg = linkImg;
        this.field = field;
        this.content = content;
        this.id = id;
        this.create_date = create_date;
        this.from = from;
        this.to = to;
    }


    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getLinkImg() {
        return linkImg;
    }

    public void setLinkImg(String linkImg) {
        this.linkImg = linkImg;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Long getCreate_date() {
        return create_date;
    }

    public void setCreate_date(Long create_date) {
        this.create_date = create_date;
    }

    public Long getFrom() {
        return from;
    }

    public void setFrom(Long from) {
        this.from = from;
    }

    public Long getTo() {
        return to;
    }

    public void setTo(Long to) {
        this.to = to;
    }
}
