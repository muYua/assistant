require.config({
    baseUrl: 'js',
    paths: {
        jquery: 'lib/jquery-3.4.1.min',
        utils: 'utils/utils',
        layui: '../layui/layui', //layui.js-模块化方式, layui.all.js-非模块化方式
        ckeditor: 'lib/ckeditor/ckeditor',
        ckeditorLanguage: 'lib/ckeditor/zh-cn'
    },
    shim: {
        'layui': { //layui不遵循于AMD规范
            deps: ['jquery'], //依赖的模块
            exports: 'layui'
        }
    }
});

require(['layui', 'utils', 'ckeditor', 'ckeditorLanguage'], function (layui, utils, ClassicEditor) {
    layui.use(['table', 'element', 'upload'], function () {
        let table = layui.table
            , element = layui.element //导航的hover效果、二级菜单等功能，需要依赖element模块
            , upload = layui.upload
            , $ = layui.$; //使用layui内置的JQuery，table依赖layer，layer依赖JQuery
   
        /* 注销 */
        $("#logout").on("click", function () {
            let encryptedId = localStorage.getItem("id");
            if(utils.isEmpty(encryptedId))
                return false;
            $.ajax({
                url: 'logout',
                type: 'post',
                dataType: 'json',
                timeout: 10000,
                data: {
                    id: encryptedId
                },
                success: function (data) {
                    if(data.success){
                        localStorage.removeItem("token");
                        localStorage.removeItem("id");
                        localStorage.removeItem("type");
                        window.location.href="index.html";
                    } else {
                        layer.msg("注销失败，请重试！");
                    }
                },
                error: function (xhr, type, errorThrown) {
                    console.log(xhr);
                    console.log(type);
                    console.log(errorThrown);
                    layer.msg("注销失败，请重试！");
                }
            });//end ajax
        });//end logout.onclick
        
        /* 考勤情况 */
        $("#checkSignIn").on("click", function () {
            $(this).addClass("layui-this").siblings().removeClass("layui-this");//选中高亮

            //页面内容替换并填充
            const CONTENT_BODY_DIV = $("#contentBody");
            CONTENT_BODY_DIV.empty();

            CONTENT_BODY_DIV.html(`
                <!-- 面包屑 -->
                <div id="breadcrumb">
                    <span class="layui-breadcrumb">
                        <a href="#">教师主页</a>
                        <a><cite>考勤情况</cite></a>
                    </span>
                </div>
            `);
            element.render('breadcrumb');//重新进行对面包屑的渲染
            
        });//end checkSignIn.onclick    
        
        /*布置作业*/
        $("#sendHomework").on("click", function () {
        	$(this).addClass("layui-this").siblings().removeClass("layui-this");//选中高亮
//        	window.location.reload();
            //页面内容替换并填充
            const CONTENT_BODY_DIV = $("#contentBody");
            CONTENT_BODY_DIV.empty();

            CONTENT_BODY_DIV.html(`
                <!-- 面包屑 -->
                <div id="breadcrumb">
                    <span class="layui-breadcrumb">
                        <a href="#">教师主页</a>
                        <a><cite>布置作业</cite></a>
                    </span>
                </div>
                <!-- 写字栏 -->
                <div id="editor"></div>
                </div>
                <button type="button" class="layui-btn" id="sendHomeworkAction" style="margin-top: 5px;">完成并提交</button>
                </div> 
            	<!-- 布置作业 -->
                <fieldset class="layui-elem-field layui-field-title">
            		<legend>需要上传的文件</legend>
        		</fieldset>
                <!-- 多文件拖拽上传 -->
                <div class="layui-upload">
                <div class="layui-upload-drag" id="upload">
                    <i class="layui-icon"></i>
                    <p>点击上传，或将文件拖拽到此处</p>
                </div>
                <div class="layui-upload-list">
                <table class="layui-table">
                  <thead>
                    <tr><th>文件名</th>
                    <th>大小</th>
                    <th>状态</th>
                    <th>操作</th>
                  	</tr>
              	  </thead>
                  <tbody id="uploadList"></tbody>
                </table>
                </div>
                <button type="button" class="layui-btn" id="uploadListAction">开始上传</button>
                </div> 

            `);

            element.render('breadcrumb');//重新进行对面包屑的渲染
            
            //多文件上传列表
            let encryptRoleId = localStorage.getItem("id");
            let uploadListView = $('#uploadList')
                ,uploadListIns = upload.render({
                elem: '#upload'
                ,url: 'http://localhost:8080/assistant/teacher/uploadFiles' //上传接口
                ,data:{
                    roleId: encryptRoleId
                }
                ,accept: 'file' //允许上传的文件类型,普通文件
                ,multiple: true
                ,auto: false
                ,bindAction: '#uploadListAction'
                ,choose: function(obj){
                    let files = this.files = obj.pushFile(); //将每次选择的文件追加到文件队列
                    //读取本地文件
                    obj.preview(function(index, file, result){
                        let tr = $(['<tr id="upload-'+ index +'">'
                            ,'<td>'+ file.name +'</td>'
                            ,'<td>'+ (file.size/1024).toFixed(1) +'kb</td>'
                            ,'<td>等待上传</td>'
                            ,'<td>'
                            ,'<button class="layui-btn layui-btn-xs upload-reload layui-hide">重传</button>'
                            ,'<button class="layui-btn layui-btn-xs layui-btn-danger upload-delete">删除</button>'
                            ,'</td>'
                            ,'</tr>'].join(''));

                        //单个重传
                        tr.find('.upload-reload').on('click', function(){
                            obj.upload(index, file);
                        });

                        //删除
                        tr.find('.upload-delete').on('click', function(){
                            delete files[index]; //删除对应的文件
                            tr.remove();
                            uploadListIns.config.elem.next()[0].value = ''; //清空 input file 值，以免删除后出现同名文件不可选
                        });

                        uploadListView.append(tr);
                    });
                }
                ,done: function(res, index, upload){
                    if(res.success){ //上传成功
                        let tr = uploadListView.find('tr#upload-'+ index)
                            ,tds = tr.children();
                        tds.eq(2).html('<span style="color: #5FB878;">上传成功</span>');
                        tds.eq(3).html(''); //清空操作
                        return delete this.files[index]; //删除文件队列已经上传成功的文件
                    }
                    this.error(index, upload);
                }
                ,error: function(index, upload){
                    let tr = uploadListView.find('tr#upload-' + index)
                        , tds = tr.children();
                    tds.eq(2).html('<span style="color: #FF5722;">上传失败</span>');
                    tds.eq(3).find('.demo-reload').removeClass('layui-hide'); //显示重传
                }
            });//end upload.render
        	
            //CKEditor文本编辑器
            let myEditor; //设置CKEditor的全局变量，方便数据调用
            ClassicEditor
	            .create( $('#editor')[0], {
	            	language: 'zh-cn',  // 中文
	            })
	            .then( editor => {
			        myEditor = editor;//赋给全局变量
			        // 设置初始值
			        myEditor.setData('');
			    })
	            .catch( error => {
	                console.error( error );
	            } );
            //提交数据
            $("#sendHomeworkAction").on("click", function () {
            	$.ajax({
                    url: "/assistant/teacher/sendHomework",
                    data: {
                   	 textData: myEditor.getData()
                    },
                    dataType: 'json',// 服务器返回json格式数据
                    type: 'post',
                    timeout: 10000,// 超时时间设置为10秒
                    success: function (data) {
                        if (data.success) {
                           
                        }// end if
                    },
                    error: function (xhr, type, errorThrown) {
                        console.log(xhr);
                        console.log(type);
                        console.log(errorThrown);
                    }
                });//end ajax
            });//end sendHomeworkAction.onclick
                  
        });//end sendHomework.onclick
        
        /*接收作业*/
        $("#getHomework").on("click", function () {
        	$(this).addClass("layui-this").siblings().removeClass("layui-this");//选中高亮

            //页面内容替换并填充
            const CONTENT_BODY_DIV = $("#contentBody");
            CONTENT_BODY_DIV.empty();

            CONTENT_BODY_DIV.html(`
                <!-- 面包屑 -->
                <div id="breadcrumb">
                    <span class="layui-breadcrumb">
                        <a href="#">教师主页</a>
                        <a><cite>接收作业</cite></a>
                    </span>
                </div>
                <table class="layui-hide" id="homeworkFiles" lay-filter="homeworkFiles"></table>
	            <script type="text/html" id="homeworkFilesRowBar">
	        		<a class="layui-btn layui-btn-xs layui-btn-xs" lay-event="download">下载</a>
	        		<a class="layui-btn layui-btn-xs layui-btn-xs" lay-event="open">在线打开</a>
	    		</script>
            `);

            element.render('breadcrumb');//重新进行对面包屑的渲染
        	
            //渲染表格
            table.render({
           	 elem: '#homeworkFiles'
       		 ,url:'http://localhost:8080/assistant/teacher/getFiles' //数据接口
   			 ,where: {
   				 sort: "2"
   			 }
        	 ,method: 'get'
   			 ,cellMinWidth: 80 //全局定义常规单元格的最小宽度，layui 2.2.1 新增
   			 ,cols: [[
   				 {field:'fileId', title: '文件ID'}
					 ,{field:'fileName', title: '文件名'} //width 支持：数字、百分比和不填写。你还可以通过 minWidth 参数局部定义当前单元格的最小宽度，layui 2.2.1 新增
					 ,{field:'fileSize', title: '文件大小',  align: 'center'}
					 ,{field:'roleName', title: '创建人', align: 'center'}
					 ,{field:'createTime', title: '创建时间', sort: true, align: 'center'} //单元格内容水平居中
					 ,{fixed: 'right', title: '操作', toolbar: '#homeworkFilesRowBar', align: 'center'} //行工具栏
				 ]]
            });//end render(homeworkFiles)
            //监听行点击事件
            table.on('row(homeworkFiles)', function (obj) {
                //标注选中行样式
                obj.tr.addClass("layui-table-click").siblings().removeClass("layui-table-click");
            });
            // 监听表格行工具栏时间
            table.on('tool(homeworkFiles)', function (obj) { //tool(lay-filter)
                let data = obj.data;
                if (obj.event === 'download') {
                	window.open(url="http://localhost:8080/assistant/static/"+data.fileName);
//               	 	$.ajax({
//                        url: "/assistant/static/"+data.fileName,
//                        data: {
//                       	 count: 1
//                        },
//                        dataType: 'json',// 服务器返回json格式数据
//                        type: 'get',// HTTP请求类型
//                        timeout: 10000,// 超时时间设置为10秒
//                        success: function (data) {
//                            if (data.success) {
//                               
//                            }// end if
//                        },
//                        error: function (xhr, type, errorThrown) {
//                            console.log(xhr);
//                            console.log(type);
//                            console.log(errorThrown);
//                        }
//                    });// end ajax
                }// end obj.event==='download'
            });// end table.on.tool(homeworkFiles)
        });//end getHomework.onclick
        
        /*课件下发*/
        $("#sendTeachingFiles").on("click", function () {
        	$(this).addClass("layui-this").siblings().removeClass("layui-this");//选中高亮

            //页面内容替换并填充
            const CONTENT_BODY_DIV = $("#contentBody");
            CONTENT_BODY_DIV.empty();

            CONTENT_BODY_DIV.html(`
                <!-- 面包屑 -->
                <div id="breadcrumb">
                    <span class="layui-breadcrumb">
                        <a href="#">教师主页</a>
                        <a><cite>课件下发</cite></a>
                    </span>
                </div>
                <!-- 多文件拖拽上传 -->
                <div class="layui-upload">
<!--                <button type="button" class="layui-btn layui-btn-normal" id="testList">选择多文件</button> -->
                <div class="layui-upload-drag" id="upload">
                    <i class="layui-icon"></i>
                    <p>点击上传，或将文件拖拽到此处</p>
                </div>
                <div class="layui-upload-list">
                <table class="layui-table">
                  <thead>
                    <tr><th>文件名</th>
                    <th>大小</th>
                    <th>状态</th>
                    <th>操作</th>
                  </tr></thead>
                  <tbody id="uploadList"></tbody>
                </table>
                </div>
                <button type="button" class="layui-btn" id="uploadListAction">开始上传</button>
                </div> 
            `);

            element.render('breadcrumb');//重新进行对面包屑的渲染
            
            //多文件上传列表
            let encryptRoleId = localStorage.getItem("id");
            let uploadListView = $('#uploadList')
                ,uploadListIns = upload.render({
                elem: '#upload'
                ,url: 'http://localhost:8080/assistant/student/uploadFiles' //上传接口
                ,data:{
                    roleId: encryptRoleId
                }
                ,accept: 'file' //允许上传的文件类型,普通文件
                ,multiple: true
                ,auto: false
                ,bindAction: '#uploadListAction'
                ,choose: function(obj){
                    let files = this.files = obj.pushFile(); //将每次选择的文件追加到文件队列
                    //读取本地文件
                    obj.preview(function(index, file, result){
                        let tr = $(['<tr id="upload-'+ index +'">'
                            ,'<td>'+ file.name +'</td>'
                            ,'<td>'+ (file.size/1024).toFixed(1) +'kb</td>'
                            ,'<td>等待上传</td>'
                            ,'<td>'
                            ,'<button class="layui-btn layui-btn-xs upload-reload layui-hide">重传</button>'
                            ,'<button class="layui-btn layui-btn-xs layui-btn-danger upload-delete">删除</button>'
                            ,'</td>'
                            ,'</tr>'].join(''));

                        //单个重传
                        tr.find('.upload-reload').on('click', function(){
                            obj.upload(index, file);
                        });

                        //删除
                        tr.find('.upload-delete').on('click', function(){
                            delete files[index]; //删除对应的文件
                            tr.remove();
                            uploadListIns.config.elem.next()[0].value = ''; //清空 input file 值，以免删除后出现同名文件不可选
                        });

                        uploadListView.append(tr);
                    });
                }
                ,done: function(res, index, upload){
                    if(res.success){ //上传成功
                        let tr = uploadListView.find('tr#upload-'+ index)
                            ,tds = tr.children();
                        tds.eq(2).html('<span style="color: #5FB878;">上传成功</span>');
                        tds.eq(3).html(''); //清空操作
                        return delete this.files[index]; //删除文件队列已经上传成功的文件
                    }
                    this.error(index, upload);
                }
                ,error: function(index, upload){
                    let tr = uploadListView.find('tr#upload-' + index)
                        , tds = tr.children();
                    tds.eq(2).html('<span style="color: #FF5722;">上传失败</span>');
                    tds.eq(3).find('.demo-reload').removeClass('layui-hide'); //显示重传
                }
            });//end upload.render
        });//end sendTeachingFiles.onclick   
        
        /*平时成绩*/
        $("#usualPerformance").on("click", function () {
        	$(this).addClass("layui-this").siblings().removeClass("layui-this");//选中高亮
            //页面内容替换并填充
            const CONTENT_BODY_DIV = $("#contentBody");
            CONTENT_BODY_DIV.empty();

            CONTENT_BODY_DIV.html(`
                <!-- 面包屑 -->
                <div id="breadcrumb">
                    <span class="layui-breadcrumb">
                        <a href="#">教师主页</a>
                        <a><cite>平时成绩</cite></a>
                    </span>
                </div>
            `);
            element.render('breadcrumb');//重新进行对面包屑的渲染
        });// end usualPerformance.onclick
            
        /*消息通知*/
        $("#sendMessage").on("click", function () {
        	$(this).addClass("layui-this").siblings().removeClass("layui-this");//选中高亮
            //页面内容替换并填充
            const CONTENT_BODY_DIV = $("#contentBody");
            CONTENT_BODY_DIV.empty();

            CONTENT_BODY_DIV.html(`
                <!-- 面包屑 -->
                <div id="breadcrumb">
                    <span class="layui-breadcrumb">
                        <a href="#">教师主页</a>
                        <a><cite>消息通知</cite></a>
                    </span>
                </div>
                <!-- 写字栏 -->
                <div id="editor"></div>
                </div>
                <button type="button" class="layui-btn" id="sendMessageAction" style="margin-top: 5px;">完成并提交</button>
                </div> 
            `);
            element.render('breadcrumb');//重新进行对面包屑的渲染
          //CKEditor文本编辑器
            let myEditor; //设置CKEditor的全局变量，方便数据调用
            ClassicEditor
	            .create( $('#editor')[0], {
	            	language: 'zh-cn',  // 中文
	            })
	            .then( editor => {
			        myEditor = editor;//赋给全局变量
			        // 设置初始值
			        myEditor.setData('');
			    })
	            .catch( error => {
	                console.error( error );
	            } );
            //提交数据
            $("#sendMessageAction").on("click", function () {
            	$.ajax({
                    url: "/assistant/teacher/sendMessage",
                    data: {
                   	 textData: myEditor.getData()
                    },
                    dataType: 'json',// 服务器返回json格式数据
                    type: 'post',
                    timeout: 10000,// 超时时间设置为10秒
                    success: function (data) {
                        if (data.success) {
                           
                        }// end if
                    },
                    error: function (xhr, type, errorThrown) {
                        console.log(xhr);
                        console.log(type);
                        console.log(errorThrown);
                    }
                });//end ajax
            });//end sendMessageAction.onclick
        });//end sendMessage.onclick
    });//end layui
});//end require