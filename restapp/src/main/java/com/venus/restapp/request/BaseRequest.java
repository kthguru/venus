package com.venus.restapp.request;

import net.sf.oval.constraint.Max;
import net.sf.oval.constraint.Min;

/**
 * This class represents the base request for all requests.
 * The optional parameters for each request will be set in
 * this class. This class should extended by each class which
 * represent a request object
 * 
 * @author sigabort
 *
 */
public class BaseRequest {

  @Min(value = 0, message= "startIndex must be greater than or equal to 0")
  private Integer startIndex = 0;

  @Max(value = 200, message = "itemsPerPage may be no greater than 200")
  @Min(value = 1, message= "itemsPerPage must be not be less than 1")
  private Integer itemsPerPage = 20;

  private String format = "text/html";

  private String sortBy;

  private String filterBy;

  private String filterValue;

  private String filterOp;

  private String sortOrder = "ascending";

  private Boolean onlyActive;
 
  public void setStartIndex(Integer startIndex) {
    this.startIndex = startIndex;
  }
  
  public Integer getStartIndex() {
    return this.startIndex;
  }

  public void setItemsPerPage(Integer itemsPerPage) {
    this.itemsPerPage = itemsPerPage;
  }
  
  public Integer getItemsPerPage() {
    return this.itemsPerPage;
  }

  public void setFormat(String format) {
    this.format = format;
  }
  
  public String getFormat() {
    return this.format;
  }

  public void setSortOrder(String sortOrder) {
    this.sortOrder = sortOrder;
  }
  
  public String getSortOrder() {
    return this.sortOrder;
  }

  public void setSortBy(String sortBy) {
    this.sortBy = sortBy;
  }
  
  public String getSortBy() {
    return this.sortBy;
  }

  public void setFilterBy(String filterBy) {
    this.filterBy = filterBy;
  }
  
  public String getFilterBy() {
    return this.filterBy;
  }

  public void setFilterValue(String filterValue) {
    this.filterValue = filterValue;
  }
  
  public String getFilterValue() {
    return this.filterValue;
  }

  public void setFilterOp(String filterOp) {
    this.filterOp = filterOp;
  }
  
  public String getFilterOp() {
    return this.filterOp;
  }

  public void setOnlyActive(Boolean onlyActive) {
    this.onlyActive = onlyActive;
  }
  
  public Boolean getOnlyActive() {
    return this.onlyActive;
  }

}
