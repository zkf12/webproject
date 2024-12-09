<%--
  Created by IntelliJ IDEA.
  User: 13728
  Date: 2024/12/4
  Time: 21:20
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

    a {
      margin: 20px;
      display: inline-block;
    }

    /* 选项卡样式 */
    .tab-buttons {
      list-style-type: none;
      padding: 0;
      margin: 20px 0;
      text-align: center;
    }

    .tab-buttons li {
      display: inline-block;
      padding: 10px 20px;
      cursor: pointer;
      margin-right: 5px;
      background-color: #e0e0e0;
      border-radius: 5px;
      transition: background-color 0.3s;
    }

    .tab-buttons li:hover {
      background-color: #d0d0d0;
    }

    .tab-buttons li.selected {
      font-weight: bold;
      color: white;
      background-color: #007bff;
    }

    .tab-buttons li.disabled {
      cursor: not-allowed;
      background-color: #ccc;
    }

    /* 内容样式 */
    .tab-content {
      display: none;
      padding: 20px;
      background-color: #fff;
      border: 1px solid #ddd;
      border-radius: 5px;
      box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1);
    }

    .tab-content.active {
      display: block;
    }

    /* 表格样式 */
    table {
      width: 100%;
      margin-top: 20px;
      border-collapse: collapse;
    }

    th, td {
      border: 1px solid #ddd;
      padding: 8px;
      text-align: center;
    }

    th {
      background-color: #f4f4f4;
    }

    tr:nth-child(even) {
      background-color: #f9f9f9;
    }

    /* 移动设备优化 */
    @media screen and (max-width: 600px) {
      th, td {
        font-size: 12px;
        padding: 6px;
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

    // 确保DOM完全加载后再运行脚本
    document.addEventListener('DOMContentLoaded', function() {
      // 绑定按钮的点击事件
      document.getElementById('submitButton').addEventListener('click', search);
      document.getElementById('returnButton').addEventListener('click', function(){
        fetchGoodsList(1);
      });
    })

    // 页面加载时默认显示第一个选项卡，同时初始化其他选项卡
    window.onload = function() {
      // 获取 sessionStorage 中的 'username' 值
      username = sessionStorage.getItem('username');
      // 设置用户名显示在 <span> 标签中
      document.getElementById('username').textContent = username || 'Guest'; // 如果没有 username，则显示 'Guest'

      fetchGoodsList(1);
      fetchLoverList(1);
      fetchHistory(1)
      switchTab(1);


      // 绑定按钮的点击事件
      document.getElementById('purchaseButton').addEventListener('click', click);

  };

  function fetchGoodsList(page) {
  fetch(`/GoodsServlet?page=${page}`)
  .then(response => {
  if (!response.ok) throw new Error("Failed to fetch goods data");
  return response.json();
              })
              .then(data => {
                renderGoodsTable(data.goodsList);
                renderPagination(data.currentPage, data.totalPages, "pagination1", "fetchGoodsList");
              })
              .catch(error => {
                console.error("Error:", error);
                alert("Failed to load goods data.");
              });
    }

    function fetchLoverList(page) {
      fetch(`/shoppingCartServlet?page=${page}&lover=${encodeURIComponent(username)}`)
              .then(response => {
                if (!response.ok) throw new Error("Failed to fetch cart data");
                return response.json();
              })
              .then(data => {
                renderLoverTable(data.loverList);
                renderPagination(data.currentPage, data.totalPages, "pagination2", "fetchLoverList");
              })
              .catch(error => {
                console.error("Error:", error);
                alert("Failed to load cart data.");
              });
    }

    function fetchHistory(page) {
      fetch(`/historyServlet?page=${page}&user=${encodeURIComponent(username)}`)
              .then(response => {
                if (!response.ok) throw new Error("Failed to fetch history data");
                return response.json();
              })
              .then(data => {
                renderHistoryTable(data.historyList);
                renderPagination(data.currentPage, data.totalPages, "pagination3", "fetchHistory");
              })
              .catch(error => {
                console.error("Error:", error);
                alert("Failed to load history data.");
              });
    }

    function renderGoodsTable(goodsList) {
      const table = document.querySelector('#content1-table');
      let rows = `
      <tr>
        <th>Goods Name</th>
        <th>Owner</th>
        <th>Price</th>
        <th>Quantity</th>
        <th>Action</th>
      </tr>`;

      goodsList.forEach(goods => {
        rows += `
        <tr>
          <td>${goods.goodsName}</td>
          <td>${goods.owner}</td>
          <td>${goods.price}</td>
          <td>${goods.quantity}</td>
          <td><button onclick="addCart(${goods.id}, username)">加入购物车</button></td>
        </tr>`;
      });
      table.innerHTML = rows;
    }

    // 在渲染购物车商品表时，读取session中勾选的商品ID
    function renderLoverTable(goodsList) {
      const table = document.querySelector('#content2-table');
      let rows = `
    <tr>
      <th>Goods Name</th>
      <th>Owner</th>
      <th>Price</th>
      <th>Action</th>
    </tr>`;

      // 获取session中保存的已勾选商品ID
      const selectedGoodsIds = sessionStorage.getItem('selectedGoodsIds') ? JSON.parse(sessionStorage.getItem('selectedGoodsIds')) : [];
      goodsList.forEach(goods => {
        // 判断当前商品是否已经被勾选
        const isChecked = selectedGoodsIds.includes(goods.id);
        rows += `
        <tr>
          <td>${goods.goodsName}</td>
          <td>${goods.owner}</td>
          <td>${goods.price}</td>
          <td><input type="checkbox" name="selectedGoodsIds" value="${goods.id}" ${isChecked ? 'checked' : ''}></td>
        </tr>`;
      });
      table.innerHTML = rows;
    }

    function renderHistoryTable(goodsList) {
      const table = document.querySelector('#content3-table');
      let rows = `
    <tr>
      <th>Goods Name</th>
      <th>Time</th>
      <th>Owner</th>
      <th>Price</th>
    </tr>`;

      goodsList.forEach(goods => {
        rows += `
        <tr>
          <td>${goods.goodsName}</td>
          <td>${goods.time}</td>
          <td>${goods.owner}</td>
          <td>${goods.price}</td>
        </tr>`;
      });
      table.innerHTML = rows;
    }

    // 监听勾选框的变化，更新session中的勾选状态
    document.addEventListener('change', function(event) {
      if (event.target.type === 'checkbox' && event.target.name === 'selectedGoodsIds') {
        let selectedGoodsIds = sessionStorage.getItem('selectedGoodsIds') ? JSON.parse(sessionStorage.getItem('selectedGoodsIds')) : [];

        if (event.target.checked) {
          selectedGoodsIds.push(parseInt(event.target.value));
        } else {
          selectedGoodsIds = selectedGoodsIds.filter(id => id !== parseInt(event.target.value));
        }

        // 将更新后的选中商品ID保存到sessionStorage
        sessionStorage.setItem('selectedGoodsIds', JSON.stringify(selectedGoodsIds));
      }
    });

    function addCart(id, username) {
      fetch(`/addShoppingCartServlet?id=${id}&username=${username}`)
              .then(response => response.json())
              .then(data => alert(data))
              .catch(error => {
                console.error("Error:", error);
                alert(".");
              });
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

    function click() {
      // 获取session中保存的已勾选商品ID
      const selectedGoodsIds = sessionStorage.getItem('selectedGoodsIds')
              ? JSON.parse(sessionStorage.getItem('selectedGoodsIds'))
              : [];

      // 如果没有勾选商品，给出提示
      if (selectedGoodsIds.length === 0) {
        alert("未选择任何商品！");
        return;
      }

      // 发送POST请求到/purchaseServlet，携带selectedGoodsIds数据
      fetch('/purchaseServlet', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({ selectedGoodsIds })
      })
              .then(response => response.json())
              .then(data => {
                if (data.success) {
                  if (confirm(`总价为：${data.totalPrice}元，是否确认付款？`)) {
                    alert('付款成功！谢谢您的购买！');
                    window.location.href = '/user_page.jsp';
                  } else {
                    alert('付款已取消！');
                    window.location.href = '/user_page.jsp';
                  }
                } else {
                  alert(data.message || '购买失败，请稍后重试。');
                }
              })
              .catch(error => {
                console.error('请求失败:', error);
                alert("请求失败，请检查网络连接。");
              })
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
                  renderGoodsTable(data.goodsList);
                  renderPagination(data.currentPage, data.totalPages, "pagination1.1", "search");
                })
                .catch(error => {
                  console.error('Error:', error);
                });

      } else {
        alert("请至少输入其中一项")
      }
    }

  </script>
