package com.example.stepcounter.model;

public class Sources {

    private String title;
    private String slug;
    private String url;
    private long crawl_rate;

    public Sources() {

    }

    public Sources(String title, String slug, String url, long crawl_rate) {
        this.title = title;
        this.slug = slug;
        this.url = url;
        this.crawl_rate = crawl_rate;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public long getCrawl_rate() {
        return crawl_rate;
    }

    public void setCrawl_rate(long crawl_rate) {
        this.crawl_rate = crawl_rate;
    }
}
