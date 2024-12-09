<%--
  Created by IntelliJ IDEA.
  User: 13728
  Date: 2024/12/6
  Time: 14:15
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>选项卡示例</title>
  <style>
    /* 基础元素样式 */
    body {
      font-family: Arial, sans-serif;
      margin: 0;
      padding: 0;
    }

    ul {
      list-style: none;
      padding: 0;
    }

    li {
      margin: 0;
    }

    /* 通用样式 */
    .container {
      max-width: 1200px;
      margin: 0 auto;
      padding: 20px;
      box-sizing: border-box;
    }

    .tab-buttons, .sub-tab-buttons {
      display: flex;
      justify-content: flex-start;
      gap: 10px;
      margin-bottom: 10px;
    }

    .tab-buttons li, .sub-tab {
      display: inline-block;
      padding: 10px;
      cursor: pointer;
      transition: background-color 0.3s ease;
      border-radius: 5px;
    }

    .tab-buttons li.selected, .sub-tab.selected {
      font-weight: bold;
      color: white;
      background-color: #007bff;
    }

    .tab-buttons li {
      background-color: #f1f1f1;
    }

    .sub-tab {
      background-color: #d1d1d1;
    }

    /* 表格样式 */
    table {
      width: 100%;
      border-collapse: collapse;
      margin-bottom: 20px;
    }

    th, td {
      border: 1px solid #ddd;
      padding: 8px;
      text-align: left;
    }

    th {
      background-color: #f8f8f8;
    }

    /* 内容样式 */
    .tab-content, .sub-tab-content {
      display: none;
      padding: 20px;
      background-color: #fff;
      border: 1px solid #ddd;
      border-radius: 5px;
      box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1);
    }

    .tab-content.active, .sub-tab-content.active {
      display: block;
    }

    /* 响应式调整 */
    @media (max-width: 768px) {
      .tab-buttons, .sub-tab-buttons {
        flex-wrap: wrap;
      }

      .tab-buttons li, .sub-tab {
        width: 100%;
        text-align: center;
        margin-bottom: 5px;
      }
    }

  </style>
  <script>
    var username

    function switchTab(tabNumber) {
      // 隐藏所有选项卡内容
      let tabContents = document.querySelectorAll('.tab-content');
      tabContents.forEach(content => content.style.display = 'none');

      // 更新选项卡按钮样式
      let tabs = document.querySelectorAll('.tab');
      tabs.forEach(tab => tab.classList.remove('selected'));

      // 显示当前选中的选项卡内容
      document.getElementById('content' + tabNumber).style.display = 'block';
      document.getElementById('tab' + tabNumber).classList.add('selected');
    }

    function switchSubTab(tabName) {
      // 隐藏所有子选项卡内容
      let subTabContents = document.querySelectorAll('.sub-tab-content');
      subTabContents.forEach(content => content.style.display = 'none');

      // 更新子选项卡按钮样式
      let subTabs = document.querySelectorAll('.sub-tab');
      subTabs.forEach(tab => tab.classList.remove('selected'));

      // 显示当前选中的子选项卡内容
      document.getElementById('sub-content-' + tabName).style.display = 'block';
      document.getElementById('sub-tab-' + tabName).classList.add('selected');
    }

    // 确保DOM完全加载后再运行脚本
    document.addEventListener('DOMContentLoaded', function() {
      // 绑定按钮的点击事件
      document.getElementById('submitButton').addEventListener('click', search);
      document.getElementById('addButton').addEventListener('click', add);
      document.getElementById('deleteButton').addEventListener('click', Delete);
      document.getElementById('alterButton').addEventListener('click', alter);
    })

  // 页面加载时默认显示第一个选项卡，同时初始化其他选项卡
  window.onload = function() {
      // 获取 sessionStorage 中的 'username' 值
      username = sessionStorage.getItem('username');
      // 设置用户名显示在 <span> 标签中
      document.getElementById('username').textContent = username || 'Guest'; // 如果没有 username，则显示 'Guest'

      fetchGoodsList(1);
      fetchSaleList(1);
      fetchCustomerList(1);
      fetchStatsList(1);
      switchTab(1);


    }

    function fetchGoodsList(page) {
      fetch(`/GoodsServlet?page=${page}`)
              .then(response => {
                if (!response.ok) throw new Error("Failed to fetch goods data");
                return response.json();
              })
              .then(data => {
                renderGoodsTable(data.goodsList, '#content1-table');
                renderPagination(data.currentPage, data.totalPages, "pagination1", "fetchGoodsList");
              })
              .catch(error => {
                console.error("Error:", error);
                alert("Failed to load goods data.");
              });
    }

    function fetchSaleList(page) {
      fetch(`/historyServlet?page=${page}`)
              .then(response => {
                if (!response.ok) throw new Error("Failed to fetch sale data");
                return response.json();
              })
              .then(data => {
                renderHistoryTable(data.historyList, '#content2-saletable');
                renderPagination(data.currentPage, data.totalPages, "pagination2.1", "fetchSaleList");
              })
              .catch(error => {
                console.error("Error:", error);
                alert("Failed to load sale data.");
              });
    }

    function fetchCustomerList(page) {
      fetch(`/userServlet`)
              .then(response => {
                if (!response.ok) throw new Error("Failed to fetch user data");
                return response.json();
              })
              .then(data => {
                renderUserTable(data.usersList, '#content3-usertable');
                renderPagination(data.currentPage, data.totalPages, "pagination3.1", "fetchCustomerList");
              })
              .catch(error => {
                console.error("Error:", error);
                alert("Failed to load user data.");
              });
    }

    function fetchStatsList(page) {
      fetch(`/statsServlet`)
              .then(response => {
                if (!response.ok) throw new Error("Failed to fetch stats data");
                return response.json();
              })
              .then(data => {
                renderStatsTable(data.goodsList, '#content2-statstable');
                renderPagination(data.currentPage, data.totalPages, "pagination2.2", "fetchStatsList");
              })
              .catch(error => {
                console.error("Error:", error);
                alert("Failed to load stats data.");
              });
    }

    function renderGoodsTable(goodsList, tablename) {
      const table = document.querySelector(tablename);
      let rows = `
      <tr>
        <th>Goods Name</th>
        <th>Owner</th>
        <th>Price</th>
        <th>Quantity</th>
      </tr>`;
      goodsList.forEach(goods => {
        rows += `
        <tr>
          <td>${goods.goodsName}</td>
          <td>${goods.owner}</td>
          <td>${goods.price}</td>
          <td>${goods.quantity}</td>
        </tr>`;
      });
      table.innerHTML = rows;
    }

    function renderHistoryTable(goodsList, tablename) {
      const table = document.querySelector(tablename);
      let rows = `
    <tr>
      <th>Goods Name</th>
      <th>Time</th>
      <th>Owner</th>
      <th>Price</th>
      <th>Status</th>
    </tr>`;

      goodsList.forEach(goods => {
        rows += `
        <tr>
          <td>${goods.goodsName}</td>
          <td>${goods.time}</td>
          <td>${goods.owner}</td>
          <td>${goods.price}</td>
          <th>${goods.status}</th>
        </tr>`;
      });
      table.innerHTML = rows;
    }

    function renderUserTable(usersList, tablename) {
      const table = document.querySelector(tablename);
      let rows = `
      <tr>
        <th>id</th>
        <th>userName</th>
        <th>email</th>
      </tr>`;
      usersList.forEach(user => {
        rows += `
        <tr>
          <td>${user.id}</td>
          <td>${user.userName}</td>
          <td>${user.email}</td>
        </tr>`;
      });
      table.innerHTML = rows;
    }

    function renderStatsTable(goodsList, tablename) {
      const table = document.querySelector(tablename);
      let rows = `
      <tr>
        <th>goodsName</th>
        <th>totalPrice</th>
      </tr>`;
      goodsList.forEach(Good => {
        rows += `
        <tr>
          <td>${Good.goodsname}</td>
          <td>${Good.totalprice}</td>
        </tr>`;
      });
      table.innerHTML = rows;
    }

    function renderPagination(currentPage, totalPages, paginationNum, fetchFunction) {
      const pagination = document.getElementById(paginationNum);
      const maxVisibleButtons = 5; // 显示的最大分页按钮数量
      let buttons = '';

      // 计算分页按钮的起始和结束
      let startPage = Math.max(1, currentPage - Math.floor(maxVisibleButtons / 2));
      let endPage = Math.min(totalPages, startPage + maxVisibleButtons - 1);

      // 如果分页按钮少于5个，向前调整起始页
      startPage = Math.max(1, endPage - maxVisibleButtons + 1);

      // 上一页按钮
      if (currentPage > 1) {
        buttons += `<button onclick="${fetchFunction}(${currentPage - 1})">&laquo;</button>`;
      }

      // 动态生成中间的分页按钮
      for (let i = startPage; i <= endPage; i++) {
        buttons += `<button onclick="${fetchFunction}(${i})" ${i == currentPage ? 'disabled' : ''}>${i}</button>`;
      }

      // 下一页按钮
      if (currentPage < totalPages) {
        buttons += `<button onclick="${fetchFunction}(${currentPage + 1})">&raquo;</button>`;
      }

      pagination.innerHTML = buttons;
    }
    function search() {
      console.log('Search function invoked');
        var itemId = document.getElementById('searchItemId').value.trim();
        var itemName = document.getElementById('searchItemName').value.trim();
        var defaultIntValue = document.querySelector('[name="defaultIntValue1"]').value;
        // 构建请求体
        var data = {
          itemId: itemId,
          itemName: itemName,
          defaultIntValue: defaultIntValue
        };
        // 发送 POST 请求
      if (itemId || itemName){
        fetch('/GoodsServlet', {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json;charset=UTF-8'
          },
          body: JSON.stringify(data)
        })
                .then(response => response.json())
                .then(data => {
                  // 处理接收到的 JSON 数据并更新页面
                  renderGoodsTable(data.goodsList, '#results-table');
                  renderPagination(data.currentPage, data.totalPages, "pagination1.1", "search");
                })
                .catch(error => {
                  console.error('Error:', error);
                });

      } else {
        alert("请至少输入其中一项")
      }
    }

    function add() {
      var username = document.getElementById('addUsername').value.trim();
      var goodsname = document.getElementById('addGoodsname').value.trim();
      var price = document.getElementById('addPrice').value.trim();
      var quantity = document.getElementById('addQuantity').value.trim();
      var defaultIntValue = document.querySelector('[name="defaultIntValue2"]').value;
      if (username === '' || goodsname === '' || price === '' || quantity === '') {
        alert("请填写所有项");
      }else {
        // 构建请求体
        var data = {
          username:username,
          goodsname:goodsname,
          price:price,
          quantity:quantity,
          defaultIntValue: defaultIntValue
        };
        fetch('/GoodsServlet', {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json;charset=UTF-8'
          },
          body: JSON.stringify(data)
        })
                .then(response => response.json())
                .then(data => {
                  alert(data.message)
                })
                .catch(error => {
                  alert("添加失败")
                  console.error('Error:', error);
                });
      }
    }

    function Delete() {
      var username = document.getElementById('deleteUsername').value.trim();
      var goodsname = document.getElementById('deleteGoodsname').value.trim();
      var defaultIntValue = document.querySelector('[name="defaultIntValue3"]').value;
      if (username === '' || goodsname === '') {
        alert("请填写所有项");
      }else {
        // 构建请求体
        var data = {
          username:username,
          goodsname:goodsname,
          defaultIntValue: defaultIntValue
        };
        fetch('/GoodsServlet', {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json;charset=UTF-8'
          },
          body: JSON.stringify(data)
        })
                .then(response => response.json())
                .then(data => {
                  alert(data.message)
                })
                .catch(error => {
                  alert("删除失败")
                  console.error('Error:', error);
                });
      }
    }

    function alter() {
      var username = document.getElementById('alterUsername').value.trim();
      var goodsname = document.getElementById('alterGoodsname').value.trim();
      var price = document.getElementById('alterPrice').value.trim();
      var defaultIntValue = document.querySelector('[name="defaultIntValue4"]').value;
      if (username === '' || goodsname === '') {
        alert("请填写所有项");
      }else {
        // 构建请求体
        var data = {
          username:username,
          goodsname:goodsname,
          price:price,
          defaultIntValue: defaultIntValue
        };
        fetch('/GoodsServlet', {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json;charset=UTF-8'
          },
          body: JSON.stringify(data)
        })
                .then(response => response.json())
                .then(data => {
                  alert(data.message)
                })
                .catch(error => {
                  alert("删除失败")
                  console.error('Error:', error);
                });
      }
    }

  </script>
