package com.tb.hc.common.page;

/**
 * 分页数据
 *
 * @author bin.tong
 * @since 2021/3/15 10:01
 **/
public class CustomPage {

  // 分页数据列表
  private Object tableData;
  // 总记录数
  private long recordCount;
  // 总页数据
  private long totalPages;

  public Object getTableData() {
    return tableData;
  }

  public CustomPage setTableData(Object tableData) {
    this.tableData = tableData;
    return this;
  }

  public long getRecordCount() {
    return recordCount;
  }

  public CustomPage setRecordCount(long recordCount) {
    this.recordCount = recordCount;
    return this;
  }

  public long getTotalPages() {
    return totalPages;
  }

  public CustomPage setTotalPages(long totalPages) {
    this.totalPages = totalPages;
    return this;
  }
}
