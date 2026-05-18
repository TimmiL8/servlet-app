<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Lab 1 - User Management</title>
    <style>
        body { font-family: Arial, sans-serif; margin: 40px; }
        .section { margin-bottom: 20px; padding: 15px; border: 1px solid #ccc; max-width: 300px; }
        input, button { margin-top: 10px; padding: 5px; width: 100%; }
    </style>
</head>
<body>

<h1>User Dashboard</h1>

<div class="section">
    <h3>Add a New User</h3>
    <form action="user" method="POST">
        <label for="username">Username:</label><br>
        <input type="text" id="username" name="username" required>
        <button type="submit">Add User</button>
    </form>
</div>

<div class="section">
    <h3>View Users</h3>
    <form action="user" method="GET">
        <input type="hidden" name="action" value="print">
        <button type="submit">Print Existing Users</button>
    </form>
</div>

</body>
</html>