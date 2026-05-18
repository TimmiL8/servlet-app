<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Lab 3 - DAO CRUD</title>
    <style>
        body { font-family: Arial, sans-serif; margin: 40px; }
        .section { margin-bottom: 20px; padding: 15px; border: 1px solid #ccc; max-width: 300px; display: inline-block; vertical-align: top; margin-right: 20px; }
        input, button { margin-top: 10px; padding: 5px; width: 100%; box-sizing: border-box; }
    </style>
</head>
<body>

<h1>User Dashboard (CRUD Operations)</h1>

<div class="section">
    <h3>Add a New User (Create)</h3>
    <form action="user" method="POST">
        <input type="hidden" name="action" value="create">
        <label for="username">Username:</label><br>
        <input type="text" id="username" name="username" required>
        <label for="email">Email:</label><br>
        <input type="text" id="email" name="email" required>
        <button type="submit">Add User</button>
    </form>
</div>

<div class="section">
    <h3>View All Users (Read)</h3>
    <form action="user" method="GET">
        <input type="hidden" name="action" value="print">
        <button type="submit">Print Existing Users</button>
    </form>
</div>

<div class="section">
    <h3>Find User by ID (Read)</h3>
    <form action="user" method="GET">
        <input type="hidden" name="action" value="getById">
        <label for="searchId">User ID:</label><br>
        <input type="number" id="searchId" name="id" required>
        <button type="submit">Find User</button>
    </form>
</div>

<div class="section">
    <h3>Update Existing User</h3>
    <form action="user" method="POST">
        <input type="hidden" name="action" value="update">
        <label for="updateId">User ID to update:</label><br>
        <input type="number" id="updateId" name="id" required>
        <label for="updateUsername">New Username:</label><br>
        <input type="text" id="updateUsername" name="username" required>
        <label for="updateEmail">New Email:</label><br>
        <input type="text" id="updateEmail" name="email" required>
        <button type="submit">Update User</button>
    </form>
</div>

<div class="section">
    <h3>Delete User</h3>
    <form action="user" method="POST">
        <input type="hidden" name="action" value="delete">
        <label for="deleteId">User ID to delete:</label><br>
        <input type="number" id="deleteId" name="id" required>
        <button type="submit" style="background-color: #ff4d4d; color: white; border: none;">Delete User</button>
    </form>
</div>

</body>
</html>