<%--
    注意Bootstrap版本需要一致
  Created by IntelliJ IDEA.
  User: Zhongbo
  Date: 2017/2/20
  Time: 18:03
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!--引入jstl -->
<%@include file="common/tag.jsp"%>
<!DOCTYPE html>
<html>
    <head>
        <title>秒杀商品列表</title>
        <%@include file="common/head.jsp"%>
    </head>
    <body>
        <div class="container">
            <div class="panel panel-default">
                <div class="panel-heading text-center">
                    <h2>秒杀列表</h2>
                </div>
                <div class="panel-body">
                    <table class="table table-hover">
                        <thead>
                            <tr>
                                <th>名称</th>
                                <th>库存</th>
                                <th>开始时间</th>
                                <th>结束时间</th>
                                <th>创建时间</th>
                                <th>详情页</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="sk" items="${list}">
                                <tr>
                                    <th>${sk.name}</th>
                                    <th>${sk.number}</th>
                                    <th>
                                        <fmt:formatDate value="${sk.startTime}" pattern="yyyy-MM-dd HH:mm:ss"/>
                                    </th>
                                    <th>
                                        <fmt:formatDate value="${sk.endTime}" pattern="yyyy-MM-dd HH:mm:ss"/>
                                    </th>
                                    <th>
                                        <fmt:formatDate value="${sk.createTime}" pattern="yyyy-MM-dd HH:mm:ss"/>
                                    </th>
                                    <th>
                                        <a class="btn btn-info" href="/myseckill/${sk.seckillId}/detail" target="_blank">link</a>
                                    </th>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    </body>
    <!-- jQuery文件。务必在bootstrap.min.js 之前引入 -->
    <script src="http://apps.bdimg.com/libs/jquery/2.0.0/jquery.min.js"></script>

    <!-- 最新的 Bootstrap 核心 JavaScript 文件 -->
    <script src="http://apps.bdimg.com/libs/bootstrap/3.3.0/js/bootstrap.min.js"></script>
</html>
