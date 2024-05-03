<?php
$con = mysqli_connect("localhost","yy","123456");
if (!$con) {
    die('数据库连接失败:'. mysqli_error($con));
} else {
    mysqli_set_charset($con, 'UTF-8'); 
    mysqli_select_db($con, "websecurity");
    $result = mysqli_query($con, "SELECT * FROM student ORDER BY age");
    echo"<h2>MySQL测验</h2><p>学生列表如下：</p>";
    echo "<table border='2'width='300'>
    <tr><th>id</th><th>name</th><th>sex</th><th>age</th></tr>";
    while($row = mysqli_fetch_array($result)) {
        echo "<tr>";
        echo "<td>" . $row['id'] . "</td>";
        echo "<td>" . $row['name'] . "</td>";
        echo "<td>" . $row['sex'] . "</td>";
        echo "<td>" . $row['age'] . "</td>";
        echo "</tr>";
    }

    echo "</table>";
}
mysqli_close($con);
?>

