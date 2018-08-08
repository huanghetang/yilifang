student列表:<br>
<table border="1">
	<tr>
		<th>序号</th>
		<th>学号</th>
		<th>姓名</th>
		<th>年龄</th>
		<th>地址</th>
	</tr>

<#list stuList as stu>
	<#if stu_index %2==0>
		<tr bgcolor="red">
	<#else>
		<tr bgcolor="green">
	</#if>
		<td>${stu_index}</td>
		<td>${stu.id}</td>
		<td>${stu.name}</td>
		<td>${stu.age}</td>
		<td>${stu.address}</td>
	</tr>
</#list>
</table>
<!--?date,?time,?datetime,string(pattern) -->
测试日期时间:${date?date},${date?time},${date?datetime},${date?string("yyyy-MM-dd HH:mm:ss")}<br>
测试null:${testnull!"这是null的默认值"}<br>
测试if中的null:
<#if testnull??>
testnull不为null时...
<#else>
testnull为Null时...
</#if><br>
测试include引入另一个文件:
<#include "hello.java">
