<%--
  Created by IntelliJ IDEA.
  User: 13728
  Date: 2024/12/2
  Time: 15:22
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>用户注册</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            background-color: #f4f4f9;
            margin: 0;
            padding: 0;
            display: flex;
            align-items: center;
            justify-content: center;
            min-height: 100vh;
        }
        .register-container {
            background-color: white;
            padding: 2rem;
            border-radius: 8px;
            box-shadow: 0 4px 8px rgba(0,0,0,0.1);
            width: 300px;
            max-width: 90%;
            text-align: center;
        }
        h2 {
            margin-bottom: 1.5rem;
            font-size: 1.5rem;
            color: #333;
        }
        label {
            display: block;
            margin: 0.5rem 0 0.2rem 0;
            text-align: left;
            color: #555;
        }
        input[type="text"],
        input[type="password"],
        input[type="email"] {
            width: 100%;
            padding: 0.7rem;
            margin-bottom: 1rem;
            border-radius: 4px;
            border: 1px solid #ddd;
            box-sizing: border-box;
        }
        button {
            width: 100%;
            padding: 0.8rem;
            background-color: #007bff;
            color: white;
            border: none;
            border-radius: 4px;
            cursor: pointer;
            font-size: 1rem;
        }
        button:hover {
            background-color: #0056b3;
        }
        a {
            color: #007bff;
            text-decoration: none;
            display: inline-block;
            margin-top: 1rem;
        }
        a:hover {
            text-decoration: underline;
        }
    </style>
</head>
<body>
<div class="register-container">
    <h2>用户注册</h2>
    <form action="RegisterServlet" method="POST">
        <label for="username">用户名:</label>
        <input type="text" id="username" name="username" required>

        <label for="password">密码:</label>
        <input type="password" id="password" name="password" required>

        <label for="email">邮箱:</label>
        <input type="email" id="email" name="email" required>

        <button type="submit">注册</button>
    </form>

    <a href="login.jsp">返回登录页面</a>
</div>
</body>
</html>


