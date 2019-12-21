require.config({
    baseUrl: '../js',
    paths: {
        jquery: 'lib/jquery-3.4.1.min',
        utils: 'utils/utils',
        layui: '../layui/layui' //layui.js-模块化方式, layui.all.js-非模块化方式
    },
    shim: {
        'layui': { //layui不遵循于AMD规范
            deps: ['jquery'], //依赖的模块
            exports: 'layui'
        }
    }
});

require(['layui', 'utils'], function (layui, utils) {
    layui.use(['table', 'element', 'form'], function () {
        let table = layui.table
            , element = layui.element //导航的hover效果、二级菜单等功能，需要依赖element模块
            , form = layui.form
            , $ = layui.$; //使用layui内置的JQuery，table依赖layer，layer依赖JQuery
        //数据表格
        table.render({
            elem: '#roleInfoTable'
            // , id: 'roleInfoTable'
            // , height: 580 //注释后表单高度自适应
            , url: 'http://127.0.0.1:8080/assistant/admin/getRolesByPage' //数据接口
            // , where: {} //设定异步数据接口的额外参数
            , toolbar: '#roleInfoTableToolbar' //开启头部工具栏，并为其绑定左侧模板
            , title: 'Role账号数据表'
            // , page: true //开启分页
            , page: { //开启分页
                curr: 1 //重新从第 1 页开始
            }
            , limit: 10
            , limits: [10, 20, 30, 50, 100]
            , cols: [[ //表头
                {type: 'checkbox', fixed: 'left'}
                , {field: 'id', title: 'ID', minWidth: 60, sort: true, fixed: 'left'}
                , {field: 'type', title: '类型', minWidth: 75, sort: true}
                , {field: 'email', title: '电子邮箱', minWidth: 176}
                , {field: 'nickname', title: '昵称', minWidth: 135}
                , {field: 'password', title: '密码', width: 176}
                , {field: 'phoneNumber', title: '手机号码', width: 120}
                , {field: 'image', title: '头像', width: 60}
                , {field: 'name', title: '姓名', width: 135, sort: true}
                , {field: 'sex', title: '性别', minWidth: 75, sort: true}
                , {field: 'birthday', title: '出生日期', minWidth: 102, sort: true}
                , {field: 'qq', title: 'QQ', width: 120}
                , {field: 'regTime', title: '注册时间', minWidth: 160, sort: true}
                , {field: 'lastLogInTime', title: '最近一次登入时间', minWidth: 160}
                , {field: 'lastLogOutTime', title: '最近一次登出时间', minWidth: 160}
                , {field: 'ip', title: 'IP地址', minWidth: 130}
                , {field: 'activated', title: '账号状态', minWidth: 102, sort: true}
                , {fixed: 'right', title: '操作', toolbar: '#roleInfoTableRowBar', width: 112}
            ]]
        });//end 数据表格

        //头工具栏事件
        table.on('toolbar(roleInfoTable)', function (obj) { //toolbar(lay-filter)
            let checkStatus = table.checkStatus(obj.config.id)
                , data = checkStatus.data; //获取选中的数据

            let idList = [];//let idList = new Array()
            $.each(data, function (i, role) {
                idList[i] = role.id;
            });

            switch (obj.event) {
                case 'add':
                    layer.open({
                        type: 2, //iframe层
                        area: ['650px', '500px'], //宽高
                        fixed: true, //固定
                        maxmin: false, //最大小化
                        closeBtn: 1, //右上关闭
                        shadeClose: false, //点击遮罩关闭
                        resize: false, //是否允许拉伸
                        move: false,  //禁止拖拽
                        title: "新添数据",
                        content: 'addRoleForm.html'
                    });
                    //重载数据表格
                    table.reload('roleInfoTable', {
                        where: {}//设定异步数据接口的额外参数，任意设
                        , page: {
                            curr: 1 //获取起始页，重新从第 1 页开始
                        }
                    }); //end table.reload
                    break;//add
                case 'delete':
                    if (data.length === 0) {
                        layer.msg('请至少选择一行');
                    } else {
                        layer.confirm('真的删除选中行么？', function (index) {
                            $.ajax({
                                url: "/assistant/admin/deleteRoles",
                                data: {
                                    idList: idList.toString()
                                },
                                dataType: 'json',//服务器返回json格式数据
                                type: 'delete',//HTTP请求类型
                                timeout: 10000,//超时时间设置为10秒
                                success: function (data) {
                                    if (data.success) {
                                        //重载数据表格
                                        table.reload('roleInfoTable', {
                                            where: {}//设定异步数据接口的额外参数，任意设
                                            , page: {
                                                curr: 1 //获取起始页，重新从第 1 页开始
                                            }
                                        }); //end table.reload

                                        layer.close(index);//关闭弹窗
                                    }
                                },
                                error: function (xhr, type, errorThrown) {
                                    console.log(xhr);
                                    console.log(type);
                                    console.log(errorThrown);
                                }

                            });//end ajax
                        });//end layer.confirm
                    }
                    break;//delete
                case 'getCheckLength':
                    layer.msg('选中了：' + data.length + ' 个');
                    break;//getCheckLength
                case 'flush':
                    table.reload('roleInfoTable', {
                        url: 'http://127.0.0.1:8080/assistant/admin/getRolesByPage'
                        , page: {
                            curr: 1 //获取起始页，重新从第 1 页开始
                        }
                    }); //end table.reload
                    break;//flush
            }//end switch(obj.event)

        });//end 头工具栏事件

        //监听行工具事件
        table.on('tool(roleInfoTable)', function (obj) {

            let data = obj.data;
            switch (obj.event) {
                case 'del':
                    layer.confirm('真的删除该行么？', function (index) {
                        $.ajax({
                            url: "/assistant/admin/deleteRole",
                            data: {
                                id: data.id
                            },
                            dataType: 'json',//服务器返回json格式数据
                            type: 'delete',//HTTP请求类型
                            timeout: 10000,//超时时间设置为10秒
                            success: function (data) {
                                if (data.success) {
                                    //重载数据表格
                                    table.reload('roleInfoTable', {
                                        where: {}//设定异步数据接口的额外参数，任意设
                                        , page: {
                                            curr: 1 //获取起始页，重新从第 1 页开始
                                        }
                                    }); //end table.reload

                                    layer.close(index);
                                }//end if
                            },
                            error: function (xhr, type, errorThrown) {
                                console.log(xhr);
                                console.log(type);
                                console.log(errorThrown);
                            }

                        });//end ajax
                    });//end layer.confirm
                    break; //end del
                case "update":
                    //data数据排除password所对应的键值对
                    delete data.password;
                    localStorage.setItem("updateData", JSON.stringify(data));

                    layer.open({
                        type: 2, //iframe层
                        area: ['650px', '500px'], //宽高
                        fixed: true, //固定
                        maxmin: false, //最大小化
                        closeBtn: 1, //右上关闭
                        shadeClose: false, //点击遮罩关闭
                        resize: false, //是否允许拉伸
                        move: false,  //禁止拖拽
                        title: "编辑数据",
                        content: 'updateRoleForm.html'
                    });//end layer.open
                    break; //end update
            }//switch
        });//end 监听行工具事件

        //监听搜索事件
        $("input[name='search']").on("keyup", function () {
            //按下回车执行
            if (utils.isPressEnter()) {
                const SEARCH_DOM = $(this)
                    , SELECT_DOM = $("select[name='search']")
                    , searchCValue = SEARCH_DOM.val()
                    , select = SELECT_DOM.val();

                //重载数据表格
                table.reload('roleInfoTable', {
                    url: 'http://127.0.0.1:8080/assistant/admin/getRolesByKeyword'
                    , method: 'get'
                    , where: {
                        keyword: select,
                        value: searchCValue
                    }//设定异步数据接口的额外参数，任意设
                    , page: {
                        curr: 1 //获取起始页，重新从第 1 页开始
                    }
                });//end table.reload
            }//end if
        });//end 监听搜索事件

        //监听行点击事件
        table.on('row(roleInfoTable)', function (obj) {
            //标注选中行样式
            obj.tr.addClass("layui-table-click").siblings().removeClass("layui-table-click");
        });//end 监听行点击事件

        //账号信息
        $("#roleInfo").on("click", function () {
            window.location.reload();
        }); //end 账号信息

        //激活管理
        $("#roleActivate").on("click", function () {
            $(this).addClass("layui-this").siblings().removeClass("layui-this");//选中高亮

            //面包屑
            const BREADCRUMB_DIV = $("#breadcrumb");
            BREADCRUMB_DIV.empty();
            BREADCRUMB_DIV.html(`
                <span class="layui-breadcrumb">
                    <a href="#">用户信息</a>
                    <a href="#">账号信息</a>
                    <a><cite>激活管理</cite></a>
                </span>
            `);
            element.render('breadcrumb');

            //搜索下拉选项
            $("#searchSelect").html(`
                <option value="email">电子邮箱</option>
                <option value="phoneNumber">手机号码</option>
                <option value="ip">IP</option>
            `);
            form.render();//重新渲染
            //监听搜索框
            $("input[name='search']").off().on("keyup", function () { //移除原先所写的事件处理程序再进行事件监听
                if (utils.isPressEnter()) {//按下回车执行
                    const SEARCH_DOM = $(this)
                        , SELECT_DOM = $("select[name='search']")
                        , searchCValue = SEARCH_DOM.val()
                        , select = SELECT_DOM.val();

                    //重载数据表格
                    table.reload('activateRoleInfoTable', {
                        url: 'http://127.0.0.1:8080/assistant/admin/getRolesByKeyword'
                        , method: 'get'
                        , where: {
                            keyword: select,
                            value: searchCValue
                        }//设定异步数据接口的额外参数，任意设
                        , page: {
                            curr: 1 //获取起始页，重新从第 1 页开始
                        }
                    });//end table.reload
                }//end if
            });//end 监听搜索事件

            //数据表格重载
            const DATA_TABLE_DIV = $("#dataTable");
            DATA_TABLE_DIV.empty(); //清空数据表的DOM结构
            //重新构建DOM结构
            DATA_TABLE_DIV.html(`
                <table class="layui-hide" id="activateRoleInfoTable" lay-filter="activateRoleInfoTable"></table>
                <script type="text/html" id="activateRoleInfoTableToolbar">
                    <div class="layui-inline" title="获取选中数目" lay-event="getCheckLength">
                        <i class="layui-icon layui-icon-tips" style="color: #555;"></i>
                    </div>
                    <div class="layui-inline" title="刷新表单" lay-event="flush">
                        <i class="layui-icon layui-icon-refresh-3" style="color: #555;"></i>
                    </div>
                </script>
                <script type="text/html" id="activateRoleInfoTableRowBar">
                    <a class="layui-btn layui-btn-xs layui-btn-xs" lay-event="activate">激活</a>
                    <a class="layui-btn layui-btn-danger layui-btn-xs" lay-event="deactivate">停用</a>
                </script>
            `);
            //对重建的DOM进行渲染
            table.render({
                elem: '#activateRoleInfoTable'
                , id: 'activateRoleInfoTable'
                // , height: 580 //注解后表单高度自适应
                , url: 'http://127.0.0.1:8080/assistant/admin/getRolesByPage' //数据接口
                , where: {} //设定异步数据接口的额外参数
                , toolbar: '#activateRoleInfoTableToolbar' //开启头部工具栏，并为其绑定左侧模板
                , title: 'Role激活数据表'
                // , page: true //开启分页
                , page: { //开启分页
                    curr: 1 //重新从第 1 页开始
                }
                , limit: 10
                , limits: [10, 20, 30, 50, 100]
                , cols: [[ //表头
                    {type: 'checkbox', fixed: 'left'}
                    , {field: 'id', title: 'ID', minWidth: 60, sort: true, fixed: 'left'}
                    , {field: 'email', title: '电子邮箱', minWidth: 176}
                    , {field: 'phoneNumber', title: '手机号码', width: 120}
                    , {field: 'ip', title: 'IP地址', minWidth: 130}
                    , {field: 'activated', title: '账号状态', minWidth: 102, sort: true}
                    , {fixed: 'right', title: '操作', toolbar: '#activateRoleInfoTableRowBar', width: 112}
                ]]
            });//end table.render

            //监听行点击事件
            table.on('row(activateRoleInfoTable)', function (obj) {
                //标注选中行样式
                obj.tr.addClass("layui-table-click").siblings().removeClass("layui-table-click");
            });//end 监听行点击事件

            //监听行工具事件
            table.on('tool(activateRoleInfoTable)', function (obj) {
                let data = obj.data;
                switch (obj.event) {
                    case "activate":
                        if(data.activated === 1){
                            layer.msg("已经激活了，不需要再进行操作！",{time:2000});
                            return false;
                        }
                        layer.confirm('正在进行激活操作，确定继续吗？', function (index) {
                            $.ajax({
                                url: "/assistant/admin/activateRole",
                                data: {
                                    id: data.id,
                                    activated: 1
                                },
                                dataType: 'json',//服务器返回json格式数据
                                type: 'put',//HTTP请求类型
                                timeout: 10000,//超时时间设置为10秒
                                success: function (data) {
                                    if (data.success) {
                                        //重载数据表格
                                        table.reload('activateRoleInfoTable', {
                                            page: {
                                                curr: 1 //获取起始页，重新从第 1 页开始
                                            }
                                            , title: 'Role激活数据表'
                                        }); //end table.reload

                                        layer.close(index);
                                    }//end if
                                },
                                error: function (xhr, type, errorThrown) {
                                    console.log(xhr);
                                    console.log(type);
                                    console.log(errorThrown);
                                }

                            });//end ajax
                        });//end layer.confirm
                        break;//activate
                    case "deactivate":
                        if(data.activated === 0){
                            layer.msg("已经停用了，不需要再进行操作！",{time:2000});
                            return false;
                        }
                        layer.confirm('正在进行停用操作，确定继续吗？', function (index) {
                            $.ajax({
                                url: "/assistant/admin/activateRole",
                                data: {
                                    id: data.id,
                                    activated: 0
                                },
                                dataType: 'json',//服务器返回json格式数据
                                type: 'put',//HTTP请求类型
                                timeout: 10000,//超时时间设置为10秒
                                success: function (data) {
                                    if (data.success) {
                                        //重载数据表格
                                        table.reload('activateRoleInfoTable', {
                                            page: {
                                                curr: 1 //获取起始页，重新从第 1 页开始
                                            }
                                            , title: 'Role激活数据表'
                                        }); //end table.reload

                                        layer.close(index);
                                    }//end if
                                },
                                error: function (xhr, type, errorThrown) {
                                    console.log(xhr);
                                    console.log(type);
                                    console.log(errorThrown);
                                }

                            });//end ajax
                        });//end layer.confirm
                        break;//freeze
                }//switch
            });//end 监听行工具事件

            //监听头工具栏事件
            table.on('toolbar(activateRoleInfoTable)', function (obj) {
                let checkStatus = table.checkStatus(obj.config.id)
                    , data = checkStatus.data; //获取选中的数据

                switch (obj.event) {
                    case 'getCheckLength':
                        layer.msg('选中了：' + data.length + ' 个');
                        break;//getCheckLength
                    case 'flush':
                        table.reload('activateRoleInfoTable', {
                            page: {
                                curr: 1 //获取起始页，重新从第 1 页开始
                            }
                        }); //end table.reload
                        break;//flush
                }//end switch(obj.event)

            });//end 头工具栏事件

        });//end 激活管理

        //头像管理
        $("#roleHeadPortraits").on("click", function () {
            $(this).addClass("layui-this").siblings().removeClass("layui-this");//选中高亮
        });

    });//end layui
});//end require