</head>
<body>
<!-- 登录信息 -->
<div>欢迎, <span id="username"></span></div>
<a href="/logoutServlet">注销</a>

<!-- 选项卡按钮 -->
<ul class="tab-buttons">
  <li class="tab" id="tab1" onclick="switchTab(1)">购买</li>
  <li class="tab" id="tab2" onclick="switchTab(2)">购物车</li>
  <li class="tab" id="tab3" onclick="switchTab(3)">购买记录</li>
</ul>

<!-- 动态切换的选项卡内容 -->
<div id="content1" class="tab-content">
  <h1>Goods Table</h1>
  <table id="content1-table" border="1"></table>
  <div id="pagination1"></div>
  <div class="sub-tab" id="sub-tab-add" onclick="switchSubTab('add')">添加</div>

  <div id="sub-content-search" class="sub-tab-content">
    <label for="searchItemId">商家用户名:</label>
    <input type="text" id="searchItemId" name="itemId"><br><br>

    <label for="searchItemName">商品名称:</label>
    <input type="text" id="searchItemName" name="itemName"><br><br>

    <!-- 隐藏的输入框，用于提交一个固定的整数值 -->
    <input type="hidden" name="defaultIntValue1" value="1">

    <button id="submitButton">提交</button>
    <button id="returnButton">返回</button>
  </div>
</div>

<div id="content2" class="tab-content">
  <table id="content2-table" border="1"></table>
  <div id="pagination2"></div>
  <button id="purchaseButton">购买</button>
</div>

<div id="content3" class="tab-content">
  <h2>购买记录</h2>
  <table id="content3-table" border="1"></table>
  <div id="pagination3"></div>
  </ul>
</div>

</body>
</html>