</head>
  <body>
  <div class="container">
  <!-- 登录信息 -->
  <div>欢迎, <span id="username"></span></div>
  <a href="/logoutServlet">注销</a>

  <!-- 选项卡按钮 -->
  <ul class="tab-buttons">
    <li class="tab" id="tab1" onclick="switchTab(1)">商品列表</li>
    <li class="tab" id="tab2" onclick="switchTab(2)">订单管理</li>
    <li class="tab" id="tab3" onclick="switchTab(3)">客户管理</li>
  </ul>

  <!-- 动态切换的选项卡内容 -->
  <div id="content1" class="tab-content">
    <h1>Goods Table</h1>
    <table id="content1-table" border="1"></table>
    <div id="pagination1"></div>
    <!-- 添加增删查改四个操作选项卡按钮 -->
    <ul class="sub-tab-buttons">
      <li class="sub-tab" id="sub-tab-add" onclick="switchSubTab('add')">添加</li>
      <li class="sub-tab" id="sub-tab-delete" onclick="switchSubTab('delete')">删除</li>
      <li class="sub-tab" id="sub-tab-search" onclick="switchSubTab('search')">查询</li>
      <li class="sub-tab" id="sub-tab-alter" onclick="switchSubTab('alter')">修改</li>
    </ul>

    <!-- 以下是四个选项卡内容的区域 -->
    <div id="sub-content-search" class="sub-tab-content">
        <label for="searchItemId">商家用户名:</label>
        <input type="text" id="searchItemId" name="itemId"><br><br>

        <label for="searchItemName">商品名称:</label>
        <input type="text" id="searchItemName" name="itemName"><br><br>

        <!-- 隐藏的输入框，用于提交一个固定的整数值 -->
        <input type="hidden" name="defaultIntValue1" value="1">

        <button id="submitButton">提交</button>

      <!-- 结果展示容器 -->
      <div id="results">
        <h1>查询结果</h1>
        <table id="results-table" border="1"></table>
        <div id="pagination1.1"></div>
      </div>
    </div>
    </div>
    <div id="sub-content-add" class="sub-tab-content">
        <label for="addUsername">用户名:</label>
        <input type="text" id="addUsername" name="username"><br><br>

        <label for="addGoodsname">商品名称:</label>
        <input type="text" id="addGoodsname" name="goodsname"><br><br>

        <label for="addPrice">价格:</label>
        <input type="text" id="addPrice" name="price"><br><br>

        <label for="addQuantity">数量:</label>
        <input type="text" id="addQuantity" name="picture"><br><br>

        <!-- 隐藏的输入框，用于提交一个固定的整数值 -->
        <input type="hidden" name="defaultIntValue2" value="2">

        <button id="addButton">提交</button>
    </div>

    <div id="sub-content-delete" class="sub-tab-content">
        <label for="deleteUsername">用户名:</label>
        <input type="text" id="deleteUsername" name="username"><br><br>

        <label for="deleteGoodsname">商品名称:</label>
        <input type="text" id="deleteGoodsname" name="goodsname"><br><br>

        <!-- 隐藏的输入框，用于提交一个固定的整数值 -->
        <input type="hidden" name="defaultIntValue3" value="3">

        <button id="deleteButton">提交</button>
    </div>

    <div id="sub-content-alter" class="sub-tab-content">
        <label for="alterUsername">用户名:</label>
        <input type="text" id="alterUsername" name="username"><br><br>

        <label for="alterGoodsname">商品名称:</label>
        <input type="text" id="alterGoodsname" name="goodsname"><br><br>

        <label for="alterPrice">价格:</label>
        <input type="text" id="alterPrice" name="price"><br><br>

        <!-- 隐藏的输入框，用于提交一个固定的整数值 -->
        <input type="hidden" name="defaultIntValue4" value="4">

        <button id="alterButton">提交</button>
    </div>
  </div>

  <div id="content2" class="tab-content">
    <h2>订单管理</h2>
    <table id="content2-saletable" border="1"></table>
    <div id="pagination2.1"></div>
    <h2>销售统计报表</h2>
    <table id="content2-statstable" border="1"></table>
    <div id="pagination2.2"></div>
  </div>

  <div id="content3" class="tab-content">
    <h2>客户列表</h2>
    <table id="content3-usertable" border="1"></table>
    <div id="pagination3.1"></div>
  </div>
  </div>
  </body>
  </html>
