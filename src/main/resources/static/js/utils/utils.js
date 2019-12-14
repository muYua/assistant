/* 声明一个工具包模块 */
define({
	//判断是否按下回车
	isPressEnter: function() {
		return event.keyCode === 13;
	},

	//判断字符串是否为空
	isEmpty: function(obj) {
		//typeof obj == "undefined"，判断obj是否定义
		return typeof obj == "undefined" || obj == null || obj === "";
	},

	//判断日期是否格式化输入
	isFormatDate: function(str) {
		const format = /^(1|2)9[\d]{2}-(0\d|1[0-2])-([012]\d|3[01])$/;
		return format.test(str);
	},

	//判断手机号合法性
	isPhoneNumber: function(str) {
		const phone = /^1[3-9]\d{9}$/;
		return phone.test(str);
	},

	//判断电子邮件合法性
	isEmail: function(str) {
		const email = /^[a-zA-Z\d](\w|\-|\+)+@(qq|163|126|yeah|gmail|outlook|hotmail|live|aliyun)\.(com|net)$/;
		return email.test(str);
	},

	//判断密码合法性
	isPassword: function(str) {
		const pswd = /^[\S]{6,16}$/;
		return pswd.test(str);
	}
});
