package com.example.googlereaderec;

public class SearchResult {
	
	private String gSearchResultClass;
	private String unescapedUrl;
	private String url;
	private String visibleUrl;
	private String cacheUrl;
	private String title;
	private String titleNoFormatting;
	private String content;

	public void setGsearchResultClass(String gSearchResultClass) {
	       this.gSearchResultClass = gSearchResultClass;
	}

	public String getGsearchResultClass() {
	       return gSearchResultClass;
	}
	
	public void setUnescapedUrl(String unescapedUrl) {
	       this.unescapedUrl = unescapedUrl;
	}

	public String getUnescapedUrl() {
	       return unescapedUrl;
	}
	
	public void setUrl(String url) {
	       this.url = url;
	}

	public String getUrl() {
	       return url;
	}
	
	public void setVisibleUrl(String visibleUrl) {
	       this.visibleUrl = visibleUrl;
	}

	public String getVisibleUrl() {
	       return visibleUrl;
	}
	
	public void setCacheUrl(String cacheUrl) {
	       this.cacheUrl = cacheUrl;
	}

	public String getCacheUrl() {
	       return cacheUrl;
	}
	
	public void setTitle(String title) {
	       this.title = title;
	}

	public String getTitle() {
	       return title;
	}
	
	public void setTitleNoFormatting(String titleNoFormatting) {
	       this.titleNoFormatting = titleNoFormatting;
	}

	public String getTitleNoFormatting() {
	       return titleNoFormatting;
	} 
	
	public void setContent(String content) {
	       this.content = content;
	}

	public String getContent() {
	       return content;
	}
}
